// Room Allocation Management

let selectedBlock = 'A';
let selectedFloor = '';
let selectedStudent = null;
let selectedBedData = null;
let registeredStudents = [];

/**
 * Get URL parameter
 */
function getQueryParam(name) {
    const url = new URL(window.location);
    return url.searchParams.get(name);
}

/**
 * Select block
 */
window.selectBlock = function(btn, blockId) {
    document.querySelectorAll('.block-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    selectedBlock = blockId;
    loadRooms();
};

/**
 * Select floor
 */
window.selectFloor = function(floor) {
    selectedFloor = floor;
    if (floor) {
        loadRooms();
    }
};

/**
 * Load rooms for selected block and floor
 */
function loadRooms() {
    if (!selectedBlock || !selectedFloor) {
        document.getElementById('rooms-container').innerHTML = '<p style="text-align: center; color: #999;">Please select block and floor</p>';
        return;
    }

    const roomsContainer = document.getElementById('rooms-container');
    roomsContainer.innerHTML = '<p style="text-align: center;">Loading rooms...</p>';

    // Fetch real rooms from server
    fetch(`/hostel-allocation/api/rooms?block=${selectedBlock}&floor=${selectedFloor}`)
        .then(response => response.json())
        .then(data => {
            displayRooms(data);
        })
        .catch(error => {
            console.error('Error loading rooms:', error);
            // Fallback to mock data
            const mockRooms = generateMockRooms(selectedBlock, selectedFloor);
            displayRooms(mockRooms);
        });
}

/**
 * Generate mock room data
 */
function generateMockRooms(block, floor) {
    const rooms = [];
    const baseRoomNum = floor * 100;

    for (let i = 1; i <= 5; i++) {
        const roomNum = baseRoomNum + i;
        const roomId = `${block}-${floor}-${i}`;

        // Mock bed statuses
        const bedStatuses = ['AVAILABLE', 'OCCUPIED', 'AVAILABLE'].map((status, idx) => ({
            bed_id: `${roomId}-${idx + 1}`,
            bed_number: idx + 1,
            status: status
        }));

        rooms.push({
            room_id: roomId,
            room_number: roomNum,
            block_id: block,
            floor_id: floor,
            beds: bedStatuses
        });
    }

    return rooms;
}

/**
 * Display rooms as cards
 */
function displayRooms(rooms) {
    const roomsContainer = document.getElementById('rooms-container');
    let html = '';

    if (rooms.length === 0) {
        roomsContainer.innerHTML = '<p style="text-align: center; color: #999;">No rooms available</p>';
        return;
    }

    rooms.forEach(room => {
        const occupancyStatus = getOccupancyStatus(room.beds);
        const occupancyColor = getOccupancyColor(occupancyStatus);

        html += `
            <div class="room-card allocation-room-card">
                <div class="room-card-header">
                    <h3>Room ${room.roomNo}</h3>
                    <span class="occupancy-badge" style="background: ${occupancyColor};">
                        ${occupancyStatus}
                    </span>
                </div>

                <div class="beds-grid">
                    ${room.beds.map(bed => `
                        <button
                            class="bed-icon bed-${bed.status.toLowerCase()}"
                            title="Bed ${bed.bedNo} - ${bed.status} ${bed.rollNumber ? '(' + bed.rollNumber + ')' : ''}"
                            onclick="selectBed(event, '${room.roomNo}', '${bed.bedNo}', '${bed.status}', 'Room ${room.roomNo}')"
                            ${bed.status.toLowerCase() !== 'available' ? 'disabled' : ''}
                        >
                            🛏️
                        </button>
                    `).join('')}
                </div>

                <div class="room-controls">
                    <button class="btn btn-sm btn-success" onclick="addBed('${room.roomNo}')">+ Bed</button>
                    <button class="btn btn-sm btn-danger" onclick="removeBed('${room.roomNo}')">- Bed</button>
                </div>

                <div class="bed-legend">
                    <span><span class="bed-legend-icon bed-available"></span> Available</span>
                    <span><span class="bed-legend-icon bed-occupied"></span> Occupied</span>
                </div>
            </div>
        `;
    });

    roomsContainer.innerHTML = html;
}

/**
 * Get occupancy status
 */
function getOccupancyStatus(beds) {
    const occupied = beds.filter(b => b.status === 'occupied').length;
    const total = beds.length;

    if (occupied === 0) return 'Available';
    if (occupied === total) return 'Fully Occupied';
    return 'Partly Occupied';
}

/**
 * Get occupancy color
 */
function getOccupancyColor(status) {
    switch(status) {
        case 'Available': return '#28A745';
        case 'Partly Occupied': return '#FFC107';
        case 'Fully Occupied': return '#DC3545';
        default: return '#999';
    }
}

/**
 * Load unallocated students
 */
function loadStudents() {
    const studentSelect = document.getElementById('student-select');
    const preSelectedId = getQueryParam('student_id');

    // Fetch real students from server
    fetch('/hostel-allocation/api/registered-students')
        .then(response => response.json())
        .then(data => {
            console.log("Registered students:", data);
            registeredStudents = data; // Store in global array
            let html = '<option value="">-- Select Registered Student --</option>';
            data.forEach(student => {
                const roll = student.rollNumber || student.rollNo;
                html += `<option value="${roll}" data-name="${student.name}" data-year="${student.year}" data-email="${student.email}" data-roll="${roll}">
                    ${student.name} (Roll: ${roll}) - Year ${student.year}
                </option>`;
            });

            studentSelect.innerHTML = html;

            // Pre-select student if passed in URL
            if (preSelectedId) {
                studentSelect.value = preSelectedId;
                loadStudentDetails();
            }
        })
        .catch(error => {
            console.error('Error loading students:', error);
            // Show error message instead of fallback data
            const studentSelect = document.getElementById('student-select');
            studentSelect.innerHTML = '<option value="">-- Error loading students --</option>';
            alert('Error loading students. Please refresh the page.');
        });
}

/**
 * Load selected student details
 */
function loadStudentDetails() {
    const studentSelect = document.getElementById('student-select');
    const selectedValue = studentSelect.value;

    if (!selectedValue) {
        alert('Please select a student');
        return;
    }

    selectedStudent = registeredStudents.find(s => (s.rollNumber || s.rollNo) === selectedValue);

    if (!selectedStudent) {
        alert('Student not found');
        return;
    }

    // Ensure rollNumber is set for consistency
    selectedStudent.rollNumber = selectedStudent.rollNumber || selectedStudent.rollNo;

    // Show student details
    document.getElementById('student-name').textContent = selectedStudent.name;
    document.getElementById('student-year').textContent = `Year ${selectedStudent.year}`;
    document.getElementById('student-email').textContent = selectedStudent.email;
    document.getElementById('student-details').style.display = 'block';

    // No form to update anymore - allocation is done via AJAX
}

/**
 * Select a bed
 */
function selectBed(event, roomId, bedId, status, roomName) {
    event.preventDefault();

    if (status.toLowerCase() !== 'available') {
        alert('This bed is not available for allocation');
        return;
    }

    if (!selectedStudent) {
        alert('Please select a student first');
        return;
    }

    // Validate floor selection before allowing bed selection
    if (!selectedFloor) {
        alert("Please select a floor first");
        return;
    }

    selectedBedData = {
        room_id: roomId,
        bedNo: bedId,
        room_name: roomName
    };

    // Validate all required data before showing confirmation
    if (!selectedStudent) {
        alert('Please select a student first');
        return;
    }
    if (!selectedBlock || !selectedFloor || !roomId || !bedId || !selectedStudent.rollNumber) {
        alert("Missing data. Please select student, block, floor, room and bed again.");
        console.log({
            rollNumber: selectedStudent.rollNumber,
            block: selectedBlock,
            floor: selectedFloor,
            roomNo: roomId,
            bedNo: bedId
        });
        return;
    }

    // Show confirmation
    if (confirm(`Allocate Room ${roomId}, Bed ${bedId} to ${selectedStudent.name} (Roll No: ${selectedStudent.rollNumber})?`)) {

        // Submit via AJAX with JSON
        const allocationData = {
            rollNumber: selectedStudent.rollNumber,
            block: selectedBlock,
            floor: selectedFloor,
            roomNo: roomId,
            bedNo: bedId
        };

        console.log("Sending allocation data:", allocationData);

        fetch('/hostel-allocation/api/allocate-room', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(allocationData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message || 'Room allocated successfully!');
                // Reset selection state
                selectedStudent = null;
                selectedBedData = null;
                // Reset UI elements
                document.getElementById('student-select').value = '';
                document.getElementById('student-details').style.display = 'none';
                // Reload data to reflect changes
                loadStudents();
                loadRooms();
                if (typeof loadDashboardStats === "function" && document.getElementById('total-students')) {
                    loadDashboardStats();
                }
                if (typeof loadAllocatedStudents === "function") {
                    loadAllocatedStudents();
                }
                resetAllocation();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while allocating the bed');
        });
    }
}

/**
 * Confirm allocation
 */
function confirmAllocation() {
    if (!selectedStudent || !selectedBedData) {
        alert('Please complete the selection');
        return false;
    }

    if (!selectedBlock || !selectedFloor || !selectedBedData.room_id || !selectedBedData.bedNo || !selectedStudent.rollNumber) {
        alert("Missing data. Please select student, block, floor, room and bed again.");
        console.log({
            rollNumber: selectedStudent.rollNumber,
            block: selectedBlock,
            floor: selectedFloor,
            roomNo: selectedBedData.room_id,
            bedNo: selectedBedData.bedNo
        });
        return false;
    }

    if (confirm(`Allocate Room ${selectedBedData.room_id}, Bed ${selectedBedData.bedNo} to ${selectedStudent.name} (Roll No: ${selectedStudent.rollNumber})?`)) {

        const allocationData = {
            rollNumber: selectedStudent.rollNumber,
            block: selectedBlock,
            floor: selectedFloor,
            roomNo: selectedBedData.room_id,
            bedNo: selectedBedData.bedNo
        };

        fetch('/hostel-allocation/api/allocate-room', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(allocationData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message || 'Room allocated successfully!');
                // Reset selection state
                selectedStudent = null;
                selectedBedData = null;
                // Reset UI elements
                document.getElementById('student-select').value = '';
                document.getElementById('student-details').style.display = 'none';
                // Reload data to reflect changes
                loadStudents();
                loadRooms();
                if (typeof loadDashboardStats === "function" && document.getElementById('total-students')) {
                    loadDashboardStats();
                }
                if (typeof loadAllocatedStudents === "function") {
                    loadAllocatedStudents();
                }
                resetAllocation();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while allocating the bed');
        });
    }

    return false;
}
/**
 * Reset allocation
 */
function resetAllocation() {
    selectedStudent = null;
    selectedBedData = null;

    // Safely hide elements that may not exist
    const form = document.getElementById('allocation-form');
    if (form) form.style.display = 'none';

    const summary = document.getElementById('allocation-summary');
    if (summary) summary.style.display = 'none';

    document.getElementById('student-details').style.display = 'none';
    document.getElementById('student-select').value = '';
    document.querySelectorAll('.bed-icon').forEach(b => b.classList.remove('selected'));
}

/**
 * Initialize on page load
 */
document.addEventListener('DOMContentLoaded', function() {
    loadStudents();
    loadRooms();
});

/**
 * Add a bed to a room
 */
function addBed(roomNumber) {
    if (!selectedBlock || !selectedFloor) {
        alert('Please select a block and floor first');
        return;
    }

    const bedNumber = prompt('Enter bed number to add:');
    if (!bedNumber || bedNumber.trim() === '') {
        return;
    }

    if (confirm(`Are you sure you want to add bed ${bedNumber} to room ${roomNumber}?`)) {
        const formData = new URLSearchParams();
        formData.append('action', 'addBed');
        formData.append('blockName', selectedBlock);
        formData.append('floorNumber', selectedFloor);
        formData.append('roomNumber', roomNumber);
        formData.append('bedNumber', bedNumber);

        fetch('/hostel-allocation/structure', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Bed added successfully!');
                loadRooms(); // Reload rooms to show the new bed
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while adding the bed');
        });
    }
}

/**
 * Remove a bed from a room
 */
function removeBed(roomNumber) {
    if (!selectedBlock || !selectedFloor) {
        alert('Please select a block and floor first');
        return;
    }

    const bedNumber = prompt('Enter bed number to remove:');
    if (!bedNumber || bedNumber.trim() === '') {
        return;
    }

    if (confirm(`Are you sure you want to remove bed ${bedNumber} from room ${roomNumber}?`)) {
        const formData = new URLSearchParams();
        formData.append('action', 'removeBed');
        formData.append('blockName', selectedBlock);
        formData.append('floorNumber', selectedFloor);
        formData.append('roomNumber', roomNumber);
        formData.append('bedNumber', bedNumber);

        fetch('/hostel-allocation/structure', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Bed removed successfully!');
                loadRooms(); // Reload rooms to reflect the removal
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while removing the bed');
        });
    }
}
