// Hostel Allocation System - JavaScript Functions

/**
 * Validate Add Student Form
 */
function validateAddStudentForm(form) {
    const studentName = form.student_name.value.trim();
    const studentYear = form.student_year.value;
    const studentEmail = form.student_email.value.trim();

    if (!studentName) {
        alert('Please enter student name');
        form.student_name.focus();
        return false;
    }

    if (studentName.length < 3) {
        alert('Student name must be at least 3 characters long');
        form.student_name.focus();
        return false;
    }

    if (!studentYear) {
        alert('Please select student year');
        form.student_year.focus();
        return false;
    }

    if (!studentEmail) {
        alert('Please enter student email');
        form.student_email.focus();
        return false;
    }

    if (!isValidEmail(studentEmail)) {
        alert('Please enter a valid email address');
        form.student_email.focus();
        return false;
    }

    return true;
}

/**
 * Validate Email Format
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Format Date to readable format
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const notif = document.createElement('div');
    notif.className = `notification notification-${type}`;
    notif.textContent = message;
    notif.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 4px;
        color: white;
        font-weight: 600;
        z-index: 1000;
        animation: slideIn 0.3s ease-in;
    `;

    const bgColor = {
        success: '#28A745',
        error: '#DC3545',
        warning: '#FFC107',
        info: '#4A6FA5'
    }[type] || '#4A6FA5';

    notif.style.backgroundColor = bgColor;
    document.body.appendChild(notif);

    setTimeout(() => {
        notif.remove();
    }, 3000);
}

/**
 * Copy text to clipboard
 */
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
            showNotification('Copied to clipboard!', 'success');
        }).catch(() => {
            fallbackCopy(text);
        });
    } else {
        fallbackCopy(text);
    }
}

function fallbackCopy(text) {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    try {
        document.execCommand('copy');
        showNotification('Copied to clipboard!', 'success');
    } catch (err) {
        showNotification('Failed to copy', 'error');
    }
    document.body.removeChild(textArea);
}

/**
 * Format occupancy percentage
 */
function formatOccupancy(occupied, total) {
    if (total === 0) return '0%';
    return Math.round((occupied / total) * 100) + '%';
}

/**
 * Get status color based on occupancy
 */
function getStatusColor(occupied, total) {
    if (total === 0) return 'gray';
    const percentage = (occupied / total) * 100;
    if (percentage === 0) return '#28A745'; // Green - Available
    if (percentage < 100) return '#FFC107'; // Yellow - Partly Occupied
    return '#DC3545'; // Red - Fully Occupied
}

/**
 * Get status text based on occupancy
 */
function getStatusText(occupied, total) {
    if (total === 0) return 'No Beds';
    if (occupied === 0) return 'Available';
    if (occupied < total) return 'Partly Occupied';
    return 'Fully Occupied';
}

/**
 * Initialize form submission handlers
 */
document.addEventListener('DOMContentLoaded', function() {
    // Handle login form
    const loginForm = document.querySelector('form[action*="/login"]');
    if (loginForm) {
        loginForm.addEventListener('submit', function() {
            // Basic validation
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();
            const role = document.getElementById('role').value;

            if (!username) {
                alert('Please enter username');
                return false;
            }
            if (!password) {
                alert('Please enter password');
                return false;
            }
            if (!role) {
                alert('Please select a role');
                return false;
            }
        });
    }

    // Handle add student form
    const addStudentForm = document.querySelector('form[action*="/warden"]');
    if (addStudentForm) {
        addStudentForm.addEventListener('submit', function(e) {
            if (this.action.includes('add_student')) {
                if (!validateAddStudentForm(this)) {
                    e.preventDefault();
                    return false;
                }
            }
        });
    }

    // Add copy-to-clipboard functionality to credential codes
    const codeTags = document.querySelectorAll('.credentials-box code');
    codeTags.forEach(code => {
        code.style.cursor = 'pointer';
        code.title = 'Click to copy';
        code.addEventListener('click', function(e) {
            e.stopPropagation();
            copyToClipboard(this.textContent);
        });
    });

    // Load dashboard data if on warden dashboard
    if (document.getElementById('students-list')) {
        loadDashboardStats();
        loadRegisteredStudents();
        loadAllocatedStudents();
    }
});

/**
 * Logout with confirmation
 */
function confirmLogout() {
    if (confirm('Are you sure you want to logout?')) {
        window.location.href = '/hostel-allocation/jsp/login.jsp';
        return true;
    }
    return false;
}

/**
 * Load dashboard stats
 */
function loadDashboardStats() {
    fetch('/hostel-allocation/api/dashboard-stats')
        .then(response => response.json())
        .then(data => {
            // Update stats cards by ID
            document.getElementById('total-students').textContent = data.totalStudents;
            document.getElementById('registered-students').textContent = data.registeredStudents;
            document.getElementById('allocated-students').textContent = data.allocatedStudents;
            document.getElementById('total-beds').textContent = data.totalBeds;
            document.getElementById('available-beds').textContent = data.availableBeds;
            document.getElementById('occupied-beds').textContent = data.occupiedBeds;

            // Update occupancy bar
            const occupancyPercent = data.totalBeds > 0 ? (data.occupiedBeds / data.totalBeds * 100) : 0;
            const occupancyFill = document.querySelector('.occupancy-fill');
            const occupancyText = document.querySelector('.occupancy-text');

            if (occupancyFill) {
                occupancyFill.style.width = occupancyPercent + '%';
            }
            if (occupancyText) {
                occupancyText.textContent = occupancyPercent.toFixed(1) + '% Occupied';
            }
        })
        .catch(error => {
            console.error('Error loading dashboard stats:', error);
        });
}

/**
 * Load allocated students
 */
function loadAllocatedStudents() {
    const allocatedList = document.getElementById('allocated-students-list');

    if (!allocatedList) {
        return; // Only run on warden dashboard
    }

    // Fetch allocated students from server
    fetch('/hostel-allocation/api/allocated-students', {
        method: 'GET'
    })
    .then(response => response.json())
    .then(data => {
        displayAllocatedStudents(data);
    })
    .catch(error => {
        console.error('Error fetching allocated students:', error);
        if (allocatedList) {
            allocatedList.innerHTML = `
                <div class="no-students-msg">
                    <p>❌ Error loading allocated students.</p>
                    <p>Please refresh the page.</p>
                </div>
            `;
        }
    });
}

/**
 * Refresh dashboard data
 */
function refreshDashboard() {
    location.reload();
}

/**
 * Export data as CSV
 */
function exportTableAsCSV(tableId, filename = 'export.csv') {
    const table = document.getElementById(tableId);
    if (!table) {
        alert('Table not found');
        return;
    }

    let csv = [];
    const rows = table.querySelectorAll('tr');

    rows.forEach(row => {
        const cols = row.querySelectorAll('td, th');
        const csvRow = [];
        cols.forEach(col => {
            csvRow.push(col.innerText);
        });
        csv.push(csvRow.join(','));
    });

    downloadCSV(csv.join('\n'), filename);
}

/**
 * Download CSV file
 */
function downloadCSV(csv, filename) {
    const csvFile = new Blob([csv], { type: 'text/csv' });
    const downloadLink = document.createElement('a');
    downloadLink.href = URL.createObjectURL(csvFile);
    downloadLink.download = filename;
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}

/**
 * Debounce function for form inputs
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Handle room filter changes
 */
function filterRooms(blockId, floorId) {
    const roomCards = document.querySelectorAll('.room-card');
    let visibleCount = 0;

    roomCards.forEach(card => {
        const cardBlock = card.dataset.block;
        const cardFloor = card.dataset.floor;

        const matchBlock = !blockId || cardBlock === blockId;
        const matchFloor = !floorId || cardFloor === floorId;

        if (matchBlock && matchFloor) {
            card.style.display = 'block';
            visibleCount++;
        } else {
            card.style.display = 'none';
        }
    });

    // Show message if no rooms match
    const message = visibleCount === 0 ? 'No rooms found' : `${visibleCount} room(s) found`;
    console.log(message);
}

/**
 * Keyboard shortcut for quick actions
 */
document.addEventListener('keydown', function(event) {
    // Ctrl/Cmd + Enter to submit form
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
        const form = document.querySelector('form');
        if (form) {
            form.submit();
        }
    }

    // Escape to go back
    if (event.key === 'Escape') {
        history.back();
    }
});

/**
 * Load and display registered students
 */
function loadRegisteredStudents() {
    const studentsList = document.getElementById('students-list');
    const allocatedList = document.getElementById('allocated-students-list');

    if (!studentsList && !allocatedList) {
        return; // Only run on warden dashboard
    }

    // Fetch unallocated students from server
    fetch('/hostel-allocation/api/registered-students', {
        method: 'GET'
    })
    .then(response => response.json())
    .then(data => {
        if (studentsList) {
            displayUnallocatedStudents(data);
        }
    })
    .catch(error => {
        console.error('Error fetching students:', error);
        if (studentsList) {
            fetchAndDisplayMockStudents();
        }
    });
}

/**
 * Display unallocated students
 */
function displayUnallocatedStudents(students) {
    const studentsList = document.getElementById('students-list');

    if (students.length === 0) {
        studentsList.innerHTML = `
            <div class="no-students-msg">
                <p>📝 No unallocated students.</p>
                <p>All registered students have been allocated rooms!</p>
            </div>
        `;
        return;
    }

    let html = `
        <div class="students-table-header">
            <div class="table-header-row">
                <div class="header-cell">Name</div>
                <div class="header-cell">Roll No.</div>
                <div class="header-cell">Year</div>
                <div class="header-cell">Email</div>
                <div class="header-cell">Status</div>
                <div class="header-cell">Action</div>
            </div>
        </div>
        <div class="students-table-body">
    `;

    students.forEach((student, index) => {
        const roll = student.rollNumber || student.rollNo;
        html += `
            <div class="table-row ${index % 2 === 0 ? 'even' : 'odd'}">
                <div class="table-cell">${student.name}</div>
                <div class="table-cell"><code>${roll}</code></div>
                <div class="table-cell"><span class="year-badge">Year ${student.year}</span></div>
                <div class="table-cell">${student.email}</div>
                <div class="table-cell"><span class="status-badge status-${student.status}">${student.status}</span></div>
                <div class="table-cell">
                    <a href="/hostel-allocation/jsp/allocation.jsp?student_id=${roll}" class="copy-btn" style="text-decoration:none;">
                        🎯 Allocate
                    </a>
                </div>
            </div>
        `;
    });

    html += `
        </div>
    `;

    studentsList.innerHTML = html;
}

/**
 * Display allocated students
 */
function displayAllocatedStudents(students) {
    const allocatedList = document.getElementById('allocated-students-list');

    if (students.length === 0) {
        allocatedList.innerHTML = `
            <div class="no-students-msg">
                <p>🏠 No students allocated yet.</p>
                <p>Use the allocation page to assign rooms to students.</p>
            </div>
        `;
        return;
    }

    let html = `
        <div class="students-table-header">
            <div class="table-header-row">
                <div class="header-cell">Name</div>
                <div class="header-cell">Roll No.</div>
                <div class="header-cell">Year</div>
                <div class="header-cell">Email</div>
                <div class="header-cell">Room</div>
                <div class="header-cell">Bed</div>
            </div>
        </div>
        <div class="students-table-body">
    `;

    students.forEach((student, index) => {
        const roll = student.rollNumber || student.rollNo;
        const roomInfo = student.roomNo ? `${student.block}-${student.floor}-${student.roomNo}` : 'N/A';
        const bedInfo = student.bedNo || 'N/A';

        html += `
            <div class="table-row ${index % 2 === 0 ? 'even' : 'odd'}">
                <div class="table-cell">${student.name}</div>
                <div class="table-cell"><code>${roll}</code></div>
                <div class="table-cell"><span class="year-badge">Year ${student.year}</span></div>
                <div class="table-cell">${student.email}</div>
                <div class="table-cell"><span class="room-badge">${roomInfo}</span></div>
                <div class="table-cell"><span class="bed-badge">${bedInfo}</span></div>
            </div>
        `;
    });

    html += `
        </div>
    `;

    allocatedList.innerHTML = html;
}

/**
 * Mock display for students (fallback)
 */
function fetchAndDisplayMockStudents() {
    const studentsList = document.getElementById('students-list');

    const mockStudents = [
        {
            id: 'S001',
            name: 'Student One',
            year: 1,
            email: 'student1@college.edu',
            username: 'stu001',
            password: 'Not available'
        },
        {
            id: 'S002',
            name: 'Student Two',
            year: 2,
            email: 'student2@college.edu',
            username: 'stu002',
            password: 'Not available'
        }
    ];

    if (mockStudents.length === 0) {
        studentsList.innerHTML = `
            <div class="no-students-msg">
                <p>📝 No students registered yet.</p>
                <p>Use the form above to add new students.</p>
            </div>
        `;
        return;
    }

    let html = `
        <div class="students-table-header">
            <div class="table-header-row">
                <div class="header-cell">Name</div>
                <div class="header-cell">Year</div>
                <div class="header-cell">Email</div>
                <div class="header-cell">Credentials</div>
                <div class="header-cell">Action</div>
            </div>
        </div>
        <div class="students-table-body">
    `;

    mockStudents.forEach((student, index) => {
        const credentials = `Username: ${student.username}\nPassword: Ask warden`;
        html += `
            <div class="table-row ${index % 2 === 0 ? 'even' : 'odd'}">
                <div class="table-cell">${student.name}</div>
                <div class="table-cell"><span class="year-badge">Year ${student.year}</span></div>
                <div class="table-cell">${student.email}</div>
                <div class="table-cell">
                    <div class="credentials-preview">
                        <small>Username: <strong>${student.username}</strong></small><br>
                        <small>Password: <strong>••••••••</strong></small>
                    </div>
                </div>
                <div class="table-cell">
                    <button class="copy-btn" onclick="copyStudentCredentialsForWhatsApp('${student.name}', '${student.year}', '${student.username}')">
                        📋 Copy
                    </button>
                </div>
            </div>
        `;
    });

    html += `
        </div>
    `;

    studentsList.innerHTML = html;
}

/**
 * Copy student credentials for WhatsApp
 */
function copyStudentCredentialsForWhatsApp(name, year, username) {
    const message = `🏨 Hostel Allocation - Student Login Details

👤 Name: ${name}
📊 Year: ${year}
👤 Username: ${username}
🔐 Password: [Ask Warden For Password]

📍 Login URL: http://localhost:8080/hostel-allocation/

Questions? Contact Warden Office ✓`;

    copyToClipboard(message);
}

/**
 * Show notification
 */
function showNotification(message, type) {
    // Simple alert for now - can be enhanced with proper notification system
    alert(message);
}
