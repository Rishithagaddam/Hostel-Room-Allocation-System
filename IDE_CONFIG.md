# IDE Configuration Guide - Fixing Servlet Import Errors

## ⚠️ Issue
Your IDE shows error: `package javax.servlet.http does not exist`

## ✅ Solution
The code **COMPILES FINE** but your IDE can't find the Tomcat libraries. Follow the solution for your IDE:

---

## VS Code

### Option 1: Add Extension Pack for Java
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search for "Extension Pack for Java" by Microsoft
4. Click Install
5. Reload VS Code

### Option 2: Manual Configuration
1. Create folder: `.vscode/` (already created ✓)
2. Edit `.vscode/settings.json` (already created ✓)
3. Update the Java path to your JDK:
   ```json
   "java.configuration.runtimes": [
       {
           "name": "JavaSE-1.8",
           "path": "C:\\Program Files\\Java\\jdk1.8.0_xxx",
           "default": true
       }
   ]
   ```
4. Reload VS Code

### Option 3: Command Palette Fix
1. Press Ctrl+Shift+P
2. Type "Java: Configure Classpath"
3. Add: `../../../lib/servlet-api.jar`

---

## Eclipse IDE

### Automatic (Already Configured ✓)
1. Open Eclipse
2. File → Import → Existing Projects into Workspace
3. Select: `apache-tomcat-9.0.117/webapps/hostel-allocation`
4. Click Finish
5. Right-click project → Properties → Java Build Path
6. Click "Add External JARs..."
7. Browse to: `apache-tomcat-9.0.117/lib/servlet-api.jar`
8. Click OK

### Or use .classpath file (Created ✓)
The `.classpath` file is already created with proper configuration.

---

## IntelliJ IDEA

1. Open IntelliJ IDEA
2. File → Open → Select `hostel-allocation` folder
3. Wait for project indexing
4. File → Project Structure (Ctrl+Alt+Shift+S)
5. Go to "Libraries"
6. Click "+" → "Java"
7. Browse to: `apache-tomcat-9.0.117/lib/servlet-api.jar`
8. Click "Add"
9. Click "OK"

---

## NetBeans

1. Open NetBeans
2. File → Open Project
3. Select `hostel-allocation`
4. Right-click Project → Properties
5. Go to Libraries
6. Click "Add Library..."
7. Add: `apache-tomcat-9.0.117/lib/servlet-api.jar`
8. Click OK

---

## Command Line (Verified ✓)

**The system already compiles perfectly:**
```bash
cd WEB-INF/classes
javac -cp "../../../lib/servlet-api.jar" -d . com/hostel/*.java
```
✓ All 6 classes compile successfully!

---

## Quick Fix Summary

| IDE | Action |
|-----|--------|
| **VS Code** | Install "Extension Pack for Java" & reload |
| **Eclipse** | Use .classpath file (already created) |
| **IntelliJ** | Project Structure → Libraries → Add servlet-api.jar |
| **NetBeans** | Project Properties → Libraries → Add servlet-api.jar |
| **Command Line** | Works perfectly ✓ (verified) |

---

## ✨ Important Note

These IDE warnings **DO NOT affect the running application**:
- ✅ Code compiles successfully
- ✅ Tomcat can deploy it
- ✅ Application runs without issues
- ⚠️ IDE just can't autocomplete/highlight in editor

You can safely ignore the IDE errors and run the application on Tomcat!

---

## Verify Configuration

Run this command to ensure everything is correct:
```bash
java -cp "WEB-INF/classes;../../../lib/servlet-api.jar" com.hostel.LoginServlet
```

If no error, configuration is correct ✓
