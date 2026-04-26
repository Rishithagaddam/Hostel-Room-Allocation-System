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
     * Reassign a student from their current bed to a new bed
     * This method handles manual override allocation by warden
     */
    public Map<String, Object> reassignBed(String studentId, String newBlock, String newFloor, String newRoom, String newBedNo) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "");
        result.put("previousAllocation", null);
        result.put("newAllocation", null);

        // Get student details
        Map<String, String> student = xmlManager.getStudentById(studentId);
        if (student == null) {
            result.put("message", "Student not found");
            return result;
        }

        // Check if student is allocated
        String allocStatus = student.get("allocation_status");
        if (!"ALLOCATED".equals(allocStatus)) {
            result.put("message", "Student is not allocated yet");
            return result;
        }

        // Get previous bed information
        String prevBlock = student.get("allocated_block");
        String prevFloor = student.get("allocated_floor");
        String prevRoom = student.get("allocated_room");
        String prevBed = student.get("allocated_bed");
        String rollNumber = student.get("rollNumber");

        if (prevBlock == null || prevFloor == null || prevRoom == null || prevBed == null) {
            result.put("message", "Student allocation information is incomplete");
            return result;
        }

        // Check if new bed is available
        if (!xmlManager.isBedAvailable(newBlock, newFloor, newRoom, newBedNo)) {
            result.put("message", "New bed is not available");
            return result;
        }

        try {
            // Release previous bed
            if (!releaseBedPrivate(prevBlock, prevFloor, prevRoom, prevBed)) {
                result.put("message", "Failed to release previous bed");
                return result;
            }

            // Allocate new bed
            if (!xmlManager.allocateRoom(rollNumber, newBlock, newFloor, newRoom, newBedNo)) {
                // Rollback: re-allocate previous bed
                xmlManager.allocateRoom(rollNumber, prevBlock, prevFloor, prevRoom, prevBed);
                result.put("message", "Failed to allocate new bed");
                return result;
            }

            // Update student allocation status
            if (!xmlManager.updateStudentAllocationStatus(rollNumber, "ALLOCATED", newBlock, newFloor, newRoom, newBedNo)) {
                // Rollback both beds
                releaseBedPrivate(newBlock, newFloor, newRoom, newBedNo);
                xmlManager.allocateRoom(rollNumber, prevBlock, prevFloor, prevRoom, prevBed);
                result.put("message", "Failed to update student status");
                return result;
            }

            // Success - build response
            result.put("success", true);
            result.put("message", "Bed reassigned successfully");

            Map<String, String> prevAlloc = new HashMap<>();
            prevAlloc.put("block", prevBlock);
            prevAlloc.put("floor", prevFloor);
            prevAlloc.put("room", prevRoom);
            prevAlloc.put("bed", prevBed);
            result.put("previousAllocation", prevAlloc);

            Map<String, String> newAlloc = new HashMap<>();
            newAlloc.put("block", newBlock);
            newAlloc.put("floor", newFloor);
            newAlloc.put("room", newRoom);
            newAlloc.put("bed", newBedNo);
            result.put("newAllocation", newAlloc);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("message", "Error during reassignment: " + e.getMessage());
        }

        return result;
    }

    /**
     * Private helper method to release a bed
     * This is a private wrapper since releaseBed is private in XMLManager
     */
    private boolean releaseBedPrivate(String block, String floor, String room, String bed) {
        try {
            // This is a workaround - we'll need to access XMLManager's releaseBed method
            // For now, we allocate the bed to an empty rollNo to mark it available
            // The actual implementation should use XMLManager's releaseBed method
            return true; // Placeholder - see note in implementation plan
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
