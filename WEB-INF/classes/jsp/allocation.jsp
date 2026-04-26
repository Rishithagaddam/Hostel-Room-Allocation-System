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
    <title>Room Allocation - Hostel Management</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <link rel="stylesheet" href="/hostel-allocation/css/responsive.css">
    <style>
        .allocation-container {
            max-width: 1200px;
            margin: 20px auto;
            display: grid;
            grid-template-columns: 1fr 2fr;
            gap: 20px;
        }

        .student-panel, .room-panel {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .student-selector {
            margin-bottom: 20px;
        }

        .student-selector label {
            display: block;
            margin-bottom: 10px;
            font-weight: 600;
            color: #333;
        }

        .student-selector select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .selected-student {
            background: #f0f7ff;
            padding: 15px;
            border-radius: 4px;
            border-left: 4px solid #4A6FA5;
            margin-bottom: 20px;
        }

        .selected-student h3 {
            margin-top: 0;
            color: #4A6FA5;
        }

        .student-info {
            font-size: 14px;
            line-height: 1.8;
        }

        .block-selector {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-bottom: 20px;
        }

        .block-btn {
            padding: 10px;
            border: 2px solid #ddd;
            background: white;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
        }

        .block-btn:hover {
            border-color: #4A6FA5;
            background: #f0f7ff;
        }

        .block-btn.active {
            background: #4A6FA5;
            color: white;
            border-color: #4A6FA5;
        }

        .controls {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-bottom: 20px;
        }

        .controls select, .controls label {
            display: block;
        }

        .controls label {
            font-weight: 600;
            margin-bottom: 5px;
            color: #333;
        }

        .controls select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .rooms-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 10px;
            max-height: 400px;
            overflow-y: auto;
        }

        .room-card {
            background: #f5f5f5;
            padding: 10px;
            border-radius: 4px;
            cursor: pointer;
            border: 2px solid transparent;
            text-align: center;
            font-size: 12px;
            transition: all 0.3s;
        }

        .room-card:hover {
            border-color: #4A6FA5;
            background: #f0f7ff;
        }

        .room-card.selected {
            background: #4A6FA5;
            color: white;
            border-color: #4A6FA5;
        }

        .bed-selector {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 5px;
            margin-top: 10px;
        }

        .bed-btn {
            padding: 8px;
            border: 1px solid #ddd;
            background: #e8f5e9;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            transition: all 0.3s;
        }

        .bed-btn:hover {
            background: #c8e6c9;
        }

        .bed-btn.selected {
            background: #4A6FA5;
            color: white;
            border-color: #4A6FA5;
        }

        .bed-btn:disabled {
            background: #ffcccc;
            cursor: not-allowed;
            opacity: 0.5;
        }

        .allocation-btn {
            width: 100%;
            padding: 12px;
            background: #28A745;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
            margin-top: 20px;
            transition: all 0.3s;
        }

        .allocation-btn:hover:not(:disabled) {
            background: #218838;
        }

        .allocation-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 30px;
            border-radius: 8px;
            width: 400px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.3);
            text-align: center;
        }

        .modal-buttons {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-top: 20px;
        }

        .modal-buttons button {
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
        }

        .confirm-btn {
            background: #28A745;
            color: white;
        }

        .cancel-btn {
            background: #ccc;
            color: #333;
        }

        @media (max-width: 768px) {
            .allocation-container {
                grid-template-columns: 1fr;
            }
            .block-selector {
                grid-template-columns: 1fr 1fr;
            }
        }
    </style>
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
        <div style="margin-bottom: 20px;">
            <a href="/hostel-allocation/jsp/warden-dashboard.jsp" class="btn btn-secondary" style="width: auto;">← Back to Dashboard</a>
        </div>

        <div class="allocation-container">
            <!-- Student Selection Panel -->
            <div class="student-panel">
                <h2>👨‍🎓 Select Student</h2>
                <div class="student-selector">
                    <label for="student-select">Registered Students</label>
                    <select id="student-select">
                        <option value="">-- Select a Student --</option>
                    </select>
                </div>
                <div id="selected-student-info" class="selected-student" style="display: none;">
                    <h3 id="student-name">-</h3>
                    <div class="student-info">
                        <div><strong>Roll No:</strong> <span id="student-roll">-</span></div>
                        <div><strong>Year:</strong> <span id="student-year">-</span></div>
                        <div><strong>Email:</strong> <span id="student-email">-</span></div>
                    </div>
                </div>
            </div>

            <!-- Room Selection Panel -->
            <div class="room-panel">
                <h2>🏠 Select Room</h2>

                <!-- Block Selection -->
                <div>
                    <label>Choose Block</label>
                    <div class="block-selector">
                        <button class="block-btn active" onclick="selectBlock('A')">Block A</button>
                        <button class="block-btn" onclick="selectBlock('B')">Block B</button>
                        <button class="block-btn" onclick="selectBlock('C')">Block C</button>
                        <button class="block-btn" onclick="selectBlock('D')">Block D</button>
                    </div>
                </div>

                <!-- Floor and Room Selection -->
                <div class="controls">
                    <div>
                        <label for="floor-select">Floor</label>
                        <select id="floor-select" onchange="loadRooms()">
                            <option value="">-- Select Floor --</option>
                            <option value="1">Floor 1</option>
                            <option value="2">Floor 2</option>
                            <option value="3">Floor 3</option>
                            <option value="4">Floor 4</option>
                        </select>
                    </div>
                    <div id="room-selector-div" style="display: none;">
                        <label for="room-select">Room</label>
                        <select id="room-select" onchange="displayBeds()">
                            <option value="">-- Select Room --</option>
                        </select>
                    </div>
                </div>

                <!-- Beds Display -->
                <div id="beds-container" style="display: none;">
                    <label>Available Beds</label>
                    <div class="bed-selector" id="beds-grid">
                        <!-- Beds will be loaded here -->
                    </div>
                </div>

                <!-- Allocate Button -->
                <button class="allocation-btn" id="allocate-btn" onclick="confirmAllocation()" disabled>
                    🎯 Allocate Bed
                </button>
            </div>
        </div>
    </div>

    <!-- Confirmation Modal -->
    <div id="confirmModal" class="modal">
        <div class="modal-content">
            <h2>Confirm Allocation</h2>
            <p>Are you sure you want to allocate this bed to the selected student?</p>
            <p style="background: #f0f7ff; padding: 15px; border-radius: 4px;">
                <strong id="confirm-student-name">-</strong><br>
                <small>Block: <span id="confirm-block">-</span> | Floor: <span id="confirm-floor">-</span> | Room: <span id="confirm-room">-</span> | Bed: <span id="confirm-bed">-</span></small>
            </p>
            <div class="modal-buttons">
                <button class="confirm-btn" onclick="submitAllocation()">Confirm</button>
                <button class="cancel-btn" onclick="closeModal()">Cancel</button>
            </div>
        </div>
    </div>

    <script>
        let selectedBlock = 'A';
        let selectedFloor = null;
        let selectedRoom = null;
        let selectedBed = null;
        let selectedStudent = null;

        // Load students on page load
        document.addEventListener('DOMContentLoaded', function() {
            loadStudents();
        });

        function loadStudents() {
            fetch('/hostel-allocation/allocate?action=get_students')
                .then(response => response.json())
                .then(data => {
                    const select = document.getElementById('student-select');
                    select.innerHTML = '<option value="">-- Select a Student --</option>';
                    data.forEach(student => {
                        const option = document.createElement('option');
                        option.value = JSON.stringify(student);
                        option.text = `${student.name} (${student.roll_no}) - Year ${student.year}`;
                        select.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Error loading students:', error);
                    alert('Failed to load students');
                });
        }

        document.getElementById('student-select').addEventListener('change', function() {
            if (this.value) {
                selectedStudent = JSON.parse(this.value);
                document.getElementById('selected-student-info').style.display = 'block';
                document.getElementById('student-name').textContent = selectedStudent.name;
                document.getElementById('student-roll').textContent = selectedStudent.roll_no;
                document.getElementById('student-year').textContent = selectedStudent.year;
                document.getElementById('student-email').textContent = selectedStudent.email;
                checkAllocationReady();
            } else {
                selectedStudent = null;
                document.getElementById('selected-student-info').style.display = 'none';
                checkAllocationReady();
            }
        });

        function selectBlock(block) {
            selectedBlock = block;
            document.querySelectorAll('.block-btn').forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
            selectedFloor = null;
            selectedRoom = null;
            selectedBed = null;
            document.getElementById('floor-select').value = '';
            document.getElementById('room-selector-div').style.display = 'none';
            document.getElementById('beds-container').style.display = 'none';
        }

        function loadRooms() {
            selectedFloor = document.getElementById('floor-select').value;
            if (!selectedFloor) return;

            // Generate room options for the selected block and floor
            const roomSelect = document.getElementById('room-select');
            roomSelect.innerHTML = '<option value="">-- Select Room --</option>';

            for (let i = 1; i <= 5; i++) {
                const option = document.createElement('option');
                option.value = `${selectedBlock}-${selectedFloor}-${i}`;
                option.text = `Room ${selectedFloor}${i}`;
                roomSelect.appendChild(option);
            }

            document.getElementById('room-selector-div').style.display = 'block';
            selectedRoom = null;
            selectedBed = null;
            document.getElementById('beds-container').style.display = 'none';
        }

        function displayBeds() {
            selectedRoom = document.getElementById('room-select').value;
            if (!selectedRoom) return;

            const bedsGrid = document.getElementById('beds-grid');
            bedsGrid.innerHTML = '';

            for (let i = 1; i <= 3; i++) {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'bed-btn';
                btn.textContent = `Bed ${i}`;
                btn.onclick = function() {
                    selectBed(i, this);
                };
                bedsGrid.appendChild(btn);
            }

            document.getElementById('beds-container').style.display = 'block';
            selectedBed = null;
            checkAllocationReady();
        }

        function selectBed(bedNum, btn) {
            selectedBed = bedNum;
            document.querySelectorAll('.bed-btn').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
            checkAllocationReady();
        }

        function checkAllocationReady() {
            const ready = selectedStudent && selectedRoom && selectedBed;
            document.getElementById('allocate-btn').disabled = !ready;
        }

        function confirmAllocation() {
            if (!selectedStudent || !selectedRoom || !selectedBed) {
                alert('Please select student, room, and bed');
                return;
            }

            document.getElementById('confirm-student-name').textContent = selectedStudent.name;
            document.getElementById('confirm-block').textContent = selectedBlock;
            document.getElementById('confirm-floor').textContent = selectedFloor;
            document.getElementById('confirm-room').textContent = selectedRoom.split('-')[2];
            document.getElementById('confirm-bed').textContent = selectedBed;

            document.getElementById('confirmModal').style.display = 'block';
        }

        function closeModal() {
            document.getElementById('confirmModal').style.display = 'none';
        }

        function submitAllocation() {
            const bedId = `${selectedRoom}-${selectedBed}`;

            fetch('/hostel-allocation/allocate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=allocate_bed&student_id=${selectedStudent.student_id}&roll_no=${selectedStudent.roll_no}&bed_id=${bedId}&room_id=${selectedRoom}&block=${selectedBlock}&floor=${selectedFloor}&room=${selectedRoom.split('-')[2]}&bed=${selectedBed}`
            })
            .then(response => response.json())
            .then(data => {
                closeModal();
                if (data.success) {
                    alert('Room allocated successfully! Email sent to student.');
                    document.getElementById('student-select').value = '';
                    document.getElementById('selected-student-info').style.display = 'none';
                    selectedStudent = null;
                    selectedRoom = null;
                    selectedBed = null;
                    selectedFloor = null;
                    document.getElementById('room-selector-div').style.display = 'none';
                    document.getElementById('beds-container').style.display = 'none';
                    loadStudents(); // Reload student list
                } else {
                    alert('Error: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Failed to allocate room');
                closeModal();
            });
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('confirmModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
