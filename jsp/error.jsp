<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Hostel Allocation</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <div class="error-icon">⚠️</div>
            <h2>Oops! An Error Occurred</h2>
            <p class="error-message">
                <%
                    String message = request.getParameter("message");
                    out.print(message != null ? message : "Something went wrong. Please try again.");
                %>
            </p>
            <div class="error-actions">
                <a href="/hostel-allocation/jsp/login.jsp" class="btn btn-primary">Back to Login</a>
                <a href="/hostel-allocation/" class="btn btn-secondary">Home</a>
            </div>
        </div>
    </div>
</body>
</html>
