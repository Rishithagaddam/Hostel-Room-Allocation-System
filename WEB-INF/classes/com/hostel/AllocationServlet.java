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
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read JSON from request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            java.io.BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            String jsonString = jsonBuffer.toString();

            // Parse JSON
            org.json.JSONObject json = new org.json.JSONObject(jsonString);
            String action = json.optString("action", "allocate").trim();

            if ("reassign".equals(action)) {
                handleReassign(request, response, json);
            } else {
                handleAllocate(request, response, json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Error: " + e.getMessage());
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
            String studentName = rollNumber;
            String email = "";
            if (student != null) {
                studentName = student.get("name");
                email = student.get("email");
            }

            // Send allocation email
            EmailService.sendAllocationEmail(studentName, email, rollNumber, block, floor, roomNo, bedNo);

            sendJsonSuccess(response, "Room allocated successfully");
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

