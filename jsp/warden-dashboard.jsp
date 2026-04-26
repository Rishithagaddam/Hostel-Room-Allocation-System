<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || !"warden".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
    String name = (String) session.getAttribute("name");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Warden Dashboard - Hostel Allocation</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <link rel="stylesheet" href="/hostel-allocation/css/responsive.css">
</head>
<body>
    <div class="navbar">
        <div class="navbar-content">
            <h2>🏨 Hostel Management System</h2>
            <div class="user-info">
                <span>Welcome, <%= name %></span>
                <a href="/hostel-allocation/jsp/login.jsp" class="btn btn-sm btn-secondary">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Success Message -->
        <%
            String success = request.getParameter("success");
            String newUsername = request.getParameter("username");
            String newPassword = request.getParameter("password");
            String studentName = (String) session.getAttribute("new_student_name");
            String studentYear = (String) session.getAttribute("new_student_year");
            String studentRoll = (String) session.getAttribute("new_student_roll");

            if (success != null) {
        %>
        <div class="alert alert-success">
            <strong>✓ Success:</strong> <%= success %>
            <% if (newUsername != null && newPassword != null) { %>
            <div class="credentials-box">
                <p><strong>🎓 New Student Credentials:</strong></p>
                <div class="credentials-item">
                    <strong>Name:</strong> <%= studentName != null ? studentName : "Student" %>
                </div>
                <div class="credentials-item">
                    <strong>Roll Number:</strong> <code><%= studentRoll != null ? studentRoll : "N/A" %></code>
                </div>
                <div class="credentials-item">
                    <strong>Username:</strong> <code><%= newUsername %></code>
                </div>
                <div class="credentials-item">
                    <strong>Password:</strong> <code><%= newPassword %></code>
                </div>
                <div class="credentials-item">
                    <strong>Year:</strong> <code><%= studentYear != null ? studentYear : "N/A" %></code>
                </div>
                <small>Share these credentials securely with the student via WhatsApp or email</small>
                <br><br>
                <button class="btn btn-sm" style="background: #25D366; color: white; margin-top: 10px;" onclick="copyFormattedCredentials('<%= studentName != null ? studentName : "Student" %>', '<%= newUsername %>', '<%= newPassword %>', '<%= studentYear != null ? studentYear : "N/A" %>')">
                    📋 Copy for WhatsApp
                </button>
            </div>
            <% } %>
        </div>
        <%
            // Clear session attributes
            session.removeAttribute("new_student_name");
            session.removeAttribute("new_student_year");
            session.removeAttribute("new_student_roll");
        %>
        <% } %>

        <!-- Error Message -->
        <%
            String error = request.getParameter("error");
            if (error != null) {
        %>
        <div class="alert" style="background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 15px; border-radius: 4px; margin-bottom: 20px;">
            <strong>✗ Error:</strong> <%= error %>
        </div>
        <% } %>

        <!-- Dashboard Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-value"><%= request.getAttribute("total_beds") != null ? request.getAttribute("total_beds") : "0" %></div>
                <div class="stat-label">Total Beds</div>
            </div>
            <div class="stat-card">
                <div class="stat-value" style="color: #DC3545;"><%= request.getAttribute("occupied_beds") != null ? request.getAttribute("occupied_beds") : "0" %></div>
                <div class="stat-label">Occupied Beds</div>
            </div>
            <div class="stat-card">
                <div class="stat-value" style="color: #28A745;"><%= request.getAttribute("available_beds") != null ? request.getAttribute("available_beds") : "0" %></div>
                <div class="stat-label">Available Beds</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><%= request.getAttribute("total_students") != null ? request.getAttribute("total_students") : "0" %></div>
                <div class="stat-label">Total Students</div>
            </div>
        </div>

        <!-- Occupancy Bar -->
        <div class="occupancy-section">
            <h3>Overall Occupancy</h3>
            <div class="occupancy-bar">
                <%
                    String occupancyStr = (String) request.getAttribute("occupancy_percentage");
                    double occupancy = occupancyStr != null ? Double.parseDouble(occupancyStr) : 0.0;
                %>
                <div class="occupancy-fill" style="width: <%= occupancy %>%;"></div>
            </div>
            <p class="occupancy-text"><%= String.format("%.1f%%", occupancy) %> Occupied</p>
        </div>

        <div class="dashboard-content">
            <!-- Add Student Form -->
            <div class="form-section">
                <h2>➕ Add New Student</h2>
                <form method="POST" action="/hostel-allocation/warden" class="form-grid">
                    <input type="hidden" name="action" value="add_student">

                    <div class="form-group">
                        <label for="student_name">Student Name *</label>
                        <input type="text" id="student_name" name="student_name" required placeholder="Full name">
                    </div>

                    <div class="form-group">
                        <label for="student_roll">Roll Number *</label>
                        <input type="text" id="student_roll" name="student_roll" required placeholder="e.g., 21BCE001">
                    </div>

                    <div class="form-group">
                        <label for="student_year">Year/Class *</label>
                        <select id="student_year" name="student_year" required>
                            <option value="">-- Select Year --</option>
                            <option value="1">1st Year</option>
                            <option value="2">2nd Year</option>
                            <option value="3">3rd Year</option>
                            <option value="4">4th Year</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="student_email">Email *</label>
                        <input type="email" id="student_email" name="student_email" required placeholder="student@college.edu">
                    </div>

                    <button type="submit" class="btn btn-primary" style="grid-column: 1/-1;">Register Student</button>
                </form>
            </div>

            <!-- Room Allocation Button -->
            <div class="room-section">
                <h2>🛏️ Room Allocation</h2>
                <p class="info-text">Allocate registered students to hostel rooms and beds</p>
                <div style="margin-top: 15px;">
                    <a href="/hostel-allocation/jsp/allocation.jsp" class="btn btn-primary" style="width: auto;">
                        🎯 Go to Room Allocation
                    </a>
                </div>
            </div>

            <!-- Room Status Overview -->
            <div class="room-section">
                <h2>🏠 Room Overview</h2>
                <p class="info-text">Rooms are allocated automatically based on student year. Same-year students are grouped together.</p>
                <div class="room-grid">
                    <div class="room-card">
                        <div class="room-header">
                            <h3>📌 Allocation Status</h3>
                        </div>
                        <div class="room-info">
                            <p>• Students are grouped by year</p>
                            <p>• Beds are assigned sequentially within each year group</p>
                            <p>• Each room has 3 beds</p>
                            <p>• Blocks: A, B, C (3 each, 3 floors each, 5 rooms each)</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Registered Students List -->
            <div class="room-section">
                <h2>👥 Unallocated Students</h2>
                <p class="info-text">Students waiting for room allocation</p>
                <div id="students-list" class="students-list-container">
                    <!-- Students will be loaded here -->
                    <p style="text-align: center; color: #999;">Loading students...</p>
                </div>
            </div>

            <!-- Allocated Students List -->
            <div class="room-section">
                <h2>🏠 Allocated Students</h2>
                <p class="info-text">Students who have been allocated rooms</p>
                <div id="allocated-students-list" class="students-list-container">
                    <!-- Allocated students will be loaded here -->
                    <p style="text-align: center; color: #999;">Loading allocated students...</p>
                </div>
            </div>
        </div>
    </div>

    <script src="/hostel-allocation/js/app.js"></script>
</body>
</html>
