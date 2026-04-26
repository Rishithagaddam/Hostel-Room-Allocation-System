<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || !"student".equals(role)) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("username");
    String name = (String) session.getAttribute("name");
    Boolean allocated = (Boolean) request.getAttribute("allocated");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Room - Hostel Allocation</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <link rel="stylesheet" href="/hostel-allocation/css/responsive.css">
</head>
<body>
    <div class="navbar">
        <div class="navbar-content">
            <h2>🏨 Hostel Portal - Student</h2>
            <div class="user-info">
                <span>Welcome, <%= name %></span>
                <a href="/hostel-allocation/jsp/login.jsp" class="btn btn-sm btn-secondary">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <% if (allocated != null && allocated) { %>
            <!-- Room Details Card -->
            <div class="room-detail-card">
                <div class="room-header-section">
                    <h1>Your Assigned Room</h1>
                </div>

                <div class="room-detail-layout">
                    <!-- Room Image -->
                    <div class="room-image-section">
                        <img src="<%= request.getAttribute("room_image") %>" alt="Room Image" class="room-image" onerror="this.src='/hostel-allocation/images/room-placeholder.jpg'">
                        <p class="room-location">
                            Block <%= request.getAttribute("block_id") %> |
                            Floor <%= request.getAttribute("floor_number") %> |
                            Room <%= request.getAttribute("room_number") %>
                        </p>
                    </div>

                    <!-- Room Details -->
                    <div class="room-details-section">
                        <div class="detail-item">
                            <label>Block</label>
                            <div class="detail-value"><%= request.getAttribute("block_id") %></div>
                        </div>

                        <div class="detail-item">
                            <label>Floor</label>
                            <div class="detail-value"><%= request.getAttribute("floor_number") %></div>
                        </div>

                        <div class="detail-item">
                            <label>Room Number</label>
                            <div class="detail-value"><%= request.getAttribute("room_number") %></div>
                        </div>

                        <div class="detail-item">
                            <label>Bed Number</label>
                            <div class="detail-value bed-highlight"><%= request.getAttribute("bed_id") %></div>
                        </div>
                    </div>
                </div>

                <!-- Room Features -->
                <div class="room-features">
                    <h3>Room Features</h3>
                    <ul>
                        <li>✓ Shared accommodation (3 beds per room)</li>
                        <li>✓ Basic furniture included</li>
                        <li>✓ Attached washroom</li>
                        <li>✓ 24x7 Security & CCTV</li>
                    </ul>
                </div>

                <!-- Room Rules -->
                <div class="room-rules">
                    <h3>Hostel Rules</h3>
                    <ul>
                        <li>Maintain discipline and cleanliness in the room</li>
                        <li>No pets or hazardous items allowed</li>
                        <li>Designated quiet hours: 10 PM - 8 AM</li>
                        <li>Report any maintenance issues immediately</li>
                    </ul>
                </div>
            </div>

        <% } else { %>
            <!-- No Allocation Message -->
            <div class="alert alert-warning">
                <h2>⏳ Room Not Yet Allocated</h2>
                <p><%= request.getAttribute("message") != null ? request.getAttribute("message") : "Your room is being processed." %></p>
                <p>Please check back soon or contact the warden office.</p>
                <a href="/hostel-allocation/jsp/login.jsp" class="btn btn-primary" style="margin-top: 15px;">Back to Login</a>
            </div>
        <% } %>
    </div>

    <script src="/hostel-allocation/js/app.js"></script>
</body>
</html>
