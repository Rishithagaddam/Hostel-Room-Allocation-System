package com.hostel;

import java.util.*;

/**
 * Core business logic for allocating rooms to students based on year
 */
public class AllocationEngine {
    private XMLManager xmlManager;

    public AllocationEngine(XMLManager xmlManager) {
        this.xmlManager = xmlManager;
    }

    /**
     * Allocate a room to a student based on their year
     * Students of the same year should be grouped together
     */
    public Map<String, Object> allocateRoom(String studentId, String year) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "");
        result.put("allocation_details", null);

        // Get available beds for the year
        List<Map<String, String>> availableBeds = xmlManager.getAvailableBedsForYear(year);

        if (availableBeds.isEmpty()) {
            result.put("message", "No available beds for year " + year);
            return result;
        }

        // Get the first available bed (sequential assignment within year group)
        Map<String, String> selectedBed = availableBeds.get(0);
        String bedId = selectedBed.get("bed_id");
        String roomId = selectedBed.get("room_id");

        // Allocate the bed
        boolean allocated = xmlManager.allocateBed(studentId, roomId, bedId);

        if (allocated) {
            result.put("success", true);
            result.put("message", "Room allocated successfully");

            Map<String, String> details = new HashMap<>();
            details.put("bed_id", bedId);
            details.put("room_id", roomId);
            details.put("block_id", selectedBed.get("block_id"));
            details.put("floor_id", selectedBed.get("floor_id"));
            result.put("allocation_details", details);
        } else {
            result.put("message", "Failed to allocate room");
        }

        return result;
    }

    /**
     * Get occupancy status for a room
     */
    public String getOccupancyStatus(String roomId) {
        Map<String, String> room = xmlManager.getRoomById(roomId);
        if (room == null) {
            return "Unknown";
        }

        // Count occupied beds (for demo, we'll calculate based on allocation records)
        // This would typically query allocations.xml
        return "Available";
    }

    /**
     * Get occupancy percentage
     */
    public double getOccupancyPercentage() {
        Map<String, Integer> stats = xmlManager.getDashboardStats();
        int totalBeds = stats.get("total_beds");
        int occupiedBeds = stats.get("occupied_beds");

        if (totalBeds == 0) {
            return 0.0;
        }

        return (occupiedBeds * 100.0) / totalBeds;
    }
}
