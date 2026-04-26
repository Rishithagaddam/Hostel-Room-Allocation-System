package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class StudentServlet extends HttpServlet {
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
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        String studentId = (String) session.getAttribute("student_id");

        // Verify student role
        if (!"student".equals(role) || studentId == null) {
            response.sendRedirect("jsp/error.jsp?message=Unauthorized+access");
            return;
        }

        // Get allocation for this student
        Map<String, String> allocation = xmlManager.getAllocationByStudentId(studentId);

        if (allocation != null) {
            String roomId = allocation.get("room_id");
            String bedId = allocation.get("bed_id");

            // Get room details
            Map<String, String> room = xmlManager.getRoomById(roomId);

            if (room != null) {
                // Parse room_id to get block, floor, room number
                String[] parts = roomId.split("-");
                String blockId = parts[0];
                String floorNumber = parts[1];
                String roomNumber = room.get("room_number");

                request.setAttribute("block_id", blockId);
                request.setAttribute("floor_number", floorNumber);
                request.setAttribute("room_number", roomNumber);
                request.setAttribute("bed_id", bedId);
                request.setAttribute("bed_number", parts.length > 2 ? parts[2] : "");
                request.setAttribute("room_image", room.get("room_image"));
                request.setAttribute("allocated", true);
            } else {
                request.setAttribute("allocated", false);
                request.setAttribute("message", "Could not find room details");
            }
        } else {
            request.setAttribute("allocated", false);
            request.setAttribute("message", "No room has been allocated to you yet");
        }

        getServletContext().getRequestDispatcher("/jsp/student-dashboard.jsp").forward(request, response);
    }
}
