<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="true"%>
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
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
            gap: 10px;
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
            grid-template-columns: repeat(auto-fit, minmax(620px, 1fr));
            gap: 22px;
            margin-bottom: 30px;
            align-items: start;
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

        .block-section {
            background: rgba(255, 255, 255, 0.75);
            border: 1px solid #e9ecef;
            border-radius: 14px;
            padding: 18px;
            margin-bottom: 24px;
            box-shadow: 0 8px 24px rgba(14, 28, 37, 0.08);
            min-width: 0;
            overflow: hidden;
        }

        .block-section-header {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            align-items: center;
            margin-bottom: 18px;
            padding-bottom: 12px;
            border-bottom: 1px solid #edf1f5;
        }

        .block-title {
            font-size: 20px;
            font-weight: 700;
            color: #284b6d;
            margin: 0;
        }

        .block-summary {
            font-size: 13px;
            color: #6c757d;
        }

        .floor-section {
            margin-top: 0;
            padding: 16px;
            border-radius: 12px;
            background: linear-gradient(180deg, #ffffff 0%, #f9fbfd 100%);
            border: 1px solid #edf1f5;
            min-width: 0;
        }

        .floors-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 16px;
        }

        .floor-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            margin-bottom: 14px;
        }

        .floor-title {
            font-size: 16px;
            font-weight: 700;
            color: #4A6FA5;
            margin: 0;
        }

        .room-layout {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 16px;
        }

        .room-card.empty-room {
            opacity: 0.55;
        }

        .room-meta {
            font-size: 12px;
            color: #6c757d;
            margin-top: 4px;
        }

        .room-id-pill {
            display: inline-block;
            font-size: 11px;
            padding: 4px 8px;
            border-radius: 999px;
            background: #f1f5f9;
            color: #415a77;
            margin-top: 10px;
        }

        .bed-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
            gap: 10px;
            margin: 16px 0;
        }

        .room-card,
        .room-header,
        .room-stats,
        .block-summary,
        .floor-header,
        .room-footer,
        .bed-chip-roll {
            min-width: 0;
        }

        .bed-chip {
            border-radius: 12px;
            padding: 12px 10px;
            min-height: 72px;
            border: 1px solid transparent;
            display: flex;
            flex-direction: column;
            justify-content: center;
            gap: 4px;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }

        .bed-chip:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 16px rgba(0,0,0,0.08);
        }

        .bed-chip.available {
            background: #e9f8ef;
            border-color: #28A745;
        }

        .bed-chip.occupied {
            background: #fdecef;
            border-color: #dc3545;
        }

        .bed-chip-label {
            font-size: 12px;
            font-weight: 700;
            color: #234;
        }

        .bed-chip-status {
            font-size: 11px;
            color: #5f6c7b;
        }

        .bed-chip-roll {
            font-size: 11px;
            color: #284b6d;
            font-weight: 700;
            word-break: break-word;
        }

        .room-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            padding-top: 10px;
            border-top: 1px solid #edf1f5;
            flex-wrap: wrap;
        }

        .room-footer .room-stats {
            flex: 1;
            min-width: 200px;
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

        .state-panel {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 24px;
            text-align: center;
            color: #666;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        .state-panel strong {
            color: #4A6FA5;
            display: block;
            margin-bottom: 8px;
            font-size: 16px;
        }

        @media (max-width: 768px) {
            .filter-controls {
                grid-template-columns: 1fr;
            }

            .rooms-grid {
                grid-template-columns: 1fr;
            }

            .floors-grid {
                grid-template-columns: 1fr;
            }

            .room-layout {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 1400px) {
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
        let allBlocks = [];
        let selectedBlock = '';
        const contextPath = '<%= request.getContextPath() %>';

        window.addEventListener('DOMContentLoaded', () => {
            initializeBlocks();
            loadStats();
            loadRooms();
        });

        function initializeBlocks() {
            const blockSelector = document.getElementById('blockSelector');
            ['', 'A', 'B', 'C', 'D'].forEach(block => {
                const btn = document.createElement('button');
                btn.type = 'button';
                btn.dataset.block = block;
                btn.className = 'block-btn' + (block === '' ? ' active' : '');
                btn.textContent = block === '' ? 'All Blocks' : 'Block ' + block;
                btn.onclick = () => selectBlock(block);
                blockSelector.appendChild(btn);
            });
        }

        function selectBlock(block) {
            selectedBlock = block;
            document.querySelectorAll('.block-btn').forEach((btn) => {
                btn.classList.toggle('active', (btn.dataset.block || '') === block);
            });
            document.getElementById('floorSelect').value = '';
            loadRooms();
        }

        function loadStats() {
            fetch(apiUrl('/api/rooms-vacancy?action=get_stats'))
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
            const grid = document.getElementById('roomsGrid');

            grid.innerHTML = '<div class="loading-spinner">Loading rooms...</div>';

            const queryParts = ['action=get_rooms'];
            if (block) {
                queryParts.push('block=' + encodeURIComponent(block));
            }
            if (floor) {
                queryParts.push('floor=' + encodeURIComponent(floor));
            }
            const url = apiUrl('/api/rooms-vacancy?' + queryParts.join('&'));

            fetch(url)
                .then(r => {
                    if (!r.ok) {
                        throw new Error('HTTP ' + r.status);
                    }

                    return r.json();
                })
                .then(data => {
                    const blocks = normalizeBlocksResponse(data);

                    if (blocks.length === 0) {
                        allBlocks = [];
                        renderStatePanel('No rooms found for the selected block/floor.', 'Try a different block or floor selection.');
                        return;
                    }

                    allBlocks = blocks;
                    applyFilter();
                })
                .catch(e => {
                    console.error('Error:', e);
                    allBlocks = [];
                    renderStatePanel('Unable to load room data.', 'Check that Tomcat is running and the /api/rooms-vacancy endpoint returns JSON.');
                });
        }

        function apiUrl(path) {
            return contextPath + path;
        }

        function normalizeBlocksResponse(data) {
            if (!data) {
                return [];
            }

            if (Array.isArray(data.blocks)) {
                return data.blocks;
            }

            if (Array.isArray(data)) {
                return data;
            }

            if (Array.isArray(data.rooms)) {
                return [{
                    blockName: selectedBlock,
                    floors: [{
                        floorNo: document.getElementById('floorSelect').value || '',
                        rooms: data.rooms
                    }]
                }];
            }

            return [];
        }

        function normalizeRoom(room, blockName, floorNo) {
            const beds = Array.isArray(room.beds) ? room.beds.map(bed => ({
                bedNo: String(bed.bedNo || bed.bed_number || bed.number || ''),
                status: String(bed.status || 'AVAILABLE').toUpperCase(),
                rollNo: String(bed.rollNo || bed.roll_no || '').trim(),
                bedId: String(bed.bedId || `${blockName}-${floorNo}-${room.roomNo || room.room_number || room.number}-${bed.bedNo || bed.bed_number || bed.number || ''}`)
            })) : [];

            const totalBeds = Number(room.totalBeds || beds.length || 0);
            const occupiedBeds = Number(room.occupiedBeds != null
                ? room.occupiedBeds
                : beds.filter(bed => bed.status === 'OCCUPIED').length);
            const availableBeds = Number(room.availableBeds != null
                ? room.availableBeds
                : Math.max(totalBeds - occupiedBeds, 0));

            return {
                roomNo: String(room.roomNo || room.room_number || room.number || room.roomId || ''),
                roomId: String(room.roomId || `${blockName}-${floorNo}-${room.roomNo || room.room_number || room.number || ''}`),
                beds,
                totalBeds,
                occupiedBeds,
                availableBeds,
                occupancyPercentage: totalBeds > 0
                    ? Math.round((occupiedBeds / totalBeds) * 100)
                    : 0
            };
        }

        function getVisibleRooms(rooms) {
            const filter = document.getElementById('filterSelect').value;
            if (filter === 'available') {
                return rooms.filter(room => room.availableBeds > 0);
            }
            if (filter === 'occupied') {
                return rooms.filter(room => room.occupiedBeds > 0);
            }
            return rooms;
        }

        function renderBlocks(blocks) {
            const grid = document.getElementById('roomsGrid');
            const filter = document.getElementById('filterSelect').value;
            const blockFilter = selectedBlock;

            const visibleBlocks = blocks
                .filter(block => !blockFilter || block.blockName === blockFilter)
                .map(block => {
                    const floors = Array.isArray(block.floors) ? block.floors : [];
                    const normalizedFloors = floors
                        .map(floor => {
                            const rooms = Array.isArray(floor.rooms) ? floor.rooms.map(room => normalizeRoom(room, block.blockName, floor.floorNo)) : [];
                            return {
                                floorNo: String(floor.floorNo || floor.number || ''),
                                rooms: getVisibleRooms(rooms)
                            };
                        })
                        .filter(floor => floor.rooms.length > 0);

                    return {
                        blockName: block.blockName,
                        floors: normalizedFloors
                    };
                })
                .filter(block => block.floors.length > 0);

            if (visibleBlocks.length === 0) {
                grid.innerHTML = '<div class="state-panel"><strong>No rooms match the selected filter</strong><div>Try selecting All Beds or change the block/floor.</div></div>';
                return;
            }

            const html = visibleBlocks.map(block => {
                const blockTotals = block.floors.reduce((acc, floor) => {
                    floor.rooms.forEach(room => {
                        acc.totalBeds += room.totalBeds;
                        acc.occupiedBeds += room.occupiedBeds;
                        acc.availableBeds += room.availableBeds;
                        acc.roomCount += 1;
                    });
                    return acc;
                }, { totalBeds: 0, occupiedBeds: 0, availableBeds: 0, roomCount: 0 });

                const blockOccupancy = blockTotals.totalBeds > 0
                    ? Math.round((blockTotals.occupiedBeds / blockTotals.totalBeds) * 100)
                    : 0;

                const floorsHtml = block.floors.map(floor => {
                    const floorTotals = floor.rooms.reduce((acc, room) => {
                        acc.totalBeds += room.totalBeds;
                        acc.occupiedBeds += room.occupiedBeds;
                        acc.availableBeds += room.availableBeds;
                        return acc;
                    }, { totalBeds: 0, occupiedBeds: 0, availableBeds: 0 });

                    const floorOccupancy = floorTotals.totalBeds > 0
                        ? Math.round((floorTotals.occupiedBeds / floorTotals.totalBeds) * 100)
                        : 0;

                    const roomsHtml = floor.rooms.map(room => {
                        const roomStateClass = room.availableBeds === room.totalBeds ? 'empty-room' : '';
                        const roomBadgeColor = room.occupancyPercentage === 0 ? '#28A745' : room.occupancyPercentage < 100 ? '#FFC107' : '#DC3545';

                        const bedHtml = room.beds.map(bed => {
                            const isOccupied = bed.status === 'OCCUPIED';
                            return `
                                <div class="bed-chip ${isOccupied ? 'occupied' : 'available'}">
                                    <div class="bed-chip-label">Bed ${bed.bedNo || '-'}</div>
                                    <div class="bed-chip-status">${isOccupied ? 'Allocated' : 'Available'}</div>
                                    <div class="bed-chip-roll">${isOccupied && bed.rollNo ? bed.rollNo : 'Open bed'}</div>
                                </div>
                            `;
                        }).join('');

                        return `
                            <div class="room-card ${roomStateClass}">
                                <div class="room-header">
                                    <div>
                                        <div class="room-number">Room ${room.roomNo || '-'}</div>
                                        <div class="room-meta">${block.blockName} Block • Floor ${floor.floorNo || '-'}</div>
                                    </div>
                                    <div class="occupancy-badge" style="background: ${roomBadgeColor};">${room.occupancyPercentage}% Full</div>
                                </div>

                                <div class="room-id-pill">${room.roomId}</div>

                                <div class="bed-grid">
                                    ${bedHtml}
                                </div>

                                <div class="room-footer">
                                    <div class="room-stats">
                                        <div class="room-stats-row"><span>Total Beds:</span><strong>${room.totalBeds}</strong></div>
                                        <div class="room-stats-row"><span>Occupied:</span><strong style="color: #DC3545;">${room.occupiedBeds}</strong></div>
                                        <div class="room-stats-row"><span>Available:</span><strong style="color: #28A745;">${room.availableBeds}</strong></div>
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('');

                    return `
                        <section class="floor-section">
                            <div class="floor-header">
                                <h3 class="floor-title">Floor ${floor.floorNo || '-'}</h3>
                                <div class="block-summary">${floorTotals.occupiedBeds}/${floorTotals.totalBeds} beds occupied • ${floorOccupancy}% occupancy</div>
                            </div>
                            <div class="room-layout">
                                ${roomsHtml}
                            </div>
                        </section>
                    `;
                }).join('');

                return `
                    <section class="block-section">
                        <div class="block-section-header">
                            <div>
                                <h2 class="block-title">Block ${block.blockName}</h2>
                                <div class="block-summary">${blockTotals.roomCount} rooms • ${blockTotals.occupiedBeds}/${blockTotals.totalBeds} beds occupied • ${blockOccupancy}% occupancy</div>
                            </div>
                            <div class="occupancy-badge" style="background: ${blockOccupancy === 0 ? '#28A745' : blockOccupancy < 100 ? '#FFC107' : '#DC3545'};">${blockOccupancy}% Full</div>
                        </div>
                        <div class="floors-grid">
                            ${floorsHtml}
                        </div>
                    </section>
                `;
            }).join('');

            grid.innerHTML = html;
        }

        function renderStatePanel(title, description) {
            const grid = document.getElementById('roomsGrid');
            grid.innerHTML = `
                <div class="state-panel">
                    <strong>${title}</strong>
                    <div>${description}</div>
                </div>
            `;
        }

        function applyFilter() {
            if (allBlocks.length === 0) {
                renderStatePanel('No room data loaded yet.', 'Try reloading the page or changing the block/floor selection.');
                return;
            }

            renderBlocks(allBlocks);
        }
    </script>
</body>
</html>
