<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true"%>
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
    <title>Student Management - Hostel Allocation</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <style>
        .search-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }

        .search-controls {
            display: grid;
            grid-template-columns: 1fr 1fr 150px;
            gap: 15px;
            margin-bottom: 15px;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 25px;
            border-radius: 8px;
            max-width: 500px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.2);
        }

        .modal-header {
            font-size: 20px;
            font-weight: bold;
            color: #4A6FA5;
            margin-bottom: 20px;
        }

        .close-btn {
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            color: #999;
        }

        .close-btn:hover {
            color: #333;
        }

        .student-list-header {
            display: grid;
            grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1fr 150px;
            gap: 15px;
            padding: 15px 20px;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            color: white;
            font-weight: 600;
            border-radius: 8px 8px 0 0;
            text-transform: uppercase;
            font-size: 12px;
        }

        .student-row {
            display: grid;
            grid-template-columns: 1.5fr 1fr 1.5fr 1fr 1fr 150px;
            gap: 15px;
            padding: 15px 20px;
            border-bottom: 1px solid #eee;
            align-items: center;
            font-size: 14px;
            cursor: pointer;
        }

        .student-row:hover {
            background-color: #f8f9fa;
        }

        .details-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
        }

        .detail-item {
            background: #f8f9fa;
            border-radius: 6px;
            padding: 10px 12px;
        }

        .detail-label {
            font-size: 12px;
            color: #6c757d;
            text-transform: uppercase;
            font-weight: 600;
            margin-bottom: 4px;
        }

        .detail-value {
            font-size: 14px;
            color: #333;
            font-weight: 600;
            word-break: break-word;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .btn-small {
            padding: 6px 12px;
            font-size: 12px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
        }

        .btn-edit {
            background: var(--primary-color);
            color: white;
        }

        .btn-edit:hover {
            background: #3a5a8f;
        }

        .btn-delete {
            background: var(--danger-color);
            color: white;
        }

        .btn-delete:hover {
            background: #c82333;
        }

        .student-list-container {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .empty-state {
            padding: 40px;
            text-align: center;
            color: #999;
        }

        .error-message {
            background: #f8d7da;
            color: #721c24;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 15px;
            display: none;
        }

        .success-message {
            background: #d4edda;
            color: #155724;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 15px;
            display: none;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }

        .form-group input, .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: var(--primary-color);
            box-shadow: 0 0 0 3px rgba(74, 111, 165, 0.1);
        }

        .form-buttons {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }

        .btn-submit {
            background: var(--primary-color);
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
        }

        .btn-submit:hover {
            background: #3a5a8f;
        }

        .btn-cancel {
            background: #6c757d;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
        }

        .btn-cancel:hover {
            background: #5a6268;
        }

        @media (max-width: 1024px) {
            .search-controls {
                grid-template-columns: 1fr;
            }

            .student-list-header, .student-row {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body class="login-page" style="background: #f8f9fa;">
    <div class="container">
        <div style="margin: 30px auto; max-width: 1200px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
                <h2 style="color: #4A6FA5; margin: 0;">Student Management</h2>
                <a href="warden-dashboard.jsp" style="color: #4A6FA5; text-decoration: none; font-weight: 600;">← Back to Dashboard</a>
            </div>

            <!-- Search Section -->
            <div class="search-container">
                <h3 style="color: #4A6FA5; margin-top: 0;">Search Students</h3>
                <div class="search-controls">
                    <input type="text" id="searchInput" placeholder="Search by name or year..." style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
                    <select id="yearFilter" style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
                        <option value="">All Years</option>
                        <option value="1">Year 1</option>
                        <option value="2">Year 2</option>
                        <option value="3">Year 3</option>
                        <option value="4">Year 4</option>
                    </select>
                    <button onclick="searchStudents()" style="background: #4A6FA5; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-weight: 600;">Search</button>
                </div>
                <div id="errorMessage" class="error-message"></div>
                <div id="successMessage" class="success-message"></div>
            </div>

            <!-- Students List -->
            <div class="student-list-container">
                <div class="student-list-header">
                    <div>Name</div>
                    <div>Roll Number</div>
                    <div>Email</div>
                    <div>Year</div>
                    <div>Status</div>
                    <div>Actions</div>
                </div>
                <div id="studentsList">
                    <div class="empty-state">Loading students...</div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Modal -->
    <div id="editModal" class="modal">
        <div class="modal-content">
            <span class="close-btn" onclick="closeEditModal()">&times;</span>
            <div class="modal-header">Edit Student Details</div>
            <div id="editErrorMessage" class="error-message"></div>

            <form onsubmit="submitEditForm(event)">
                <input type="hidden" id="editStudentId">

                <div class="form-group">
                    <label for="editName">Name</label>
                    <input type="text" id="editName" required>
                </div>

                <div class="form-group">
                    <label for="editEmail">Email</label>
                    <input type="email" id="editEmail" required>
                </div>

                <div class="form-group">
                    <label for="editYear">Year</label>
                    <select id="editYear" required>
                        <option value="1">Year 1</option>
                        <option value="2">Year 2</option>
                        <option value="3">Year 3</option>
                        <option value="4">Year 4</option>
                    </select>
                </div>

                <div class="form-buttons">
                    <button type="button" class="btn-cancel" onclick="closeEditModal()">Cancel</button>
                    <button type="submit" class="btn-submit">Update Student</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Student Details Modal -->
    <div id="detailsModal" class="modal">
        <div class="modal-content" style="max-width: 700px;">
            <span class="close-btn" onclick="closeDetailsModal()">&times;</span>
            <div class="modal-header">Student Details</div>
            <div id="studentDetailsContent" class="details-grid"></div>
        </div>
    </div>

    <script>
        let studentsCache = [];

        // Load students on page load
        window.addEventListener('DOMContentLoaded', () => {
            loadStudents();
        });

        function loadStudents() {
            fetch('/hostel-allocation/api/student-management?action=list')
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        displayStudents(data.students || []);
                    }
                })
                .catch(err => {
                    console.error('Error loading students:', err);
                    document.getElementById('studentsList').innerHTML = '<div class="empty-state">Error loading students</div>';
                });
        }

        function displayStudents(students) {
            const container = document.getElementById('studentsList');
            studentsCache = Array.isArray(students) ? students : [];

            if (!students || students.length === 0) {
                container.innerHTML = '<div class="empty-state">No students found</div>';
                return;
            }

            console.log('Students data:', students);
            let html = '';
            students.forEach((student, idx) => {
                console.log(`Student ${idx}:`, JSON.stringify(student));
                const name = normalizeField(student.name, '(unknown)');
                const rollNumber = normalizeField(student.rollNumber || student.roll_number, '(unknown)');
                const email = normalizeField(student.email, '(unknown)');
                const year = normalizeField(student.year, '?');
                const status = student.allocation_status === 'ALLOCATED' ? 'Allocated' : 'Unallocated';
                const statusColor = student.allocation_status === 'ALLOCATED' ? '#28A745' : '#FFC107';
                const studentId = normalizeField(student.student_id || student.studentId, '');

                html += `
                    <div class="student-row" onclick="openStudentDetails(${idx})">
                        <div>${name}</div>
                        <div>${rollNumber}</div>
                        <div>${email}</div>
                        <div><span class="year-badge" style="background: #4A6FA5;">Year ${year}</span></div>
                        <div><span style="background: ${statusColor}; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px;">${status}</span></div>
                        <div class="action-buttons">
                            <button class="btn-small btn-edit" onclick="editStudent(event, '${studentId}')">Edit</button>
                            <button class="btn-small btn-delete" onclick="deleteStudent(event, '${studentId}')">Delete</button>
                        </div>
                    </div>
                `;
            });

            container.innerHTML = html;
        }

        function normalizeField(value, fallback) {
            if (value === undefined || value === null) {
                return fallback;
            }

            const cleaned = String(value).trim();
            if (!cleaned || cleaned.toLowerCase() === 'false' || cleaned.toLowerCase() === 'null') {
                return fallback;
            }

            return cleaned;
        }

        function searchStudents() {
            const searchTerm = document.getElementById('searchInput').value.trim();
            const yearFilter = document.getElementById('yearFilter').value;

            const searchQuery = yearFilter || searchTerm;
            if (!searchQuery) {
                showError('Please enter a search term or select a year');
                return;
            }

            fetch('/hostel-allocation/api/student-management', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'action=search&search=' + encodeURIComponent(searchQuery)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    displayStudents(data.students || []);
                    showSuccess('Found ' + data.count + ' student(s)');
                } else {
                    showError(data.message);
                }
            })
            .catch(err => {
                showError('Search error: ' + err.message);
            });
        }

        function editStudent(event, studentId) {
            event.stopPropagation();
            const student = studentsCache.find(s => normalizeField(s.student_id || s.studentId, '') === studentId);
            if (!student) {
                showError('Student not found');
                return;
            }

            document.getElementById('editStudentId').value = studentId;
            document.getElementById('editName').value = normalizeField(student.name, '');
            document.getElementById('editEmail').value = normalizeField(student.email, '');
            document.getElementById('editYear').value = normalizeField(student.year, '1');
            document.getElementById('editModal').style.display = 'block';
        }

        function submitEditForm(event) {
            event.preventDefault();

            const studentId = document.getElementById('editStudentId').value;
            const name = document.getElementById('editName').value.trim();
            const email = document.getElementById('editEmail').value.trim();
            const year = document.getElementById('editYear').value;

            if (!name || !email) {
                showEditError('All fields are required');
                return;
            }

            fetch('/hostel-allocation/api/student-management', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'action=update&studentId=' + studentId + '&name=' + encodeURIComponent(name) +
                      '&email=' + encodeURIComponent(email) + '&year=' + year
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    closeEditModal();
                    loadStudents();
                    showSuccess('Student updated successfully');
                } else {
                    showEditError(data.message);
                }
            })
            .catch(err => {
                showEditError('Error: ' + err.message);
            });
        }

        function deleteStudent(event, studentId) {
            event.stopPropagation();
            const student = studentsCache.find(s => normalizeField(s.student_id || s.studentId, '') === studentId);
            const name = student ? normalizeField(student.name, 'this student') : 'this student';

            if (!confirm('Are you sure you want to delete ' + name + '? This action cannot be undone.')) {
                return;
            }

            fetch('/hostel-allocation/api/student-management', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: 'action=delete&studentId=' + studentId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    loadStudents();
                    showSuccess('Student deleted successfully');
                } else {
                    showError(data.message);
                }
            })
            .catch(err => {
                showError('Error: ' + err.message);
            });
        }

        function closeEditModal() {
            document.getElementById('editModal').style.display = 'none';
            document.getElementById('editErrorMessage').textContent = '';
            document.getElementById('editErrorMessage').style.display = 'none';
        }

        function openStudentDetails(index) {
            const student = studentsCache[index];
            if (!student) {
                return;
            }

            const status = student.allocation_status === 'ALLOCATED' ? 'Allocated' : 'Unallocated';
            const block = normalizeField(student.allocated_block || student.block, '-');
            const floor = normalizeField(student.allocated_floor || student.floor, '-');
            const room = normalizeField(student.allocated_room || student.room || student.roomNo, '-');
            const bed = normalizeField(student.allocated_bed || student.bed || student.bedNo, '-');

            document.getElementById('studentDetailsContent').innerHTML = `
                <div class="detail-item"><div class="detail-label">Student ID</div><div class="detail-value">${normalizeField(student.student_id || student.studentId, '-')}</div></div>
                <div class="detail-item"><div class="detail-label">Name</div><div class="detail-value">${normalizeField(student.name, '-')}</div></div>
                <div class="detail-item"><div class="detail-label">Roll Number</div><div class="detail-value">${normalizeField(student.rollNumber || student.roll_number, '-')}</div></div>
                <div class="detail-item"><div class="detail-label">Email</div><div class="detail-value">${normalizeField(student.email, '-')}</div></div>
                <div class="detail-item"><div class="detail-label">Year</div><div class="detail-value">Year ${normalizeField(student.year, '-')}</div></div>
                <div class="detail-item"><div class="detail-label">Allocation Status</div><div class="detail-value">${status}</div></div>
                <div class="detail-item"><div class="detail-label">Block</div><div class="detail-value">${block}</div></div>
                <div class="detail-item"><div class="detail-label">Floor</div><div class="detail-value">${floor}</div></div>
                <div class="detail-item"><div class="detail-label">Room</div><div class="detail-value">${room}</div></div>
                <div class="detail-item"><div class="detail-label">Bed</div><div class="detail-value">${bed}</div></div>
            `;

            document.getElementById('detailsModal').style.display = 'block';
        }

        function closeDetailsModal() {
            document.getElementById('detailsModal').style.display = 'none';
        }

        function showError(message) {
            const el = document.getElementById('errorMessage');
            el.textContent = message;
            el.style.display = 'block';
            setTimeout(() => {
                el.style.display = 'none';
            }, 5000);
        }

        function showEditError(message) {
            const el = document.getElementById('editErrorMessage');
            el.textContent = message;
            el.style.display = 'block';
        }

        function showSuccess(message) {
            const el = document.getElementById('successMessage');
            el.textContent = message;
            el.style.display = 'block';
            setTimeout(() => {
                el.style.display = 'none';
            }, 3000);
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('editModal');
            const detailsModal = document.getElementById('detailsModal');
            if (event.target === modal) {
                closeEditModal();
            }
            if (event.target === detailsModal) {
                closeDetailsModal();
            }
        }
    </script>
</body>
</html>
