# Hostel Room Allocation System

A full-stack web-based hostel management system built with Java Servlets, JSP, and XML storage on Apache Tomcat.

## Features

✅ **Warden (Admin) Dashboard**
- Add new students (auto-generates username and password)
- Real-time occupancy dashboard
- View statistics (total beds, occupied, available, total students)
- Room allocation status overview

✅ **Student Portal**
- Secure login with generated credentials
- View allocated room details (block, floor, room, bed)
- Room image and bed layout visualization
- Hostel rules and features information

✅ **Automatic Room Allocation**
- Students grouped by year
- Sequential bed assignment within year groups
- Real-time bed status updates

✅ **Responsive Design**
- Modern card-based UI with blue/purple theme
- Mobile-friendly interface
- Fully responsive across all devices

✅ **Data Persistence**
- XML-based storage (no database required)
- Separate files for students, rooms, and allocations
- Structured hostel layout (Blocks → Floors → Rooms → Beds)

## System Architecture

```
Frontend (HTML/CSS/JS)
    ↓
Servlets (Business Logic)
    ↓
XML Files (Data Storage)
```

### Project Structure

```
hostel-allocation/
├── WEB-INF/
│   ├── web.xml (servlet mappings)
│   ├── classes/com/hostel/ (compiled Java classes)
│   └── lib/
├── jsp/ (JSP pages)
│   ├── login.jsp
│   ├── warden-dashboard.jsp
│   ├── student-dashboard.jsp
│   └── error.jsp
├── css/ (stylesheets)
│   ├── style.css (main styles)
│   └── responsive.css (mobile responsive)
├── js/ (JavaScript)
│   └── app.js (validation & interactivity)
├── data/ (XML files)
│   ├── students.xml
│   ├── rooms.xml
│   └── allocations.xml
├── images/ (room images)
├── index.html (entry point)
```

## Java Classes

### Core Classes

1. **XMLManager.java**
   - Reads/writes XML files
   - Manages student, room, and allocation data
   - Provides queries for finding available beds

2. **PasswordUtils.java**
   - Generates random passwords (8 characters)
   - Generates usernames from student names
   - MD5 password hashing/verification

3. **AllocationEngine.java**
   - Core room allocation algorithm
   - Groups students by year
   - Calculates occupancy statistics

### Servlets

1. **LoginServlet**
   - Authenticates warden and students
   - Creates session for logged-in users
   - Redirects based on user role

2. **WardenServlet**
   - Handles student registration
   - Displays occupancy dashboard
   - Manages room allocations

3. **StudentServlet**
   - Displays allocated room details
   - Returns room information
   - Shows student's assigned bed

## Demo Credentials

**Warden Login:**
- Username: `warden`
- Password: `warden123`
- Role: Warden (Admin)

**Student Credentials:**
- Generated automatically by warden when adding a new student
- Format: 3 chars from first name + 3 chars from last name + 3 random digits

## Hostel Structure

- **Blocks:** 3 (A, B, C)
- **Floors per Block:** 3
- **Rooms per Floor:** 5
- **Beds per Room:** 3
- **Total:** 135 rooms, 405 beds

Room numbering:
- Block A, Floor 1: Rooms 101-105
- Block A, Floor 2: Rooms 201-205
- Block A, Floor 3: Rooms 301-305
- (Same pattern for Blocks B and C)

## How to Use

### Starting the System

1. **Ensure Apache Tomcat is running**
   ```bash
   # On Windows, run Tomcat startup script
   cd apache-tomcat-9.0.117/bin
   catalina.bat start
   ```

2. **Access the application**
   - Open browser and go to: `http://localhost:8080/hostel-allocation/`
   - You'll be redirected to the login page

### Warden Workflow

1. **Login**
   - Username: `warden`
   - Password: `warden123`
   - Role: Warden

2. **Add Student**
   - Enter student name, year, and email
   - System auto-generates username and password
   - Credentials are displayed - save and provide to student

3. **View Dashboard**
   - See occupancy statistics
   - View total/occupied/available beds
   - Monitor student registrations

### Student Workflow

1. **Login**
   - Use username and password provided by warden
   - Role: Student

2. **View Room Details**
   - See allocated block, floor, room, and bed number
   - View room image
   - Read hostel rules and features

## Technical Details

### Authentication
- Session-based authentication using HttpSession
- Warden has role = "warden"
- Students have role = "student"
- Passwords hashed using MD5

### Room Allocation Algorithm

```
When adding student with year Y:
1. Query available beds in allocations.xml
2. Filter beds for year Y (from previous allocations)
3. If no beds in year Y, get first available bed overall
4. Assign bed sequentially
5. Update room status to OCCUPIED
6. Create allocation record
```

### XML Data Format

**students.xml:** Stores student records with ID, name, email, credentials, year, role

**rooms.xml:** Stores hostel structure with blocks, floors, rooms, beds, and bed statuses

**allocations.xml:** Stores allocation records linking students to beds

## Compilation

Java classes are pre-compiled in `WEB-INF/classes/com/hostel/`

To recompile:
```bash
cd WEB-INF/classes
javac -cp "path-to-tomcat/lib/servlet-api.jar" -d . com/hostel/*.java
```

## Color Scheme

- **Primary:** Blue (#4A6FA5)
- **Accent:** Purple (#8B6FA0)
- **Available:** Green (#28A745)
- **Partly Occupied:** Yellow (#FFC107)
- **Fully Occupied:** Red (#DC3545)

## Browser Support

- Chrome/Edge: Full support
- Firefox: Full support
- Safari: Full support
- IE 11: Limited support (CSS Grid not fully supported)

## Known Limitations

1. No user authentication beyond basic login
2. No email verification
3. XML files not synchronized across multiple instances
4. No real-time updates (requires page refresh)
5. Single-server deployment only

## Future Enhancements

- Database integration (MySQL/PostgreSQL)
- Real-time updates using WebSockets
- Email notifications
- Payment integration
- Room change requests
- Hostel fee management
- Attendance tracking
- Mobile app

## Troubleshooting

### Can't login
- Verify username and password are correct
- Check selected role (Warden or Student)
- Check if Tomcat is running and app is deployed

### Room not allocated
- Warden needs to add the student first
- Verify student year is selected
- Check if beds are available for that year

### CSS/JS not loading
- Clear browser cache (Ctrl+Shift+Delete)
- Verify file paths in JSP pages
- Check if images/ and css/ directories exist

### Java compilation errors
- Ensure servlet-api.jar is in Tomcat lib folder
- Check Java version compatibility (Java 8 or higher)

## Support

For issues or questions, contact the development team or check Tomcat logs at:
`apache-tomcat-9.0.117/logs/`

---

**Version:** 1.0  
**Last Updated:** April 2026  
**Built with:** Java, JSP, Servlets, XML, HTML5, CSS3, JavaScript
