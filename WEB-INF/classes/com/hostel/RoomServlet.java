package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class RoomServlet extends HttpServlet {
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
        // Check authorization - only logged-in users can view rooms
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("get_rooms".equals(action)) {
                handleGetRooms(request, response);
            } else if ("get_room_detail".equals(action)) {
                handleGetRoomDetail(request, response);
            } else if ("get_stats".equals(action)) {
                handleGetStats(request, response);
            } else {
                sendJsonError(response, "INVALID_ACTION", "Unknown action");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, "ERROR", "Server error: " + e.getMessage());
        }
    }

    /**
     * Get all rooms with bed status grouped by block and floor
     */
    private void handleGetRooms(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String blockFilter = request.getParameter("block");
        String floorFilter = request.getParameter("floor");

        List<String> blocks = xmlManager.getAllBlocks();
        JSONArray blocksArray = new JSONArray();

        for (String blockName : blocks) {
            // Filter by block if specified
            if (blockFilter != null && !blockFilter.isEmpty() && !blockFilter.equals(blockName)) {
                continue;
            }

            JSONObject blockObj = new JSONObject();
            blockObj.put("blockName", blockName);

            List<String> floors = xmlManager.getFloorsByBlock(blockName);
            JSONArray floorsArray = new JSONArray();

            for (String floorNo : floors) {
                // Filter by floor if specified
                if (floorFilter != null && !floorFilter.isEmpty() && !floorFilter.equals(floorNo)) {
                    continue;
                }

                JSONObject floorObj = new JSONObject();
                floorObj.put("floorNo", floorNo);

                List<Map<String, Object>> rooms = xmlManager.getRoomsByBlockAndFloor(blockName, floorNo);
                JSONArray roomsArray = new JSONArray();

                for (Map<String, Object> roomData : rooms) {
                    JSONObject roomObj = new JSONObject();
                    roomObj.put("roomId", roomData.get("roomId"));
                    roomObj.put("roomNo", roomData.get("roomNo"));

                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> beds = (List<Map<String, String>>) roomData.get("beds");
                    int occupiedCount = 0;

                    JSONArray bedsArray = new JSONArray();
                    for (Map<String, String> bedData : beds) {
                        JSONObject bedObj = new JSONObject();
                        bedObj.put("bedNo", bedData.get("bedNo"));
                        bedObj.put("bedId", bedData.get("bedId"));
                        bedObj.put("status", bedData.get("status"));
                        bedObj.put("rollNo", bedData.get("rollNo"));

                        if ("occupied".equalsIgnoreCase(bedData.get("status"))) {
                            occupiedCount++;
                        }

                        bedsArray.put(bedObj);
                    }

                    roomObj.put("beds", bedsArray);
                    roomObj.put("totalBeds", beds.size());
                    roomObj.put("occupiedBeds", occupiedCount);
                    roomObj.put("availableBeds", beds.size() - occupiedCount);
                    roomObj.put("occupancyPercentage", (occupiedCount * 100) / beds.size());

                    roomsArray.put(roomObj);
                }

                floorObj.put("rooms", roomsArray);
                floorsArray.put(floorObj);
            }

            blockObj.put("floors", floorsArray);
            blocksArray.put(blockObj);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("blocks", blocksArray);

        response.getWriter().write(result.toString());
    }

    /**
     * Get detailed information for a specific room
     */
    private void handleGetRoomDetail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String roomId = request.getParameter("roomId");

        if (roomId == null || roomId.isEmpty()) {
            sendJsonError(response, "MISSING_FIELD", "roomId is required");
            return;
        }

        // Parse room ID format: "A-1-1" (block-floor-room)
        String[] parts = roomId.split("-");
        if (parts.length != 3) {
            sendJsonError(response, "INVALID_ROOMID", "Invalid room ID format");
            return;
        }

        String block = parts[0];
        String floor = parts[1];
        String room = parts[2];

        List<Map<String, Object>> rooms = xmlManager.getRoomsByBlockAndFloor(block, floor);
        for (Map<String, Object> roomData : rooms) {
            if (roomId.equals(roomData.get("roomId"))) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                JSONObject roomObj = new JSONObject();
                roomObj.put("roomId", roomData.get("roomId"));
                roomObj.put("roomNo", roomData.get("roomNo"));

                @SuppressWarnings("unchecked")
                List<Map<String, String>> beds = (List<Map<String, String>>) roomData.get("beds");
                JSONArray bedsArray = new JSONArray();

                int occupiedCount = 0;
                for (Map<String, String> bedData : beds) {
                    JSONObject bedObj = new JSONObject();
                    bedObj.put("bedNo", bedData.get("bedNo"));
                    bedObj.put("bedId", bedData.get("bedId"));
                    bedObj.put("status", bedData.get("status"));
                    bedObj.put("rollNo", bedData.get("rollNo"));
                    bedsArray.put(bedObj);

                    if ("occupied".equalsIgnoreCase(bedData.get("status"))) {
                        occupiedCount++;
                    }
                }

                roomObj.put("beds", bedsArray);
                roomObj.put("totalBeds", beds.size());
                roomObj.put("occupiedBeds", occupiedCount);
                roomObj.put("availableBeds", beds.size() - occupiedCount);

                JSONObject result = new JSONObject();
                result.put("success", true);
                result.put("room", roomObj);

                response.getWriter().write(result.toString());
                return;
            }
        }

        sendJsonError(response, "ROOM_NOT_FOUND", "Room not found: " + roomId);
    }

    /**
     * Get overall hostel statistics
     */
    private void handleGetStats(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Map<String, Integer> stats = xmlManager.getDashboardStats();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("totalBeds", stats.get("totalBeds"));
        result.put("occupiedBeds", stats.get("occupiedBeds"));
        result.put("availableBeds", stats.get("availableBeds"));
        result.put("totalStudents", stats.get("totalStudents"));
        result.put("allocatedStudents", stats.get("allocatedStudents"));
        result.put("unallocatedStudents", stats.get("unallocatedStudents"));

        if (stats.get("totalBeds") > 0) {
            int occupancyPercent = (stats.get("occupiedBeds") * 100) / stats.get("totalBeds");
            result.put("occupancyPercentage", occupancyPercent);
        }

        response.getWriter().write(result.toString());
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, String errorCode, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(400);

        JSONObject error = new JSONObject();
        error.put("success", false);
        error.put("errorCode", errorCode);
        error.put("message", message);

        response.getWriter().write(error.toString());
    }
}
