package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AllocatedStudentsServlet extends HttpServlet {
    private XMLManager xmlManager;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        xmlManager = new XMLManager(appPath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Map<String, String>> students = xmlManager.getAllocatedStudents();

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < students.size(); i++) {
                Map<String, String> student = students.get(i);
                json.append("{");
                json.append("\"studentId\":\"").append(escapeJson(student.get("studentId"))).append("\",");
                json.append("\"name\":\"").append(escapeJson(student.get("name"))).append("\",");
                json.append("\"rollNumber\":\"").append(escapeJson(student.get("rollNumber"))).append("\",");
                json.append("\"year\":\"").append(escapeJson(student.get("year"))).append("\",");
                json.append("\"email\":\"").append(escapeJson(student.get("email"))).append("\",");
                json.append("\"block\":\"").append(escapeJson(student.get("block"))).append("\",");
                json.append("\"floor\":\"").append(escapeJson(student.get("floor"))).append("\",");
                json.append("\"roomNo\":\"").append(escapeJson(student.get("roomNo"))).append("\",");
                json.append("\"bedNo\":\"").append(escapeJson(student.get("bedNo"))).append("\"");
                json.append("}");

                if (i < students.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            response.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}