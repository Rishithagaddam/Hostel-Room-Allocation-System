<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
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
    <title>Room Allocation - Hostel Management</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <link rel="stylesheet" href="/hostel-allocation/css/responsive.css">
</head>
<body>
    <div class="navbar">
        <div class="navbar-content">
            <h2>🏨 Room Allocation System</h2>
            <div class="user-info">
                <span>Welcome, <%= name %></span>
                <a href="/hostel-allocation/jsp/login.jsp" class="btn btn-sm btn-secondary">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Back Navigation -->
        <div style="margin-bottom: 20px;">
            <a href="/hostel-allocation/jsp/warden-dashboard.jsp" class="btn btn-secondary" style="width: auto;">← Back to Dashboard</a>
        </div>

        <!-- Success/Error Messages -->
        <%
            String success = request.getParameter("success");
            String error = request.getParameter("error");
            if (success != null) {
        %>
        <div class="alert alert-success">
            <strong>✓ Success:</strong> <%= success %>
        </div>
        <% } %>
        <% if (error != null) { %>
        <div class="alert alert-warning">
            <strong>⚠ Error:</strong> <%= error %>
        </div>
        <% } %>

        <!-- Block & Floor Selection -->
        <div class="allocation-filters">
            <div class="filter-group">
                <label>🏢 CHOOSE BLOCK</label>
                <div class="block-selector">
                    <button class="block-btn active" data-block="A" onclick="selectBlock(this, 'A')">Block A ×</button>
                    <button class="block-btn" data-block="B" onclick="selectBlock(this, 'B')">Block B ×</button>
                    <button class="block-btn" data-block="C" onclick="selectBlock(this, 'C')">Block C ×</button>
                </div>
            </div>

            <div class="filter-group">
                <label>📍 CHOOSE FLOOR</label>
                <select id="floor-select" onchange="selectFloor(this.value)">
                    <option value="">Select Floor...</option>
                    <option value="1">Floor 1</option>
                    <option value="2">Floor 2</option>
                    <option value="3">Floor 3</option>
                </select>
            </div>
        </div>

        <!-- Student Selection -->
        <div class="allocation-section">
            <h2>👨‍🎓 SELECT STUDENT</h2>
            <div class="student-selector">
                <select id="student-select">
                    <option value="">-- Select Unallocated Student --</option>
                    <!-- Options will be loaded via JavaScript -->
                </select>
                <button class="btn btn-primary" onclick="loadStudentDetails()" style="width: auto;">Load Student</button>
            </div>
        </div>

        <!-- Student Details -->
        <div id="student-details" style="display: none;">
            <div class="student-info-card">
                <div class="info-item">
                    <label>Student Name:</label>
                    <span id="student-name">-</span>
                </div>
                <div class="info-item">
                    <label>Year:</label>
                    <span id="student-year">-</span>
                </div>
                <div class="info-item">
                    <label>Email:</label>
                    <span id="student-email">-</span>
                </div>
            </div>
        </div>

        <!-- Rooms Display -->
        <div class="allocation-section">
            <h2>🛏️ SELECT ROOM & BED</h2>
            <div id="rooms-container" class="rooms-grid">
                <p style="text-align: center; color: #999;">Select block and floor to view rooms</p>
            </div>
        </div>

        <!-- Allocation Form -->
        <form id="allocation-form" method="POST" action="/hostel-allocation/allocate" style="display: none;">
            <input type="hidden" name="action" value="allocate_bed">
            <input type="hidden" id="selected-student" name="student_id" value="">
            <input type="hidden" id="selected-block" name="block_id" value="">
            <input type="hidden" id="selected-floor" name="floor_id" value="">
            <input type="hidden" id="selected-room" name="room_id" value="">
            <input type="hidden" id="selected-bed" name="bed_id" value="">

            <div class="allocation-summary" id="allocation-summary" style="display: none;">
                <h3>📋 Allocation Summary</h3>
                <div class="summary-item">
                    <strong>Student:</strong> <span id="summary-student">-</span>
                </div>
                <div class="summary-item">
                    <strong>Block:</strong> <span id="summary-block">-</span>
                </div>
                <div class="summary-item">
                    <strong>Floor:</strong> <span id="summary-floor">-</span>
                </div>
                <div class="summary-item">
                    <strong>Room:</strong> <span id="summary-room">-</span>
                </div>
                <div class="summary-item">
                    <strong>Bed:</strong> <span id="summary-bed">-</span>
                </div>
            </div>

            <div style="margin-top: 20px; text-align: center;">
                <button type="submit" class="btn btn-primary" onclick="return confirmAllocation()">
                    ✓ Confirm Allocation
                </button>
                <button type="button" class="btn btn-secondary" onclick="resetAllocation()" style="margin-left: 10px;">
                    ✕ Cancel
                </button>
            </div>
        </form>
    </div>

    <script src="/hostel-allocation/js/allocation.js"></script>
    <script src="/hostel-allocation/js/app.js"></script>
</body>
</html>
