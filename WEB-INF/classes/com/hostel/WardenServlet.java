package com.hostel;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class WardenServlet extends HttpServlet {
    private XMLManager xmlManager;
    private AllocationEngine allocationEngine;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        xmlManager = new XMLManager(appPath);
        allocationEngine = new AllocationEngine(xmlManager);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");

        // Verify warden role
        if (!"warden".equals(role)) {
            response.sendRedirect("jsp/error.jsp?message=Unauthorized+access");
            return;
        }

        String action = request.getParameter("action");

        if ("add_student".equals(action)) {
            addStudent(request, response);
        } else if ("allocate_room".equals(action)) {
            allocateRoom(request, response);
        } else {
            response.sendRedirect("jsp/error.jsp?message=Invalid+action");
        }
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentName = request.getParameter("student_name");
        String studentYear = request.getParameter("student_year");
        String studentEmail = request.getParameter("student_email");
        String studentRoll = request.getParameter("student_roll");

        if (studentName == null || studentYear == null || studentEmail == null || studentRoll == null ||
            studentName.trim().isEmpty() || studentYear.trim().isEmpty() || studentEmail.trim().isEmpty() || studentRoll.trim().isEmpty()) {
            response.sendRedirect("jsp/error.jsp?message=All+fields+are+required");
            return;
        }

        // Check if roll number already exists
        if (xmlManager.rollNumberExists(studentRoll.trim())) {
            response.sendRedirect("jsp/warden-dashboard.jsp?error=Roll+number+already+exists");
            return;
        }

        // Generate username and password
        String username = PasswordUtils.generateUsername(studentName);
        String password = PasswordUtils.generateRandomPassword();
        String passwordHash = PasswordUtils.hashPassword(password);

        // Add student to XML
        boolean added = xmlManager.addStudent(studentName, studentYear, studentEmail, studentRoll.trim(), username, passwordHash);

        if (added) {
            // Redirect to warden dashboard with success message and credentials
            HttpSession session = request.getSession();
            session.setAttribute("new_student_username", username);
            session.setAttribute("new_student_password", password);
            session.setAttribute("new_student_name", studentName);
            session.setAttribute("new_student_year", studentYear);
            session.setAttribute("new_student_roll", studentRoll);
            response.sendRedirect("jsp/warden-dashboard.jsp?success=Student+added&username=" +
                    java.net.URLEncoder.encode(username, "UTF-8") + "&password=" +
                    java.net.URLEncoder.encode(password, "UTF-8"));
        } else {
            response.sendRedirect("jsp/error.jsp?message=Failed+to+add+student");
        }
    }

    private void allocateRoom(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String studentId = request.getParameter("student_id");
        String year = request.getParameter("year");

        if (studentId == null || year == null) {
            response.sendRedirect("jsp/error.jsp?message=Student+ID+and+year+are+required");
            return;
        }

        // Allocate room using allocation engine
        Map<String, Object> result = allocationEngine.allocateRoom(studentId, year);

        if ((Boolean) result.get("success")) {
            response.sendRedirect("jsp/warden-dashboard.jsp?success=Room+allocated+successfully");
        } else {
            response.sendRedirect("jsp/error.jsp?message=" +
                    java.net.URLEncoder.encode((String) result.get("message"), "UTF-8"));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get dashboard statistics
        Map<String, Integer> stats = xmlManager.getDashboardStats();
        double occupancy = allocationEngine.getOccupancyPercentage();

        request.setAttribute("total_beds", stats.get("total_beds"));
        request.setAttribute("occupied_beds", stats.get("occupied_beds"));
        request.setAttribute("available_beds", stats.get("available_beds"));
        request.setAttribute("total_students", stats.get("total_students"));
        request.setAttribute("occupancy_percentage", String.format("%.1f", occupancy));

        getServletContext().getRequestDispatcher("/jsp/warden-dashboard.jsp").forward(request, response);
    }
}
