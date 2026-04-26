package com.hostel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    private XMLManager xmlManager;

    @Override
    public void init() throws ServletException {
        super.init();
        String appPath = getServletContext().getRealPath("/");
        xmlManager = new XMLManager(appPath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // "warden" or "student"

        if (username == null || password == null || role == null) {
            response.sendRedirect("jsp/error.jsp?message=Invalid+request");
            return;
        }

        // Get student from XML
        Map<String, String> student = xmlManager.getStudentByUsername(username);

        if (student == null) {
            response.sendRedirect("jsp/error.jsp?message=Invalid+username");
            return;
        }

        // Verify password
        String passwordHash = student.get("password_hash");
        if (!PasswordUtils.verifyPassword(password, passwordHash)) {
            response.sendRedirect("jsp/error.jsp?message=Invalid+password");
            return;
        }

        // Verify role
        String studentRole = student.get("role");
        if (!studentRole.equals(role)) {
            response.sendRedirect("jsp/error.jsp?message=Invalid+role");
            return;
        }

        // Create session
        HttpSession session = request.getSession();
        session.setAttribute("student_id", student.get("student_id"));
        session.setAttribute("username", student.get("username"));
        session.setAttribute("name", student.get("name"));
        session.setAttribute("role", student.get("role"));
        session.setAttribute("year", student.get("year"));

        // Redirect based on role
        if ("warden".equals(role)) {
            response.sendRedirect("jsp/warden-dashboard.jsp");
        } else {
            response.sendRedirect("jsp/student-dashboard.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to login page
        response.sendRedirect("jsp/login.jsp");
    }
}
