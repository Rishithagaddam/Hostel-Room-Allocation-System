package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

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

        try {
            String action = request.getParameter("action");

            if ("allocate_bed".equals(action)) {
                allocateBed(request, response);
            } else {
                sendJsonError(response, "Invalid action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            String action = request.getParameter("action");

            if ("get_students".equals(action)) {
                getRegisteredStudents(request, response);
            } else {
                sendJsonError(response, "Missing or invalid action parameter");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Server error: " + e.getMessage());
        }
    }

    private void allocateBed(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String studentId = request.getParameter("student_id");
            String rollNumber = request.getParameter("roll_no");
            String bedId = request.getParameter("bed_id");
            String roomId = request.getParameter("room_id");
            String block = request.getParameter("block");
            String floor = request.getParameter("floor");
            String room = request.getParameter("room");
            String bed = request.getParameter("bed");

            if (studentId == null || bedId == null || roomId == null) {
                sendJsonError(response, "Missing required parameters");
                return;
            }

            // Allocate the bed
            boolean allocated = xmlManager.allocateBed(studentId, roomId, bedId);

            if (allocated) {
                // Update student allocation status
                xmlManager.updateAllocationStatus(studentId, "ALLOCATED");

                // Get student info for email
                Map<String, String> student = xmlManager.getStudentById(studentId);
                if (student != null) {
                    // Send allocation email
                    String email = student.get("email");
                    String name = student.get("name");
                    EmailService.sendAllocationEmail(name, email, rollNumber, block, floor, room, bed);
                }

                sendJsonSuccess(response, "Room allocated successfully");
            } else {
                sendJsonError(response, "Failed to allocate bed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "Error: " + e.getMessage());
        }
    }

    private void getRegisteredStudents(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            List<Map<String, String>> students = xmlManager.getUnallocatedStudents();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < students.size(); i++) {
                Map<String, String> student = students.get(i);
                json.append("{");
                json.append("\"student_id\":\"").append(escapeJson(student.get("student_id"))).append("\",");
                json.append("\"name\":\"").append(escapeJson(student.get("name"))).append("\",");
                json.append("\"roll_no\":\"").append(escapeJson(student.getOrDefault("roll_number", "N/A"))).append("\",");
                json.append("\"year\":\"").append(escapeJson(student.get("year"))).append("\",");
                json.append("\"email\":\"").append(escapeJson(student.get("email"))).append("\"");
                json.append("}");

                if (i < students.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("[]");
        }
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

