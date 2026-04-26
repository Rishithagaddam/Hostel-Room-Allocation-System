package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RoomsServlet extends HttpServlet {
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
            String block = request.getParameter("block");
            String floor = request.getParameter("floor");

            if (block == null || floor == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Missing block or floor parameter\"}");
                return;
            }

            List<Map<String, Object>> rooms = xmlManager.getRoomsByBlockAndFloor(block, floor);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < rooms.size(); i++) {
                Map<String, Object> room = rooms.get(i);
                json.append("{");
                json.append("\"roomNo\":\"").append(escapeJson(room.get("roomNo").toString())).append("\",");
                json.append("\"roomId\":\"").append(escapeJson(room.get("roomId").toString())).append("\",");

                @SuppressWarnings("unchecked")
                List<Map<String, String>> beds = (List<Map<String, String>>) room.get("beds");
                json.append("\"beds\":[");
                for (int j = 0; j < beds.size(); j++) {
                    Map<String, String> bed = beds.get(j);
                    json.append("{");
                    json.append("\"bedNo\":\"").append(escapeJson(bed.get("bedNo"))).append("\",");
                    json.append("\"bedId\":\"").append(escapeJson(bed.get("bedId"))).append("\",");
                    json.append("\"status\":\"").append(escapeJson(bed.get("status") != null ? bed.get("status").toUpperCase() : "AVAILABLE")).append("\",");
                    json.append("\"rollNumber\":\"").append(escapeJson(bed.get("rollNo") != null ? bed.get("rollNo") : "")).append("\"");
                    json.append("}");

                    if (j < beds.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                json.append("}");

                if (i < rooms.size() - 1) {
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