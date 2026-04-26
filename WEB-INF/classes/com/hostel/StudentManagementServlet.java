package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class StudentManagementServlet extends HttpServlet {
    private XMLManager xmlManager;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        System.out.println("DEBUG INIT: appPath = " + appPath);
        xmlManager = new XMLManager(appPath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check authorization - only warden can manage students
        HttpSession session = request.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        if (!"warden".equals(role)) {
            sendJsonError(response, "UNAUTHORIZED", "Only wardens can manage students");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("update".equals(action)) {
                handleUpdate(request, response);
            } else if ("delete".equals(action)) {
                handleDelete(request, response);
            } else if ("search".equals(action)) {
                handleSearch(request, response);
            } else {
                sendJsonError(response, "INVALID_ACTION", "Unknown action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "ERROR", "Server error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Check authorization - only warden can manage students
        HttpSession session = request.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        if (!"warden".equals(role)) {
            sendJsonError(response, "UNAUTHORIZED", "Only wardens can manage students");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("search".equals(action)) {
                handleSearch(request, response);
            } else if ("list".equals(action)) {
                handleList(request, response);
            } else {
                sendJsonError(response, "INVALID_ACTION", "Unknown action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "ERROR", "Server error: " + e.getMessage());
        }
    }

    /**
     * Handle student update - updates name, email, year
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String studentId = request.getParameter("studentId");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String year = request.getParameter("year");

        // Validation
        if (studentId == null || studentId.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Student ID is required");
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Name is required");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Email is required");
            return;
        }

        if (!XMLManager.isValidEmail(email)) {
            sendJsonError(response, "INVALID_EMAIL", "Invalid email format");
            return;
        }

        if (year == null || year.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Year is required");
            return;
        }

        try {
            int yearInt = Integer.parseInt(year);
            if (yearInt < 1 || yearInt > 4) {
                sendJsonError(response, "INVALID_YEAR", "Year must be between 1 and 4");
                return;
            }
        } catch (NumberFormatException e) {
            sendJsonError(response, "INVALID_YEAR", "Year must be a number");
            return;
        }

        // Verify student exists
        Map<String, String> student = xmlManager.getStudentById(studentId);
        if (student == null) {
            sendJsonError(response, "STUDENT_NOT_FOUND", "Student not found with ID: " + studentId);
            return;
        }

        // Update student
        boolean updated = xmlManager.updateStudent(studentId, name.trim(), email.trim(), year.trim());

        if (updated) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Student updated successfully");
            responseData.put("studentId", studentId);
            sendJsonResponse(response, responseData);
        } else {
            sendJsonError(response, "UPDATE_FAILED", "Failed to update student");
        }
    }

    /**
     * Handle student deletion
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String studentId = request.getParameter("studentId");

        if (studentId == null || studentId.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Student ID is required");
            return;
        }

        // Verify student exists
        Map<String, String> student = xmlManager.getStudentById(studentId);
        if (student == null) {
            sendJsonError(response, "STUDENT_NOT_FOUND", "Student not found with ID: " + studentId);
            return;
        }

        // Prevent deletion of warden account
        String role = student.get("role");
        if ("warden".equals(role)) {
            sendJsonError(response, "CANNOT_DELETE_WARDEN", "Cannot delete warden account");
            return;
        }

        // Delete student (will also release bed if allocated)
        boolean deleted = xmlManager.deleteStudent(studentId);

        if (deleted) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Student deleted successfully");
            responseData.put("studentId", studentId);
            sendJsonResponse(response, responseData);
        } else {
            sendJsonError(response, "DELETE_FAILED", "Failed to delete student");
        }
    }

    /**
     * Handle student search by name or year
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String searchTerm = request.getParameter("search");

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "Search term is required");
            return;
        }

        List<Map<String, String>> results = xmlManager.searchStudents(searchTerm.trim());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("count", results.size());
        responseData.put("students", results);
        sendJsonResponse(response, responseData);
    }

    /**
     * Handle list all students
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            List<Map<String, String>> allStudents = xmlManager.getAllStudents();
            System.out.println("DEBUG: Total students fetched: " + (allStudents != null ? allStudents.size() : 0));

            // Filter out warden account
            if (allStudents != null) {
                if (allStudents.size() > 0) {
                    System.out.println("DEBUG: First student before filtering: " + allStudents.get(0));
                }
                allStudents.removeIf(s -> "warden".equals(s.get("role")));
            } else {
                allStudents = new ArrayList<>();
            }
            System.out.println("DEBUG: Students after filtering wardens: " + allStudents.size());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("count", allStudents.size());

            JSONArray studentsArray = new JSONArray();
            for (Map<String, String> student : allStudents) {
                System.out.println("DEBUG: Converting student to JSON: name=" + student.get("name") + ", rollNumber=" + student.get("rollNumber"));
                JSONObject studentJson = new JSONObject(student);
                studentsArray.put(studentJson);
            }
            json.put("students", studentsArray);

            String responseJson = json.toString();
            System.out.println("DEBUG: Final JSON response: " + responseJson.substring(0, Math.min(500, responseJson.length())));
            response.getWriter().write(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in handleList: " + e.getMessage());
            sendJsonError(response, "ERROR", "Failed to load students: " + e.getMessage());
        }
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject json = new JSONObject(data);
        response.getWriter().write(json.toString());
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, String errorCode, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(400);

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("errorCode", errorCode);
        error.put("message", message);

        JSONObject json = new JSONObject(error);
        response.getWriter().write(json.toString());
    }
}
