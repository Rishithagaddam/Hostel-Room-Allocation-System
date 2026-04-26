package com.hostel;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XMLManager {
    private static final String DATA_DIR = "data/";
    private static final String STUDENTS_FILE = "students.xml";
    private static final String ROOMS_FILE = "rooms.xml";
    private static final String ALLOCATIONS_FILE = "allocations.xml";

    private DocumentBuilder documentBuilder;
    private String baseDir;

    public XMLManager(String appPath) {
        this.baseDir = appPath;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this.documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the full path to a data file
     */
    private String getFilePath(String filename) {
        return baseDir + File.separator + DATA_DIR + filename;
    }

    /**
     * Read student by username
     */
    public Map<String, String> getStudentByUsername(String username) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String usernameNode = student.getElementsByTagName("username").item(0).getTextContent();
                if (usernameNode.equals(username)) {
                    return parseStudentElement(student);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get student by ID
     */
    public Map<String, String> getStudentById(String studentId) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String idNode = student.getElementsByTagName("student_id").item(0).getTextContent();
                if (idNode.equals(studentId)) {
                    return parseStudentElement(student);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if roll number already exists
     */
    public boolean rollNumberExists(String rollNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                NodeList rollNodes = student.getElementsByTagName("roll_number");
                if (rollNodes.getLength() > 0) {
                    String roll = rollNodes.item(0).getTextContent();
                    if (roll.equals(rollNumber)) {
                        return true;
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a new student with roll number
     */
    public boolean addStudent(String name, String year, String email, String rollNumber, String username, String passwordHash) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            Element root = doc.getDocumentElement();

            // Generate student ID
            String studentId = "S" + String.format("%03d", getNextStudentNumber());

            // Create new student element
            Element student = doc.createElement("student");

            addElement(doc, student, "student_id", studentId);
            addElement(doc, student, "name", name);
            addElement(doc, student, "email", email);
            addElement(doc, student, "roll_number", rollNumber);
            addElement(doc, student, "username", username);
            addElement(doc, student, "password_hash", passwordHash);
            addElement(doc, student, "role", "student");
            addElement(doc, student, "year", year);
            addElement(doc, student, "payment_status", "completed");
            addElement(doc, student, "allocation_status", "UNALLOCATED");
            addElement(doc, student, "registration_date", getCurrentDate());

            root.appendChild(student);
            saveDocument(doc, getFilePath(STUDENTS_FILE));
            return true;
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all available beds for a specific year
     */
    public List<Map<String, String>> getAvailableBedsForYear(String year) {
        List<Map<String, String>> beds = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(ROOMS_FILE)));
            NodeList blockList = doc.getElementsByTagName("block");

            for (int i = 0; i < blockList.getLength(); i++) {
                Element block = (Element) blockList.item(i);
                String blockId = block.getElementsByTagName("block_id").item(0).getTextContent();

                NodeList floorList = block.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floor = (Element) floorList.item(j);
                    String floorId = floor.getElementsByTagName("floor_id").item(0).getTextContent();

                    NodeList roomList = floor.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element room = (Element) roomList.item(k);
                        String roomId = room.getElementsByTagName("room_id").item(0).getTextContent();

                        NodeList bedList = room.getElementsByTagName("bed");
                        for (int l = 0; l < bedList.getLength(); l++) {
                            Element bed = (Element) bedList.item(l);
                            String bedId = bed.getElementsByTagName("bed_id").item(0).getTextContent();
                            String status = bed.getElementsByTagName("status").item(0).getTextContent();

                            if ("AVAILABLE".equals(status)) {
                                Map<String, String> bedInfo = new HashMap<>();
                                bedInfo.put("bed_id", bedId);
                                bedInfo.put("room_id", roomId);
                                bedInfo.put("block_id", blockId);
                                bedInfo.put("floor_id", floorId);
                                beds.add(bedInfo);
                            }
                        }
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return beds;
    }

    /**
     * Get room information by ID
     */
    public Map<String, String> getRoomById(String roomId) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(ROOMS_FILE)));
            NodeList rooms = doc.getElementsByTagName("room");

            for (int i = 0; i < rooms.getLength(); i++) {
                Element room = (Element) rooms.item(i);
                String rid = room.getElementsByTagName("room_id").item(0).getTextContent();
                if (rid.equals(roomId)) {
                    return parseRoomElement(room);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Allocate a bed to a student
     */
    public boolean allocateBed(String studentId, String roomId, String bedId) {
        try {
            // Update allocations.xml
            Document allocDoc = documentBuilder.parse(new File(getFilePath(ALLOCATIONS_FILE)));
            Element allocRoot = allocDoc.getDocumentElement();

            String allocationId = "ALLOC" + String.format("%05d", getNextAllocationNumber());
            Element allocation = allocDoc.createElement("allocation");

            addElement(allocDoc, allocation, "allocation_id", allocationId);
            addElement(allocDoc, allocation, "student_id", studentId);
            addElement(allocDoc, allocation, "room_id", roomId);
            addElement(allocDoc, allocation, "bed_id", bedId);
            addElement(allocDoc, allocation, "allocation_date", getCurrentDate());
            addElement(allocDoc, allocation, "status", "ACTIVE");

            allocRoot.appendChild(allocation);
            saveDocument(allocDoc, getFilePath(ALLOCATIONS_FILE));

            // Update bed status in rooms.xml
            Document roomDoc = documentBuilder.parse(new File(getFilePath(ROOMS_FILE)));
            NodeList beds = roomDoc.getElementsByTagName("bed");

            for (int i = 0; i < beds.getLength(); i++) {
                Element bed = (Element) beds.item(i);
                String bid = bed.getElementsByTagName("bed_id").item(0).getTextContent();
                if (bid.equals(bedId)) {
                    bed.getElementsByTagName("status").item(0).setTextContent("OCCUPIED");
                    saveDocument(roomDoc, getFilePath(ROOMS_FILE));
                    return true;
                }
            }
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get allocation by student ID
     */
    public Map<String, String> getAllocationByStudentId(String studentId) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(ALLOCATIONS_FILE)));
            NodeList allocations = doc.getElementsByTagName("allocation");

            for (int i = 0; i < allocations.getLength(); i++) {
                Element allocation = (Element) allocations.item(i);
                String sid = allocation.getElementsByTagName("student_id").item(0).getTextContent();
                if (sid.equals(studentId)) {
                    Map<String, String> alloc = new HashMap<>();
                    alloc.put("allocation_id", allocation.getElementsByTagName("allocation_id").item(0).getTextContent());
                    alloc.put("student_id", sid);
                    alloc.put("room_id", allocation.getElementsByTagName("room_id").item(0).getTextContent());
                    alloc.put("bed_id", allocation.getElementsByTagName("bed_id").item(0).getTextContent());
                    alloc.put("allocation_date", allocation.getElementsByTagName("allocation_date").item(0).getTextContent());
                    alloc.put("status", allocation.getElementsByTagName("status").item(0).getTextContent());
                    return alloc;
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all students by year
     */
    public List<Map<String, String>> getStudentsByYear(String year) {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String studentYear = student.getElementsByTagName("year").item(0).getTextContent();
                if (studentYear.equals(year) && !"warden".equals(student.getElementsByTagName("role").item(0).getTextContent())) {
                    students.add(parseStudentElement(student));
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Get all registered (unallocated) students
     */
    public List<Map<String, String>> getRegisteredStudents() {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String role = student.getElementsByTagName("role").item(0).getTextContent();
                if (!"warden".equals(role)) {
                    NodeList statusNodes = student.getElementsByTagName("allocation_status");
                    String status = "UNALLOCATED";
                    if (statusNodes.getLength() > 0) {
                        status = statusNodes.item(0).getTextContent();
                    }
                    if ("UNALLOCATED".equals(status)) {
                        students.add(parseStudentElement(student));
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Get all unallocated students
     */
    public List<Map<String, String>> getUnallocatedStudents() {
        return getRegisteredStudents();
    }

    /**
     * Get all allocated students
     */
    public List<Map<String, String>> getAllocatedStudents() {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String role = student.getElementsByTagName("role").item(0).getTextContent();
                if (!"warden".equals(role)) {
                    NodeList statusNodes = student.getElementsByTagName("allocation_status");
                    if (statusNodes.getLength() > 0) {
                        String status = statusNodes.item(0).getTextContent();
                        if ("ALLOCATED".equals(status)) {
                            students.add(parseStudentElement(student));
                        }
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Update student allocation status
     */
    public boolean updateAllocationStatus(String studentId, String status) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String sid = student.getElementsByTagName("student_id").item(0).getTextContent();
                if (sid.equals(studentId)) {
                    NodeList statusNodes = student.getElementsByTagName("allocation_status");
                    if (statusNodes.getLength() > 0) {
                        statusNodes.item(0).setTextContent(status);
                    } else {
                        addElement(doc, student, "allocation_status", status);
                    }
                    saveDocument(doc, getFilePath(STUDENTS_FILE));
                    return true;
                }
            }
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get rooms for a specific block and floor
     */
    public List<Map<String, Object>> getRoomsByBlockAndFloor(String block, String floor) {
        List<Map<String, Object>> rooms = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(ROOMS_FILE)));
            NodeList blockList = doc.getElementsByTagName("block");

            for (int i = 0; i < blockList.getLength(); i++) {
                Element blockElem = (Element) blockList.item(i);
                String blockId = blockElem.getElementsByTagName("block_id").item(0).getTextContent();

                if (!blockId.equals(block)) continue;

                NodeList floorList = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floorElem = (Element) floorList.item(j);
                    String floorNum = floorElem.getElementsByTagName("floor_number").item(0).getTextContent();

                    if (!floorNum.equals(floor)) continue;

                    NodeList roomList = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element room = (Element) roomList.item(k);
                        Map<String, Object> roomData = new HashMap<>();
                        roomData.put("room_id", room.getElementsByTagName("room_id").item(0).getTextContent());
                        roomData.put("room_number", room.getElementsByTagName("room_number").item(0).getTextContent());

                        // Get beds for this room
                        NodeList beds = room.getElementsByTagName("bed");
                        List<Map<String, String>> bedList = new ArrayList<>();
                        for (int l = 0; l < beds.getLength(); l++) {
                            Element bed = (Element) beds.item(l);
                            Map<String, String> bedData = new HashMap<>();
                            bedData.put("bed_id", bed.getElementsByTagName("bed_id").item(0).getTextContent());
                            bedData.put("bed_number", bed.getElementsByTagName("bed_number").item(0).getTextContent());
                            bedData.put("status", bed.getElementsByTagName("status").item(0).getTextContent());

                            NodeList rollNoNodes = bed.getElementsByTagName("roll_no");
                            if (rollNoNodes.getLength() > 0) {
                                bedData.put("roll_no", rollNoNodes.item(0).getTextContent());
                            } else {
                                bedData.put("roll_no", "");
                            }

                            bedList.add(bedData);
                        }
                        roomData.put("beds", bedList);
                        rooms.add(roomData);
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Allocate bed to student - updates both beds and students
     */
    public boolean allocateBedToStudent(String studentId, String bedId, String rollNo, String block, String floor, String room) {
        try {
            // Update bed status and add roll number
            Document roomDoc = documentBuilder.parse(new File(getFilePath(ROOMS_FILE)));
            NodeList beds = roomDoc.getElementsByTagName("bed");
            boolean bedFound = false;

            for (int i = 0; i < beds.getLength(); i++) {
                Element bed = (Element) beds.item(i);
                String bid = bed.getElementsByTagName("bed_id").item(0).getTextContent();
                if (bid.equals(bedId)) {
                    bed.getElementsByTagName("status").item(0).setTextContent("OCCUPIED");

                    // Update or add roll_no
                    NodeList rollNoNodes = bed.getElementsByTagName("roll_no");
                    if (rollNoNodes.getLength() > 0) {
                        rollNoNodes.item(0).setTextContent(rollNo);
                    } else {
                        addElement(roomDoc, bed, "roll_no", rollNo);
                    }

                    saveDocument(roomDoc, getFilePath(ROOMS_FILE));
                    bedFound = true;
                    break;
                }
            }

            if (!bedFound) {
                return false;
            }

            // Update allocation record
            Document allocDoc = documentBuilder.parse(new File(getFilePath(ALLOCATIONS_FILE)));
            Element allocRoot = allocDoc.getDocumentElement();

            String allocationId = "ALLOC" + String.format("%05d", getNextAllocationNumber());
            Element allocation = allocDoc.createElement("allocation");

            addElement(allocDoc, allocation, "allocation_id", allocationId);
            addElement(allocDoc, allocation, "student_id", studentId);
            addElement(allocDoc, allocation, "roll_no", rollNo);
            addElement(allocDoc, allocation, "room_id", room);
            addElement(allocDoc, allocation, "bed_id", bedId);
            addElement(allocDoc, allocation, "block", block);
            addElement(allocDoc, allocation, "floor", floor);
            addElement(allocDoc, allocation, "allocation_date", getCurrentDate());
            addElement(allocDoc, allocation, "status", "ACTIVE");

            allocRoot.appendChild(allocation);
            saveDocument(allocDoc, getFilePath(ALLOCATIONS_FILE));

            // Update student allocation status
            updateAllocationStatus(studentId, "ALLOCATED");

            return true;
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total_beds", 0);
        stats.put("occupied_beds", 0);
        stats.put("available_beds", 0);
        stats.put("total_students", 0);

        try {
            // Count beds from rooms.xml
            File roomsFile = new File(getFilePath(ROOMS_FILE));
            if (roomsFile.exists()) {
                Document doc = documentBuilder.parse(roomsFile);
                NodeList beds = doc.getElementsByTagName("bed");

                int totalBeds = beds.getLength();
                int occupiedBeds = 0;
                int availableBeds = 0;

                for (int i = 0; i < totalBeds; i++) {
                    Element bed = (Element) beds.item(i);
                    NodeList statusNodes = bed.getElementsByTagName("status");
                    if (statusNodes.getLength() > 0) {
                        String status = statusNodes.item(0).getTextContent();
                        if ("OCCUPIED".equals(status)) {
                            occupiedBeds++;
                        } else {
                            availableBeds++;
                        }
                    } else {
                        availableBeds++;
                    }
                }

                stats.put("total_beds", totalBeds);
                stats.put("occupied_beds", occupiedBeds);
                stats.put("available_beds", availableBeds);

                System.out.println("Dashboard Stats: Total=" + totalBeds + ", Occupied=" + occupiedBeds + ", Available=" + availableBeds);
            }

            // Count students from students.xml
            File studentsFile = new File(getFilePath(STUDENTS_FILE));
            if (studentsFile.exists()) {
                Document studentDoc = documentBuilder.parse(studentsFile);
                NodeList students = studentDoc.getElementsByTagName("student");
                int studentCount = 0;

                for (int i = 0; i < students.getLength(); i++) {
                    Element student = (Element) students.item(i);
                    NodeList roleNodes = student.getElementsByTagName("role");
                    if (roleNodes.getLength() > 0) {
                        String role = roleNodes.item(0).getTextContent();
                        if (!"warden".equals(role)) {
                            studentCount++;
                        }
                    }
                }

                stats.put("total_students", studentCount);
                System.out.println("Total Students: " + studentCount);
            }
        } catch (IOException | SAXException e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // Helper methods

    private Map<String, String> parseStudentElement(Element student) {
        Map<String, String> data = new HashMap<>();
        data.put("student_id", student.getElementsByTagName("student_id").item(0).getTextContent());
        data.put("name", student.getElementsByTagName("name").item(0).getTextContent());
        data.put("email", student.getElementsByTagName("email").item(0).getTextContent());
        data.put("username", student.getElementsByTagName("username").item(0).getTextContent());
        data.put("password_hash", student.getElementsByTagName("password_hash").item(0).getTextContent());
        data.put("role", student.getElementsByTagName("role").item(0).getTextContent());
        data.put("year", student.getElementsByTagName("year").item(0).getTextContent());
        data.put("payment_status", student.getElementsByTagName("payment_status").item(0).getTextContent());
        data.put("registration_date", student.getElementsByTagName("registration_date").item(0).getTextContent());

        // Add roll_number if it exists
        NodeList rollNodes = student.getElementsByTagName("roll_number");
        if (rollNodes.getLength() > 0) {
            data.put("roll_number", rollNodes.item(0).getTextContent());
        }

        // Add allocation_status if it exists
        NodeList allocNodes = student.getElementsByTagName("allocation_status");
        if (allocNodes.getLength() > 0) {
            data.put("allocation_status", allocNodes.item(0).getTextContent());
        }

        return data;
    }

    private Map<String, String> parseRoomElement(Element room) {
        Map<String, String> data = new HashMap<>();
        data.put("room_id", room.getElementsByTagName("room_id").item(0).getTextContent());
        data.put("room_number", room.getElementsByTagName("room_number").item(0).getTextContent());
        data.put("room_image", room.getElementsByTagName("room_image").item(0).getTextContent());
        return data;
    }

    private void addElement(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private int getNextStudentNumber() {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(STUDENTS_FILE)));
            NodeList students = doc.getElementsByTagName("student");
            return students.getLength() + 1;
        } catch (IOException | SAXException e) {
            return 1;
        }
    }

    private int getNextAllocationNumber() {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath(ALLOCATIONS_FILE)));
            NodeList allocations = doc.getElementsByTagName("allocation");
            return allocations.getLength() + 1;
        } catch (IOException | SAXException e) {
            return 1;
        }
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    private void saveDocument(Document doc, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }
}
