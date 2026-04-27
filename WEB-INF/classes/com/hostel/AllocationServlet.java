package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class AllocationServlet extends HttpServlet {
    private XMLManager xmlManager;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        xmlManager = new XMLManager(appPath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DEBUG: AllocationServlet doPost called");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            String line;

            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            System.out.println("DEBUG: Received JSON: " + jsonString);

            org.json.JSONObject json = new org.json.JSONObject(jsonString);

            String rollNumber = json.getString("rollNumber");
            String block = json.getString("block");
            String floor = json.getString("floor");
            String roomNo = json.getString("roomNo");
            String bedNo = json.getString("bedNo");

            System.out.println("ALLOC DATA => rollNumber=" + rollNumber +
                    ", block=" + block +
                    ", floor=" + floor +
                    ", roomNo=" + roomNo +
                    ", bedNo=" + bedNo);

            // STEP 1
            System.out.println("DEBUG: About to call allocateRoom");
            boolean allocated = xmlManager.allocateRoom(
                    rollNumber, block, floor, roomNo, bedNo
            );
            System.out.println("STEP1 allocateRoom done: " + allocated);

            if (!allocated) {
                response.getWriter().write("{\"success\":false,\"message\":\"Allocation failed\"}");
                return;
            }

            // STEP 2
            System.out.println("DEBUG: About to call updateStudentAllocationStatus");
            xmlManager.updateStudentAllocationStatus(
                    rollNumber, "ALLOCATED", block, floor, roomNo, bedNo
            );
            System.out.println("STEP2 updateStudentAllocationStatus done");

            // STEP 3
            System.out.println("DEBUG: About to call getStudentByRollNo");
            Map<String,String> student =
                    xmlManager.getStudentByRollNo(rollNumber);
            System.out.println("STEP3 student fetched: " + (student != null ? "found" : "null"));

            // STEP 4 EMAIL
            boolean emailSent = false;
            if(student != null){
                String plainPassword = student.get("plain_password");
                if (plainPassword == null || plainPassword.trim().isEmpty()) {
                    System.out.println("DEBUG: Plain password not available for student: " + rollNumber + ", skipping email");
                    emailSent = false;
                } else {
                    System.out.println("DEBUG: About to send email to: " + student.get("email"));
                    emailSent = EmailService.sendAllocationEmail(
                        student.get("name"),
                        student.get("email"),
                        rollNumber,
                        plainPassword,
                        block,
                        floor,
                        roomNo,
                        bedNo
                    );
                    System.out.println("DEBUG: Email sent result: " + emailSent);
                }
            } else {
                System.out.println("DEBUG: Student not found, skipping email");
            }
            System.out.println("STEP4 email done");

            // Prepare success response
            String successMessage = emailSent ?
                "Room allocated successfully" :
                "Room allocated successfully but email could not be sent";

            String jsonResponse = "{\"success\":true,\"message\":\"" + successMessage + "\"}";
            System.out.println("DEBUG: About to send response: " + jsonResponse);

            try {
                if (!response.isCommitted()) {
                    response.getWriter().write(jsonResponse);
                    response.getWriter().flush();
                    System.out.println("DEBUG: Response sent successfully");
                } else {
                    System.out.println("ERROR: Response already committed!");
                }
            } catch (Exception e) {
                System.err.println("ERROR: Failed to write response: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in AllocationServlet: " + e.getMessage());
            e.printStackTrace();

            // Try to send error response
            try {
                String errorMessage = e.getMessage() != null ? e.getMessage().replace("\"","").replace("\\","") : "Unknown error";
                String errorResponse = "{\"success\":false,\"message\":\"" + errorMessage + "\"}";

                if (!response.isCommitted()) {
                    response.getWriter().write(errorResponse);
                    response.getWriter().flush();
                    System.out.println("DEBUG: Error response sent successfully");
                } else {
                    System.out.println("ERROR: Cannot send error response - response already committed!");
                }
            } catch (Exception e2) {
                System.err.println("CRITICAL: Failed to send error response: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    /**
     * Handle normal room allocation for unallocated students
     */
    private void handleAllocate(HttpServletRequest request, HttpServletResponse response, org.json.JSONObject json)
            throws IOException {
        String rollNumber = json.optString("rollNumber", "").trim();
        String block = json.optString("block", "").trim();
        String floor = json.optString("floor", "").trim();
        String roomNo = json.optString("roomNo", "").trim();
        String bedNo = json.optString("bedNo", "").trim();

        System.out.println("ALLOC DATA => rollNumber=" + rollNumber +
                ", block=" + block +
                ", floor=" + floor +
                ", roomNo=" + roomNo +
                ", bedNo=" + bedNo);

        if (rollNumber.isEmpty() || block.isEmpty() || floor.isEmpty() || roomNo.isEmpty() || bedNo.isEmpty()) {
            sendJsonError(response, "Missing required parameters: " +
                    "rollNumber=" + rollNumber +
                    ", block=" + block +
                    ", floor=" + floor +
                    ", roomNo=" + roomNo +
                    ", bedNo=" + bedNo);
            return;
        }

        // Check if student is already allocated
        Map<String, String> studentData = xmlManager.getStudentByRollNo(rollNumber);
        if (studentData == null) {
            sendJsonError(response, "Student not found");
            return;
        }

        String allocationStatus = studentData.get("allocation_status");
        if ("ALLOCATED".equalsIgnoreCase(allocationStatus)) {
            sendJsonError(response, "Student is already allocated");
            return;
        }

        // Allocate the room
        boolean allocated = xmlManager.allocateRoom(rollNumber, block, floor, roomNo, bedNo);

        if (allocated) {
            // Update student status in students.xml
            boolean statusUpdated = xmlManager.updateStudentAllocationStatus(rollNumber, "ALLOCATED", block, floor, roomNo, bedNo);

            if (!statusUpdated) {
                sendJsonError(response, "Room allocated but student status not updated");
                return;
            }

            // Get student info for email
            Map<String, String> student = xmlManager.getStudentByRollNo(rollNumber);
            System.out.println("DEBUG: Student lookup for rollNumber: " + rollNumber);
            System.out.println("DEBUG: Student data: " + student);

            boolean emailSent = false;

            if (student != null) {
                String studentEmail = student.get("email");
                String studentName = student.get("name");
                String plainPassword = student.get("plain_password");

                if (plainPassword == null || plainPassword.trim().isEmpty()) {
                    System.out.println("DEBUG: Plain password not available for student: " + rollNumber + ", skipping email");
                    emailSent = false;
                } else {
                    System.out.println("DEBUG: Attempting to send email to: " + studentEmail + " for student: " + studentName);
                    emailSent = EmailService.sendAllocationEmail(
                        studentName,
                        studentEmail,
                        rollNumber,
                        plainPassword,
                        block,
                        floor,
                        roomNo,
                        bedNo
                    );
                    System.out.println("DEBUG: Email sent result: " + emailSent);
                }
            } else {
                System.out.println("DEBUG: Student not found for rollNumber: " + rollNumber);
            }

            if (emailSent) {
                sendJsonSuccess(response, "Room allocated successfully and confirmation email sent");
            } else {
                sendJsonSuccess(response, "Room allocated successfully, but email could not be sent");
            }
        } else {
            sendJsonError(response, "Failed to allocate room - bed may already be occupied");
        }
    }

    /**
     * Handle manual bed reassignment for already allocated students
     */
    private void handleReassign(HttpServletRequest request, HttpServletResponse response, org.json.JSONObject json)
            throws IOException {
        // Check authorization - only warden can reassign
        HttpSession session = request.getSession(false);
        if (session == null || !"warden".equals(session.getAttribute("role"))) {
            sendJsonError(response, "Only wardens can reassign beds");
            return;
        }

        String studentId = json.optString("studentId", "").trim();
        String block = json.optString("block", "").trim();
        String floor = json.optString("floor", "").trim();
        String roomNo = json.optString("roomNo", "").trim();
        String bedNo = json.optString("bedNo", "").trim();

        if (studentId.isEmpty() || block.isEmpty() || floor.isEmpty() || roomNo.isEmpty() || bedNo.isEmpty()) {
            sendJsonError(response, "Missing required parameters for reassignment");
            return;
        }

        // Use AllocationEngine to reassign
        AllocationEngine engine = new AllocationEngine(xmlManager);
        Map<String, Object> result = engine.reassignBed(studentId, block, floor, roomNo, bedNo);

        if ((boolean) result.get("success")) {
            response.getWriter().write("{\"success\":true,\"message\":\"" + escapeJson((String) result.get("message")) + "\"}");
        } else {
            sendJsonError(response, (String) result.get("message"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Optionally handle GET requests for status checks
        response.setContentType("application/json;charset=UTF-8");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not supported");
    }

    private void sendJsonSuccess(HttpServletResponse response, String message) throws IOException {
        response.getWriter().write("{\"success\":true,\"message\":\"" + escapeJson(message) + "\"}");
    }

    private void sendJsonError(HttpServletResponse response, String message) throws IOException {
        response.getWriter().write("{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}");
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

