// Room Allocation Management

let selectedBlock = 'A';
let selectedFloor = '1';
let selectedStudent = null;
let selectedBedData = null;

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
function selectBlock(btn, blockId) {
    document.querySelectorAll('.block-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    selectedBlock = blockId;
    loadRooms();
}

/**
 * Select floor
 */
function selectFloor(floor) {
    selectedFloor = floor;
    if (floor) {
        loadRooms();
    }
}

/**
 * Load rooms for selected block and floor
 */
function loadRooms() {
    if (!selectedFloor) {
        document.getElementById('rooms-container').innerHTML = '<p style="text-align: center; color: #999;">Please select block and floor</p>';
        return;
    }

    const roomsContainer = document.getElementById('rooms-container');
    roomsContainer.innerHTML = '<p style="text-align: center;">Loading rooms...</p>';

    // In production, this would be a fetch to a servlet
    // For now, we'll create mock data
    const mockRooms = generateMockRooms(selectedBlock, selectedFloor);
    displayRooms(mockRooms);
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
                    <h3>Room ${room.room_number}</h3>
                    <span class="occupancy-badge" style="background: ${occupancyColor};">
                        ${occupancyStatus}
                    </span>
                </div>

                <div class="beds-grid">
                    ${room.beds.map(bed => `
                        <button
                            class="bed-icon bed-${bed.status.toLowerCase()}"
                            title="Bed ${bed.bed_number} - ${bed.status}"
                            onclick="selectBed(event, '${room.room_id}', '${bed.bed_id}', '${bed.bed_number}', '${bed.status}', 'Room ${room.room_number}')"
                            ${bed.status !== 'AVAILABLE' ? 'disabled' : ''}
                        >
                            🛏️
                        </button>
                    `).join('')}
                </div>

                <div class="bed-legend">
                    <span><span class="bed-legend-icon bed-available"></span> Available</span>
                    <span><span class="bed-legend-icon bed-reserved"></span> Reserved</span>
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
    const occupied = beds.filter(b => b.status === 'OCCUPIED').length;
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
    fetch('/hostel-allocation/allocate?action=get_students')
        .then(response => response.json())
        .then(data => {
            let html = '<option value="">-- Select Unallocated Student --</option>';
            data.forEach(student => {
                html += `<option value="${student.student_id}" data-name="${student.name}" data-year="${student.year}" data-email="${student.email}" data-roll="${student.roll_number}">
                    ${student.name} (Roll: ${student.roll_number}) - Year ${student.year}
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
            // Fallback to mock data
            let html = '<option value="">-- Select Unallocated Student --</option>';
            const mockStudents = [
                { student_id: 'S002', name: 'Rishitha', year: 1, email: 'rishithagaddam79@gmail.com', roll_number: '21BCE001' }
            ];
            mockStudents.forEach(student => {
                html += `<option value="${student.student_id}" data-name="${student.name}" data-year="${student.year}" data-email="${student.email}" data-roll="${student.roll_number}">
                    ${student.name} (Roll: ${student.roll_number}) - Year ${student.year}
                </option>`;
            });
            studentSelect.innerHTML = html;
        });
}

/**
 * Load selected student details
 */
function loadStudentDetails() {
    const studentSelect = document.getElementById('student-select');
    const selectedOption = studentSelect.options[studentSelect.selectedIndex];

    if (!selectedOption.value) {
        alert('Please select a student');
        return;
    }

    selectedStudent = {
        id: selectedOption.value,
        name: selectedOption.dataset.name,
        year: selectedOption.dataset.year,
        email: selectedOption.dataset.email
    };

    // Show student details
    document.getElementById('student-name').textContent = selectedStudent.name;
    document.getElementById('student-year').textContent = `Year ${selectedStudent.year}`;
    document.getElementById('student-email').textContent = selectedStudent.email;
    document.getElementById('student-details').style.display = 'block';

    // Update form
    document.getElementById('selected-student').value = selectedStudent.id;
}

/**
 * Select a bed
 */
function selectBed(event, roomId, bedId, bedNum, status, roomName) {
    event.preventDefault();

    if (status !== 'AVAILABLE') {
        alert('This bed is not available for allocation');
        return;
    }

    if (!selectedStudent) {
        alert('Please select a student first');
        return;
    }

    selectedBedData = {
        room_id: roomId,
        bed_id: bedId,
        bed_number: bedNum,
        room_name: roomName
    };

    // Update form
    document.getElementById('selected-block').value = selectedBlock;
    document.getElementById('selected-floor').value = selectedFloor;
    document.getElementById('selected-room').value = roomId;
    document.getElementById('selected-bed').value = bedId;

    // Show summary
    document.getElementById('summary-student').textContent = selectedStudent.name;
    document.getElementById('summary-block').textContent = `Block ${selectedBlock}`;
    document.getElementById('summary-floor').textContent = `Floor ${selectedFloor}`;
    document.getElementById('summary-room').textContent = roomName;
    document.getElementById('summary-bed').textContent = `Bed ${bedNum}`;

    document.getElementById('allocation-summary').style.display = 'block';
    document.getElementById('allocation-form').style.display = 'block';

    // Highlight selected bed
    document.querySelectorAll('.bed-icon').forEach(b => b.classList.remove('selected'));
    event.target.classList.add('selected');
}

/**
 * Confirm allocation
 */
function confirmAllocation() {
    if (!selectedStudent || !selectedBedData) {
        alert('Please complete the selection');
        return false;
    }

    if (confirm(`Allocate Bed ${selectedBedData.bed_number} in ${selectedBedData.room_name} to ${selectedStudent.name}?`)) {
        // Submit via AJAX
        const formData = new FormData();
        formData.append('action', 'allocate_bed');
        formData.append('student_id', selectedStudent.id);
        formData.append('bed_id', document.getElementById('selected-bed').value);
        formData.append('room_id', document.getElementById('selected-room').value);

        fetch('/hostel-allocation/allocate', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNotification('Bed allocated successfully!', 'success');
                setTimeout(() => {
                    window.location.href = '/hostel-allocation/jsp/warden-dashboard.jsp?success=Bed+allocated+successfully';
                }, 1500);
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while allocating the bed');
        });

        return false;
    }
    return false;
}

/**
 * Reset allocation
 */
function resetAllocation() {
    selectedStudent = null;
    selectedBedData = null;
    document.getElementById('allocation-form').style.display = 'none';
    document.getElementById('allocation-summary').style.display = 'none';
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
