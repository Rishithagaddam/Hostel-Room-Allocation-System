# Quick Start Testing Guide - Room Allocation System

## 🎯 Quick Test (5 Minutes)

### Step 1: Start Tomcat
```bash
cd /c/Users/LENOVO/Downloads/apache-tomcat-9.0.117-windows-x86/apache-tomcat-9.0.117/bin
./startup.sh
# Wait 5-10 seconds for startup
```

### Step 2: Login to Warden Dashboard
- URL: http://localhost:8080/hostel-allocation/
- Click "Warden Login"
- Username: `warden`
- Password: `warden123`
- Click Login

**Expected**: Dashboard shows with statistics, student registration form, and "Go to Room Allocation" button

---

## ✅ Test Case 1: Register New Student (2 mins)

### Form Details:
```
Student Name:     John Doe
Roll Number:      21BCE002
Year:             1
Email:            john@college.edu
```

### Steps:
1. Scroll to "Add New Student" section
2. Fill all fields
3. Click "Register Student"

### Expected Result:
- ✅ Success message appears
- ✅ Credentials displayed (username, password)
- ✅ Student added to "Unallocated Students" table below
- ✅ Roll number displayed in table

**If Error**: Check browser console (F12) for JSON parse errors

---

## ✅ Test Case 2: Allocate Room (3 mins)

### Steps:
1. Click "Go to Room Allocation" button
2. In left panel "Select Student":
   - Choose **John Doe** from dropdown
   - Verify student details display (Name, Roll No, Year, Email)

3. In right panel "Select Room":
   - Click **Block B**
   - Select **Floor 2** from dropdown
   - Select **Room 3** from dropdown
   - Click any **Bed** (e.g., Bed 1) - appears green

4. Click **"Allocate Bed"** button

5. **Confirmation Modal** appears:
   - Shows: Student Name, Block, Floor, Room, Bed
   - Click **"Confirm"** button

### Expected Result:
- ✅ Success message: "Room allocated successfully! Email sent to student."
- ✅ Student list refreshes
- ✅ John Doe no longer in dropdown
- ✅ Console shows email details (check F12 → Console)

### Example Console Output:
```
=== ALLOCATION EMAIL ===
To: john@college.edu
Subject: Hostel Room Allocation Details
Body:
Dear John Doe,

Your hostel room has been allocated successfully.

Roll No: 21BCE002
Block: B
Floor: 2
Room: 3
Bed: 1

Login Link: http://localhost:8080/hostel-allocation

Regards,
Hostel Warden
========================
```

---

## ✅ Test Case 3: Duplicate Roll Number (1 min)

### Steps:
1. Try to register another student with Roll No: **21BCE002** (same as John)
2. Click "Register Student"

### Expected Result:
- ✅ Error message appears: "Roll number already exists"
- ❌ Student NOT added
- Change roll number to 21BCE003 and retry
- ✅ Should succeed

---

## 🔍 Verify Data in XML Files

### Check students.xml:
```bash
cat /c/Users/LENOVO/Downloads/apache-tomcat-9.0.117-windows-x86/apache-tomcat-9.0.117/webapps/hostel-allocation/data/students.xml
```

**Look for:**
- ✅ `<allocation_status>ALLOCATED</allocation_status>` for allocated students
- ✅ `<allocation_status>UNALLOCATED</allocation_status>` for new students
- ✅ `<roll_number>` field populated

### Check allocations.xml:
```bash
cat /c/Users/LENOVO/Downloads/apache-tomcat-9.0.117-windows-x86/apache-tomcat-9.0.117/webapps/hostel-allocation/data/allocations.xml
```

**Look for:**
- ✅ New `<allocation>` records with student_id, room_id, bed_id

### Check rooms.xml:
```bash
head -50 /c/Users/LENOVO/Downloads/apache-tomcat-9.0.117-windows-x86/apache-tomcat-9.0.117/webapps/hostel-allocation/data/rooms.xml
```

**Look for:**
- ✅ 4 Blocks (A, B, C, D)
- ✅ 4 Floors per block (1, 2, 3, 4)
- ✅ 5 Rooms per floor
- ✅ 3 Beds per room
- ✅ Bed status: AVAILABLE or OCCUPIED

---

## 🐛 Troubleshooting

### Issue: "Failed to load students" error
**Solution**: 
1. Check browser console (F12 → Console)
2. Look for JSON parse errors
3. Check Tomcat logs: `tail -50 logs/catalina.out`
4. Ensure AllocationServlet is returning valid JSON

### Issue: Allocation returns error
**Solution**:
1. Check all required fields are filled
2. Verify student is actually in dropdown
3. Check Tomcat logs for exceptions
4. Ensure XML files are in `/webapps/hostel-allocation/data/`

### Issue: Roll number validation not working
**Solution**:
1. Clear browser cache (Ctrl+Shift+Delete)
2. Reload page
3. Try with completely new roll number
4. Check XMLManager.rollNumberExists() method

### Issue: Email not sending
**Solution**:
1. Check browser console for "=== ALLOCATION EMAIL ===" message
2. This confirms email logging is working
3. Real SMTP requires mail.jar in WEB-INF/lib/

---

## 📊 Test Summary Checklist

- [ ] Warden login works
- [ ] Can register student with unique roll number
- [ ] Roll number duplicate validation works
- [ ] Duplicate roll number shows error message
- [ ] Allocation page loads and fetches students
- [ ] Student details display when selected
- [ ] Block/Floor/Room/Bed selection works
- [ ] Confirmation modal appears before allocation
- [ ] Allocation succeeds
- [ ] Email details logged to console
- [ ] Student removed from dropdown after allocation
- [ ] XML files update correctly
- [ ] Bed status changes to OCCUPIED
- [ ] Student status changes to ALLOCATED
- [ ] New allocation record created in allocations.xml

---

## 🚀 Production Checklist

- [ ] All Java classes compiled
- [ ] rooms.xml has 4 blocks, 4 floors each
- [ ] students.xml has sample students with roll numbers
- [ ] Email logging works (upgrade to real SMTP if needed)
- [ ] All APIs return JSON (never HTML)
- [ ] Error handling catches all exceptions
- [ ] Responsive design works on mobile (optional)

**Status**: ✅ **Ready for Production Testing**
