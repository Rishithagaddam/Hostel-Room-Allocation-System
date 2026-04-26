<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
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
    <title>Room Vacancy Visualization - Hostel Allocation</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <style>
        .vacancy-container {
            max-width: 1400px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .vacancy-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .vacancy-title {
            font-size: 28px;
            color: #4A6FA5;
            margin: 0;
        }

        .vacancy-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 15px;
            margin-bottom: 30px;
            padding: 20px;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .stat-box {
            text-align: center;
        }

        .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #4A6FA5;
            margin-bottom: 5px;
        }

        .stat-label {
            font-size: 13px;
            color: #666;
            text-transform: uppercase;
        }

        .filter-section {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }

        .filter-title {
            font-size: 16px;
            font-weight: 600;
            color: #4A6FA5;
            margin-bottom: 15px;
        }

        .filter-controls {
            display: grid;
            grid-template-columns: 150px 150px 150px 1fr;
            gap: 15px;
            align-items: center;
        }

        .filter-controls label {
            font-weight: 600;
            color: #333;
            margin-right: 10px;
        }

        .filter-controls select {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }

        .block-selector {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .block-btn {
            padding: 8px 16px;
            border: 2px solid #ddd;
            background: white;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            color: #666;
            transition: all 0.3s;
        }

        .block-btn:hover {
            border-color: #4A6FA5;
            color: #4A6FA5;
        }

        .block-btn.active {
            background: #4A6FA5;
            color: white;
            border-color: #4A6FA5;
        }

        .rooms-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .room-card {
            background: white;
            border: 2px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .room-card:hover {
            border-color: #4A6FA5;
            box-shadow: 0 4px 16px rgba(0,0,0,0.15);
            transform: translateY(-2px);
        }

        .room-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #f0f0f0;
        }

        .room-number {
            font-size: 18px;
            font-weight: 600;
            color: #4A6FA5;
        }

        .occupancy-badge {
            background: #4A6FA5;
            color: white;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
        }

        .beds-display {
            display: flex;
            justify-content: space-around;
            gap: 10px;
            margin-bottom: 15px;
        }

        .bed-icon {
            width: 50px;
            height: 50px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            cursor: pointer;
            transition: all 0.3s;
            border: 2px solid transparent;
        }

        .bed-available {
            background: #d4edda;
            border-color: #28A745;
        }

        .bed-available:hover {
            transform: scale(1.1);
            box-shadow: 0 0 10px rgba(40, 167, 69, 0.3);
        }

        .bed-occupied {
            background: #f8d7da;
            border-color: #dc3545;
        }

        .bed-occupied:hover {
            transform: scale(1.1);
            box-shadow: 0 0 10px rgba(220, 53, 69, 0.3);
        }

        .bed-label {
            text-align: center;
            font-size: 11px;
            color: #666;
            margin-top: 5px;
        }

        .room-stats {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 4px;
            font-size: 12px;
            color: #666;
        }

        .room-stats-row {
            display: flex;
            justify-content: space-between;
            margin: 5px 0;
        }

        .legend {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            display: flex;
            gap: 30px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .legend-item {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .legend-icon {
            width: 30px;
            height: 30px;
            border-radius: 4px;
        }

        .legend-icon.available {
            background: #d4edda;
            border: 2px solid #28A745;
        }

        .legend-icon.occupied {
            background: #f8d7da;
            border: 2px solid #dc3545;
        }

        .loading-spinner {
            text-align: center;
            padding: 40px;
            color: #999;
        }

        @media (max-width: 768px) {
            .filter-controls {
                grid-template-columns: 1fr;
            }

            .rooms-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body style="background: #f8f9fa;">
    <div class="vacancy-container">
        <div class="vacancy-header">
            <h1 class="vacancy-title">🏢 Room Vacancy Visualization</h1>
            <a href="warden-dashboard.jsp" style="color: #4A6FA5; text-decoration: none; font-weight: 600;">← Back</a>
        </div>

        <!-- Statistics -->
        <div class="vacancy-stats" id="statsContainer">
            <div class="stat-box">
                <div class="stat-value" id="totalBeds">720</div>
                <div class="stat-label">Total Beds</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" id="occupiedBeds">0</div>
                <div class="stat-label">Occupied</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" id="availableBeds">720</div>
                <div class="stat-label">Available</div>
            </div>
            <div class="stat-box">
                <div class="stat-value" id="occupancyPercent">0%</div>
                <div class="stat-label">Occupancy Rate</div>
            </div>
        </div>

        <!-- Filters -->
        <div class="filter-section">
            <div class="filter-title">Select Block & Floor</div>
            <div class="filter-controls">
                <div>
                    <label>Block:</label>
                    <div class="block-selector" id="blockSelector"></div>
                </div>
                <div style="display: flex; gap: 10px;">
                    <label for="floorSelect">Floor:</label>
                    <select id="floorSelect" onchange="loadRooms()">
                        <option value="">All Floors</option>
                        <option value="1">Floor 1</option>
                        <option value="2">Floor 2</option>
                        <option value="3">Floor 3</option>
                        <option value="4">Floor 4</option>
                    </select>
                </div>
                <div style="display: flex; gap: 10px;">
                    <label for="filterSelect">Filter:</label>
                    <select id="filterSelect" onchange="applyFilter()">
                        <option value="all">All Beds</option>
                        <option value="available">Available Only</option>
                        <option value="occupied">Occupied Only</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- Rooms Display -->
        <div class="rooms-grid" id="roomsGrid">
            <div class="loading-spinner">Loading rooms...</div>
        </div>

        <!-- Legend -->
        <div class="legend">
            <div class="legend-item">
                <div class="legend-icon available"></div>
                <span>Available Bed</span>
            </div>
            <div class="legend-item">
                <div class="legend-icon occupied"></div>
                <span>Occupied Bed</span>
            </div>
        </div>
    </div>

    <script>
        let allRooms = [];
        let selectedBlock = 'A';

        window.addEventListener('DOMContentLoaded', () => {
            initializeBlocks();
            loadStats();
            loadRooms();
        });

        function initializeBlocks() {
            const blockSelector = document.getElementById('blockSelector');
            ['A', 'B', 'C', 'D'].forEach(block => {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.className = 'block-btn' + (block === 'A' ? ' active' : '');
                btn.textContent = 'Block ' + block;
                btn.onclick = () => selectBlock(block);
                blockSelector.appendChild(btn);
            });
        }

        function selectBlock(block) {
            selectedBlock = block;
            document.querySelectorAll('.block-btn').forEach((btn, i) => {
                btn.classList.toggle('active', ['A', 'B', 'C', 'D'][i] === block);
            });
            document.getElementById('floorSelect').value = '';
            loadRooms();
        }

        function loadStats() {
            fetch('/hostel-allocation/api/rooms-vacancy?action=get_stats')
                .then(r => r.json())
                .then(data => {
                    if (data.success) {
                        document.getElementById('totalBeds').textContent = data.totalBeds;
                        document.getElementById('occupiedBeds').textContent = data.occupiedBeds;
                        document.getElementById('availableBeds').textContent = data.availableBeds;
                        document.getElementById('occupancyPercent').textContent = (data.occupancyPercentage || 0) + '%';
                    }
                })
                .catch(e => console.error('Error loading stats:', e));
        }

        function loadRooms() {
            const block = selectedBlock;
            const floor = document.getElementById('floorSelect').value;

            const url = '/hostel-allocation/api/rooms-vacancy?action=get_rooms&block=' + block +
                        (floor ? '&floor=' + floor : '');

            fetch(url)
                .then(r => r.json())
                .then(data => {
                    if (data.success && data.blocks && data.blocks.length > 0) {
                        allRooms = flattenRooms(data.blocks);
                        applyFilter();
                    }
                })
                .catch(e => {
                    console.error('Error:', e);
                    document.getElementById('roomsGrid').innerHTML = '<div class="loading-spinner">Error loading rooms</div>';
                });
        }

        function flattenRooms(blocks) {
            let rooms = [];
            blocks.forEach(b => {
                b.floors.forEach(f => {
                    f.rooms.forEach(r => {
                        rooms.push(r);
                    });
                });
            });
            return rooms;
        }

        function applyFilter() {
            const filter = document.getElementById('filterSelect').value;
            const grid = document.getElementById('roomsGrid');
            let filtered = allRooms;

            if (filter === 'available') {
                filtered = allRooms.filter(r => r.availableBeds > 0);
            } else if (filter === 'occupied') {
                filtered = allRooms.filter(r => r.occupiedBeds > 0);
            }

            if (filtered.length === 0) {
                grid.innerHTML = '<div class="loading-spinner">No rooms match the selected filter</div>';
                return;
            }

            let html = '';
            filtered.forEach(room => {
                const occupancyPercent = Math.round((room.occupiedBeds / room.totalBeds) * 100);
                const occupancyColor = occupancyPercent === 0 ? '#28A745' :
                                      occupancyPercent < 50 ? '#FFC107' : '#DC3545';

                html += `
                    <div class="room-card">
                        <div class="room-header">
                            <div class="room-number">Room ${room.roomNo}</div>
                            <div class="occupancy-badge" style="background: ${occupancyColor};">${occupancyPercent}% Full</div>
                        </div>

                        <div class="beds-display">
                `;

                room.beds.forEach((bed, idx) => {
                    const bedClass = bed.status.toLowerCase() === 'occupied' ? 'bed-occupied' : 'bed-available';
                    const bedEmoji = bed.status.toLowerCase() === 'occupied' ? '❌' : '✅';
                    html += `
                        <div style="text-align: center;">
                            <div class="bed-icon ${bedClass}" title="Bed ${bed.bedNo} - ${bed.status}">
                                ${bedEmoji}
                            </div>
                            <div class="bed-label">Bed ${bed.bedNo}</div>
                        </div>
                    `;
                });

                html += `
                        </div>

                        <div class="room-stats">
                            <div class="room-stats-row">
                                <span>Total Beds:</span>
                                <strong>${room.totalBeds}</strong>
                            </div>
                            <div class="room-stats-row">
                                <span>Occupied:</span>
                                <strong style="color: #DC3545;">${room.occupiedBeds}</strong>
                            </div>
                            <div class="room-stats-row">
                                <span>Available:</span>
                                <strong style="color: #28A745;">${room.availableBeds}</strong>
                            </div>
                        </div>
                    </div>
                `;
            });

            grid.innerHTML = html;
        }
    </script>
</body>
</html>
