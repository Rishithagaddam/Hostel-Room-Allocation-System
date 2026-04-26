package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HostelStructureServlet extends HttpServlet {
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

        String action = request.getParameter("action");

        try {
            if ("getBlocks".equals(action)) {
                handleGetBlocks(response);
            } else if ("getFloors".equals(action)) {
                handleGetFloors(request, response);
            } else if ("getRooms".equals(action)) {
                handleGetRooms(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid action\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            boolean success = false;
            String message = "";

            if ("addBlock".equals(action)) {
                String blockName = request.getParameter("blockName");
                if (blockName != null && !blockName.trim().isEmpty()) {
                    success = xmlManager.addBlock(blockName.trim());
                    message = success ? "Block added successfully" : "Block already exists or failed to add";
                } else {
                    message = "Invalid block name";
                }
            } else if ("removeBlock".equals(action)) {
                String blockName = request.getParameter("blockName");
                if (blockName != null && !blockName.trim().isEmpty()) {
                    success = xmlManager.removeBlock(blockName.trim());
                    message = success ? "Block removed successfully" : "Cannot remove block with occupied beds or block not found";
                } else {
                    message = "Invalid block name";
                }
            } else if ("addFloor".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                if (blockName != null && floorNumber != null) {
                    success = xmlManager.addFloor(blockName.trim(), floorNumber.trim());
                    message = success ? "Floor added successfully" : "Floor already exists or failed to add";
                } else {
                    message = "Invalid parameters";
                }
            } else if ("removeFloor".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                if (blockName != null && floorNumber != null) {
                    success = xmlManager.removeFloor(blockName.trim(), floorNumber.trim());
                    message = success ? "Floor removed successfully" : "Cannot remove floor with occupied beds or floor not found";
                } else {
                    message = "Invalid parameters";
                }
            } else if ("addRoom".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                String roomNumber = request.getParameter("roomNumber");
                if (blockName != null && floorNumber != null && roomNumber != null) {
                    success = xmlManager.addRoom(blockName.trim(), floorNumber.trim(), roomNumber.trim());
                    message = success ? "Room added successfully" : "Room already exists or failed to add";
                } else {
                    message = "Invalid parameters";
                }
            } else if ("removeRoom".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                String roomNumber = request.getParameter("roomNumber");
                if (blockName != null && floorNumber != null && roomNumber != null) {
                    success = xmlManager.removeRoom(blockName.trim(), floorNumber.trim(), roomNumber.trim());
                    message = success ? "Room removed successfully" : "Cannot remove room with occupied beds or room not found";
                } else {
                    message = "Invalid parameters";
                }
            } else if ("addBed".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                String roomNumber = request.getParameter("roomNumber");
                String bedNumber = request.getParameter("bedNumber");
                if (blockName != null && !blockName.trim().isEmpty() && 
                    floorNumber != null && !floorNumber.trim().isEmpty() && 
                    roomNumber != null && !roomNumber.trim().isEmpty() && 
                    bedNumber != null && !bedNumber.trim().isEmpty()) {
                    success = xmlManager.addBed(blockName.trim(), floorNumber.trim(), roomNumber.trim(), bedNumber.trim());
                    message = success ? "Bed added successfully" : "Bed already exists or failed to add";
                } else {
                    message = "Invalid parameters - all fields are required";
                }
            } else if ("removeBed".equals(action)) {
                String blockName = request.getParameter("blockName");
                String floorNumber = request.getParameter("floorNumber");
                String roomNumber = request.getParameter("roomNumber");
                String bedNumber = request.getParameter("bedNumber");
                if (blockName != null && !blockName.trim().isEmpty() && 
                    floorNumber != null && !floorNumber.trim().isEmpty() && 
                    roomNumber != null && !roomNumber.trim().isEmpty() && 
                    bedNumber != null && !bedNumber.trim().isEmpty()) {
                    success = xmlManager.removeBed(blockName.trim(), floorNumber.trim(), roomNumber.trim(), bedNumber.trim());
                    message = success ? "Bed removed successfully" : "Cannot remove occupied bed or bed not found";
                } else {
                    message = "Invalid parameters - all fields are required";
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid action\"}");
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", message);
            response.getWriter().write(toJson(result));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetBlocks(HttpServletResponse response) throws IOException {
        List<String> blocks = xmlManager.getAllBlocks();
        StringBuilder json = new StringBuilder("{\"blocks\":[");
        for (int i = 0; i < blocks.size(); i++) {
            json.append("\"").append(escapeJson(blocks.get(i))).append("\"");
            if (i < blocks.size() - 1) json.append(",");
        }
        json.append("]}");
        response.getWriter().write(json.toString());
    }

    private void handleGetFloors(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String block = request.getParameter("blockName");
        if (block == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing blockName parameter\"}");
            return;
        }

        List<String> floors = xmlManager.getFloorsByBlock(block);
        StringBuilder json = new StringBuilder("{\"floors\":[");
        for (int i = 0; i < floors.size(); i++) {
            json.append("\"").append(escapeJson(floors.get(i))).append("\"");
            if (i < floors.size() - 1) json.append(",");
        }
        json.append("]}");
        response.getWriter().write(json.toString());
    }

    private void handleGetRooms(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String block = request.getParameter("blockName");
        String floor = request.getParameter("floorNumber");

        if (block == null || floor == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Missing blockName or floorNumber parameter\"}");
            return;
        }

        List<Map<String, Object>> rooms = xmlManager.getRoomsByBlockAndFloor(block, floor);
        StringBuilder json = new StringBuilder("{\"rooms\":[");
        for (int i = 0; i < rooms.size(); i++) {
            Map<String, Object> room = rooms.get(i);
            json.append("{");
            json.append("\"room_number\":\"").append(escapeJson(room.get("roomNo").toString())).append("\",");
            json.append("\"room_id\":\"").append(escapeJson(room.get("roomId").toString())).append("\",");
            json.append("\"beds\":[");
            @SuppressWarnings("unchecked")
            List<Map<String, String>> beds = (List<Map<String, String>>) room.get("beds");
            for (int j = 0; j < beds.size(); j++) {
                Map<String, String> bed = beds.get(j);
                json.append("{");
                json.append("\"bed_number\":\"").append(escapeJson(bed.get("bedNo"))).append("\",");
                json.append("\"bed_id\":\"").append(escapeJson(bed.get("bedId"))).append("\",");
                json.append("\"status\":\"").append(escapeJson(bed.get("status"))).append("\",");
                json.append("\"rollNo\":\"").append(escapeJson(bed.get("rollNo"))).append("\"");
                json.append("}");
                if (j < beds.size() - 1) json.append(",");
            }
            json.append("]");
            json.append("}");
            if (i < rooms.size() - 1) json.append(",");
        }
        json.append("]}");
        response.getWriter().write(json.toString());
    }

    private void handleStructureModification(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");

        boolean success = false;
        String message = "";

        if ("addBlock".equals(action)) {
            String blockName = request.getParameter("blockName");
            if (blockName != null && !blockName.trim().isEmpty()) {
                success = xmlManager.addBlock(blockName.trim());
                message = success ? "Block added successfully" : "Block already exists or failed to add";
            } else {
                message = "Invalid block name";
            }
        } else if ("removeBlock".equals(action)) {
            String blockName = request.getParameter("blockName");
            if (blockName != null && !blockName.trim().isEmpty()) {
                success = xmlManager.removeBlock(blockName.trim());
                message = success ? "Block removed successfully" : "Cannot remove block with occupied beds or block not found";
            } else {
                message = "Invalid block name";
            }
        } else if ("addFloor".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            if (blockName != null && floorNumber != null) {
                success = xmlManager.addFloor(blockName.trim(), floorNumber.trim());
                message = success ? "Floor added successfully" : "Floor already exists or failed to add";
            } else {
                message = "Invalid parameters";
            }
        } else if ("removeFloor".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            if (blockName != null && floorNumber != null) {
                success = xmlManager.removeFloor(blockName.trim(), floorNumber.trim());
                message = success ? "Floor removed successfully" : "Cannot remove floor with occupied beds or floor not found";
            } else {
                message = "Invalid parameters";
            }
        } else if ("addRoom".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            String roomNumber = request.getParameter("roomNumber");
            if (blockName != null && floorNumber != null && roomNumber != null) {
                success = xmlManager.addRoom(blockName.trim(), floorNumber.trim(), roomNumber.trim());
                message = success ? "Room added successfully" : "Room already exists or failed to add";
            } else {
                message = "Invalid parameters";
            }
        } else if ("removeRoom".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            String roomNumber = request.getParameter("roomNumber");
            if (blockName != null && floorNumber != null && roomNumber != null) {
                success = xmlManager.removeRoom(blockName.trim(), floorNumber.trim(), roomNumber.trim());
                message = success ? "Room removed successfully" : "Cannot remove room with occupied beds or room not found";
            } else {
                message = "Invalid parameters";
            }
        } else if ("addBed".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            String roomNumber = request.getParameter("roomNumber");
            String bedNumber = request.getParameter("bedNumber");
            if (blockName != null && floorNumber != null && roomNumber != null && bedNumber != null) {
                success = xmlManager.addBed(blockName.trim(), floorNumber.trim(), roomNumber.trim(), bedNumber.trim());
                message = success ? "Bed added successfully" : "Bed already exists or failed to add";
            } else {
                message = "Invalid parameters";
            }
        } else if ("removeBed".equals(action)) {
            String blockName = request.getParameter("blockName");
            String floorNumber = request.getParameter("floorNumber");
            String roomNumber = request.getParameter("roomNumber");
            String bedNumber = request.getParameter("bedNumber");
            if (blockName != null && floorNumber != null && roomNumber != null && bedNumber != null) {
                success = xmlManager.removeBed(blockName.trim(), floorNumber.trim(), roomNumber.trim(), bedNumber.trim());
                message = success ? "Bed removed successfully" : "Cannot remove occupied bed or bed not found";
            } else {
                message = "Invalid parameters";
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", message);
        response.getWriter().write(toJson(result));
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String toJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.append("\"").append(escapeJson(entry.getKey())).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Boolean) {
                json.append(value.toString());
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
            if (i < map.size() - 1) json.append(",");
            i++;
        }
        json.append("}");
        return json.toString();
    }
}