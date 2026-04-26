package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class DashboardStatsServlet extends HttpServlet {
    private XMLManager xmlManager;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        xmlManager = new XMLManager(appPath);
        xmlManager.initializeRoomsIfMissing();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            Map<String, Integer> stats = xmlManager.getDashboardStats();
            int totalBeds = stats.get("totalBeds");
            int occupiedBeds = stats.get("occupiedBeds");
            int availableBeds = stats.get("availableBeds");
            int totalStudents = stats.get("totalStudents");
            int unallocatedStudents = stats.get("unallocatedStudents");
            int allocatedStudents = stats.get("allocatedStudents");
            double occupancyPercentage = totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;

            String json = String.format(
                "{\"totalBeds\": %d, \"occupiedBeds\": %d, \"availableBeds\": %d, \"totalStudents\": %d, \"registeredStudents\": %d, \"allocatedStudents\": %d}",
                totalBeds, occupiedBeds, availableBeds, totalStudents, unallocatedStudents, allocatedStudents
            );

            response.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }
}