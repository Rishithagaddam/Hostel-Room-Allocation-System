package com.hostel;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XMLManager {
    private static final String DATA_DIR = "data/";
    private static final String HOSTEL_FILE = "hostel.xml";

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
     * Get the full path to the data file
     */
    private String getFilePath() {
        return baseDir + File.separator + DATA_DIR + HOSTEL_FILE;
    }

    /**
     * Initialize rooms if missing
     */
    public void initializeRoomsIfMissing() {
        File file = new File(getFilePath());
        if (!file.exists()) {
            createInitialHostelStructure();
        } else {
            // Check if rooms exist
            try {
                Document doc = documentBuilder.parse(file);
                NodeList rooms = doc.getElementsByTagName("rooms");
                if (rooms.getLength() == 0) {
                    addRoomsToExistingFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
        }
        }
    }

    /**
     * Create initial hostel XML structure
     */
    private void createInitialHostelStructure() {
        try {
            Document doc = documentBuilder.newDocument();
            Element hostel = doc.createElement("hostel");
            doc.appendChild(hostel);

            // Create students section
            Element students = doc.createElement("students");
            hostel.appendChild(students);

            // Create rooms section
            Element rooms = doc.createElement("rooms");
            hostel.appendChild(rooms);

            // Add blocks A, B, C, D
            String[] blocks = {"A", "B", "C", "D"};
            for (String blockName : blocks) {
                Element block = doc.createElement("block");
                block.setAttribute("name", blockName);
                rooms.appendChild(block);

                // Add 4 floors per block
                for (int floor = 1; floor <= 4; floor++) {
                    Element floorElem = doc.createElement("floor");
                    floorElem.setAttribute("number", String.valueOf(floor));
                    block.appendChild(floorElem);

                    // Add 5 rooms per floor
                    for (int room = 1; room <= 5; room++) {
                        Element roomElem = doc.createElement("room");
                        roomElem.setAttribute("number", String.valueOf(room));
                        floorElem.appendChild(roomElem);

                        // Add 3 beds per room
                        for (int bed = 1; bed <= 3; bed++) {
                            Element bedElem = doc.createElement("bed");
                            bedElem.setAttribute("number", String.valueOf(bed));
                            bedElem.setAttribute("status", "available");
                            bedElem.setAttribute("rollNo", "");
                            roomElem.appendChild(bedElem);
                        }
                    }
                }
        }

            saveDocument(doc, getFilePath());
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add rooms to existing file
     */
    private void addRoomsToExistingFile() {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();

            // Create rooms section
            Element rooms = doc.createElement("rooms");
            hostel.appendChild(rooms);

            // Add blocks A, B, C, D
            String[] blocks = {"A", "B", "C", "D"};
            for (String blockName : blocks) {
                Element block = doc.createElement("block");
                block.setAttribute("name", blockName);
                rooms.appendChild(block);

                // Add 4 floors per block
                for (int floor = 1; floor <= 4; floor++) {
                    Element floorElem = doc.createElement("floor");
                    floorElem.setAttribute("number", String.valueOf(floor));
                    block.appendChild(floorElem);

                    // Add 5 rooms per floor
                    for (int room = 1; room <= 5; room++) {
                        Element roomElem = doc.createElement("room");
                        roomElem.setAttribute("number", String.valueOf(room));
                        floorElem.appendChild(roomElem);

                        // Add 3 beds per room
                        for (int bed = 1; bed <= 3; bed++) {
                            Element bedElem = doc.createElement("bed");
                            bedElem.setAttribute("number", String.valueOf(bed));
                            bedElem.setAttribute("status", "available");
                            bedElem.setAttribute("rollNo", "");
                            roomElem.appendChild(bedElem);
                        }
                    }
                }
        }

            saveDocument(doc, getFilePath());
        catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read student by username
     */
    public Map<String, String> getStudentByUsername(String username) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                NodeList usernameNodes = student.getElementsByTagName("username");
                if (usernameNodes.getLength() > 0) {
                    String usernameNode = usernameNodes.item(0).getTextContent();
                    if (usernameNode.equals(username)) {
                        return parseStudentElement(student);
                    }
                }
        }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get student by roll number
     */
    public Map<String, String> getStudentByRollNo(String rollNo) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return null;
        }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String roll = getElementValue(student, "roll_number");
                if (roll != null && roll.equals(rollNo)) {
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
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return false;
        }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String roll = getElementValue(student, "roll_number");
                if (roll != null && roll.equals(rollNumber)) {
                    return true;
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
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");

            Document doc;
            Element students;

            if (studentsFile.exists()) {
                doc = documentBuilder.parse(studentsFile);
                students = doc.getDocumentElement();
            } else {
                doc = documentBuilder.newDocument();
                students = doc.createElement("students");
                doc.appendChild(students);
        }

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

            students.appendChild(student);
            saveDocument(doc, studentsFile.getAbsolutePath());
            return true;
        catch (IOException | SAXException | TransformerException e) {
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
            Document doc = documentBuilder.parse(new File(baseDir + File.separator + DATA_DIR + "rooms.xml"));
            NodeList blockList = doc.getElementsByTagName("block");

            for (int i = 0; i < blockList.getLength(); i++) {
                Element block = (Element) blockList.item(i);
                String blockId = getElementValue(block, "block_id");

                NodeList floorList = block.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floor = (Element) floorList.item(j);
                    String floorId = getElementValue(floor, "floor_number");

                    NodeList roomList = floor.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element room = (Element) roomList.item(k);
                        String roomId = getElementValue(room, "room_number");

                        NodeList bedList = room.getElementsByTagName("bed");
                        for (int l = 0; l < bedList.getLength(); l++) {
                            Element bed = (Element) bedList.item(l);
                            String bedId = getElementValue(bed, "bed_number");
                            String status = getElementValue(bed, "status");

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
     * Allocate a bed to a student (legacy method)
     */
    public boolean allocateBed(String studentId, String roomId, String bedId) {
        // This method is deprecated, use allocateRoom instead
        return false;
    }

    /**
     * Get allocation by student ID
     */
    public Map<String, String> getAllocationByStudentId(String studentId) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String sid = getElementValue(student, "student_id");
                if (sid != null && sid.equals(studentId)) {
                    String block = getElementValue(student, "block");
                    String floor = getElementValue(student, "floor");
                    String room = getElementValue(student, "room");
                    String bed = getElementValue(student, "bed");
                    String status = getElementValue(student, "status");

                    if (block != null && "allocated".equals(status)) {
                        Map<String, String> alloc = new HashMap<>();
                        alloc.put("student_id", sid);
                        alloc.put("block", block);
                        alloc.put("floor", floor);
                        alloc.put("room", room);
                        alloc.put("bed", bed);
                        alloc.put("status", status);
                        return alloc;
                    }
                }
        }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get room information by ID
     */
    public Map<String, String> getRoomById(String roomId) {
        try {
            Document doc = documentBuilder.parse(new File(baseDir + File.separator + DATA_DIR + "rooms.xml"));
            NodeList rooms = doc.getElementsByTagName("room");

            for (int i = 0; i < rooms.getLength(); i++) {
                Element room = (Element) rooms.item(i);
                String rid = getElementValue(room, "room_number");
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
     * Get all students by year
     */
    public List<Map<String, String>> getStudentsByYear(String year) {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String studentYear = getElementValue(student, "year");
                String role = getElementValue(student, "role");
                if (studentYear.equals(year) && !"warden".equals(role)) {
                    students.add(parseStudentElement(student));
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
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return students;
        }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String allocationStatus = getElementValue(student, "allocation_status");

                if ("ALLOCATED".equals(allocationStatus)) {
                    Map<String, String> data = new HashMap<>();
                    data.put("studentId", getElementValue(student, "student_id"));
                    data.put("name", getElementValue(student, "name"));
                    data.put("rollNumber", getElementValue(student, "roll_number"));
                    data.put("year", getElementValue(student, "year"));
                    data.put("email", getElementValue(student, "email"));
                    data.put("block", getElementValue(student, "allocated_block"));
                    data.put("floor", getElementValue(student, "allocated_floor"));
                    data.put("roomNo", getElementValue(student, "allocated_room"));
                    data.put("bedNo", getElementValue(student, "allocated_bed"));
                    students.add(data);
                }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Update student allocation status
     */
    public boolean updateAllocationStatus(String studentId, String status) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String sid = getElementValue(student, "student_id");
                if (sid.equals(studentId)) {
                    NodeList statusNodes = student.getElementsByTagName("status");
                    if (statusNodes.getLength() > 0) {
                        statusNodes.item(0).setTextContent(status);
                    } else {
                        addElement(doc, student, "status", status);
                    }
                    saveDocument(doc, getFilePath());
                    return true;
                }
        }
        catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * Allocate bed to student - updates hostel.xml bed status
     */
    public boolean allocateRoom(String rollNumber, String block, String floor, String roomNo, String bedNo) {
        try {
            File roomsFile = new File(baseDir + File.separator + DATA_DIR + "rooms.xml");
            Document doc = documentBuilder.parse(roomsFile);

            // Find and update the bed
            NodeList blockList = doc.getElementsByTagName("block");
            for (int i = 0; i < blockList.getLength(); i++) {
                Element blockElem = (Element) blockList.item(i);
                String blockName = getElementValue(blockElem, "block_id");
                if (!blockName.equals(block)) continue;

                NodeList floorList = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floorElem = (Element) floorList.item(j);
                    String floorNum = getElementValue(floorElem, "floor_number");
                    if (!floorNum.equals(floor)) continue;

                    NodeList roomList = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element room = (Element) roomList.item(k);
                        String rNo = getElementValue(room, "room_number");
                        if (!rNo.equals(roomNo)) continue;

                        NodeList beds = room.getElementsByTagName("bed");
                        for (int l = 0; l < beds.getLength(); l++) {
                            Element bed = (Element) beds.item(l);
                            String bNo = getElementValue(bed, "bed_number");
                            String rollNo = getElementValue(bed, "rollNo");
                            if (bNo.equals(bedNo) && (rollNo == null || rollNo.trim().isEmpty())) {
                                // Update status element
                                NodeList statusNodes = bed.getElementsByTagName("status");
                                if (statusNodes.getLength() > 0) {
                                    statusNodes.item(0).setTextContent("OCCUPIED");
                                }
                                // Add rollNo element
                                addElement(doc, bed, "rollNo", rollNumber);
                                saveDocument(doc, roomsFile.getAbsolutePath());
                                return true;
                            }
                        }
                    }
        }
        catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }
    }
    // Dashboard stats
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalBeds", 240);
        stats.put("occupiedBeds", 0);
        stats.put("availableBeds", 240);
        stats.put("totalStudents", 0);
        stats.put("unallocatedStudents", 0);
        stats.put("allocatedStudents", 0);

        try {
            // Count beds from rooms.xml
            Document hostelDoc = documentBuilder.parse(new File(baseDir + File.separator + DATA_DIR + "rooms.xml"));
            NodeList beds = hostelDoc.getElementsByTagName("bed");
            int totalBeds = beds.getLength();
            int occupiedBeds = 0;
            int availableBeds = 0;

            for (int i = 0; i < totalBeds; i++) {
                Element bed = (Element) beds.item(i);
                String rollNo = getElementValue(bed, "rollNo");
                String status = (rollNo != null && !rollNo.trim().isEmpty()) ? "occupied" : "available";
                if ("occupied".equalsIgnoreCase(status)) {
                    occupiedBeds++;
                } else {
                    availableBeds++;
                }
        }

            stats.put("totalBeds", totalBeds);
            stats.put("occupiedBeds", occupiedBeds);
            stats.put("availableBeds", availableBeds);

            // Count students from students.xml
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (studentsFile.exists()) {
                Document studentsDoc = documentBuilder.parse(studentsFile);
                NodeList students = studentsDoc.getElementsByTagName("student");

                int totalStudents = 0;
                int unallocatedStudents = 0;
                int allocatedStudents = 0;

                for (int i = 0; i < students.getLength(); i++) {
                    Element student = (Element) students.item(i);
                    String role = getElementValue(student, "role");
                    if (!"warden".equals(role)) {
                        totalStudents++;
                        String allocationStatus = getElementValue(student, "allocation_status");
                        if ("ALLOCATED".equals(allocationStatus)) {
                            allocatedStudents++;
                        } else {
                            unallocatedStudents++;
                        }
                    }
                }

                stats.put("totalStudents", totalStudents);
                stats.put("unallocatedStudents", unallocatedStudents);
                stats.put("allocatedStudents", allocatedStudents);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    // Helper methods

    private Map<String, String> parseStudentElement(Element student) {
        Map<String, String> data = new HashMap<>();
        data.put("student_id", getElementValue(student, "student_id"));
        data.put("name", getElementValue(student, "name"));
        data.put("email", getElementValue(student, "email"));
        data.put("rollNumber", getElementValue(student, "roll_number"));
        data.put("username", getElementValue(student, "username"));
        data.put("password_hash", getElementValue(student, "password_hash"));
        data.put("role", getElementValue(student, "role"));
        data.put("year", getElementValue(student, "year"));
        data.put("payment_status", getElementValue(student, "payment_status"));
        data.put("status", getElementValue(student, "status"));
        data.put("allocation_status", getElementValue(student, "allocation_status"));
        data.put("registration_date", getElementValue(student, "registration_date"));

        // Add allocation details if present
        String block = getElementValue(student, "block");
        if (block != null) data.put("block", block);
        String floor = getElementValue(student, "floor");
        if (floor != null) data.put("floor", floor);
        String room = getElementValue(student, "room");
        if (room != null) data.put("room", room);
        String bed = getElementValue(student, "bed");
        if (bed != null) data.put("bed", bed);

        return data;
    }

    private String getElementValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    private Map<String, String> parseRoomElement(Element room) {
        Map<String, String> data = new HashMap<>();
        String roomNumber = getElementValue(room, "room_number");
        data.put("room_id", roomNumber);
        data.put("room_number", roomNumber);
        data.put("room_image", ""); // No image in new structure
        return data;
    }

    private void addElement(Document doc, Element parent, String tagName, String value) {
        Element element = doc.createElement(tagName);
        element.setTextContent(value);
        parent.appendChild(element);
    }

    private int getNextStudentNumber() {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList students = doc.getElementsByTagName("student");
            return students.getLength() + 1;
        } catch (IOException | SAXException e) {
            return 1;
        }
    }

    private int getNextAllocationNumber() {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
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

    /**
     * Get registered students from students.xml
     */
    public List<Map<String, String>> getRegisteredStudents() {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return students;
        }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentNodes = doc.getElementsByTagName("student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Element student = (Element) studentNodes.item(i);
                String role = getElementValue(student, "role");
                String allocationStatus = getElementValue(student, "allocation_status");

                // Only include students who are registered (not allocated yet)
                if ("student".equals(role) && "UNALLOCATED".equalsIgnoreCase(allocationStatus)) {
                    Map<String, String> studentData = new HashMap<>();
                    studentData.put("studentId", getElementValue(student, "student_id"));
                    studentData.put("rollNumber", getElementValue(student, "roll_number"));
                    studentData.put("name", getElementValue(student, "name"));
                    studentData.put("year", getElementValue(student, "year"));
                    studentData.put("email", getElementValue(student, "email"));
                    students.add(studentData);
                }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Get rooms by block and floor
     */
    public List<Map<String, Object>> getRoomsByBlockAndFloor(String block, String floor) {
        List<Map<String, Object>> rooms = new ArrayList<>();
        try {
            File file = new File(baseDir + File.separator + DATA_DIR + "rooms.xml");
            if (!file.exists()) {
                return rooms;
        }

            Document doc = documentBuilder.parse(file);
            NodeList blockNodes = doc.getElementsByTagName("block");

            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element blockElem = (Element) blockNodes.item(i);
                if (block.equals(getElementValue(blockElem, "block_id"))) {
                    NodeList floorNodes = blockElem.getElementsByTagName("floor");

                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floorElem = (Element) floorNodes.item(j);
                        if (floor.equals(getElementValue(floorElem, "floor_number"))) {
                            NodeList roomNodes = floorElem.getElementsByTagName("room");

                            for (int k = 0; k < roomNodes.getLength(); k++) {
                                Element roomElem = (Element) roomNodes.item(k);
                                Map<String, Object> roomData = new HashMap<>();
                                String roomNo = getElementValue(roomElem, "room_number");
                                roomData.put("roomNo", roomNo);
                                roomData.put("roomId", block + "-" + floor + "-" + roomNo);
                                roomData.put("status", "available"); // Default status

                                List<Map<String, String>> beds = new ArrayList<>();
                                NodeList bedNodes = roomElem.getElementsByTagName("bed");

                                for (int l = 0; l < bedNodes.getLength(); l++) {
                                    Element bedElem = (Element) bedNodes.item(l);
                                    Map<String, String> bedData = new HashMap<>();
                                    String bedNo = getElementValue(bedElem, "bed_number");
                                    bedData.put("bedNo", bedNo);
                                    bedData.put("bedId", block + "-" + floor + "-" + roomNo + "-" + bedNo);
                                    String rollNo = getElementValue(bedElem, "rollNo");
                                    String status = rollNo != null && !rollNo.trim().isEmpty() ? "occupied" : "available";
                                    bedData.put("status", status);
                                    bedData.put("rollNo", rollNo);
                                    beds.add(bedData);
                                }

                                roomData.put("beds", beds);
                                rooms.add(roomData);
                            }
                        }
                    }
                }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Update student allocation status in students.xml
     */
    public boolean updateStudentAllocationStatus(String rollNumber, String status, String block, String floor, String roomNo, String bedNo) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return false;
        }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String roll = getElementValue(student, "roll_number");
                if (roll.equals(rollNumber)) {
                    // Update allocation status
                    NodeList statusNodes = student.getElementsByTagName("allocation_status");
                    if (statusNodes.getLength() > 0) {
                        statusNodes.item(0).setTextContent(status);
                    } else {
                        addElement(doc, student, "allocation_status", status);
                    }

                    // Add allocation details
                    addElement(doc, student, "allocated_block", block);
                    addElement(doc, student, "allocated_floor", floor);
                    addElement(doc, student, "allocated_room", roomNo);
                    addElement(doc, student, "allocated_bed", bedNo);

                    saveDocument(doc, studentsFile.getAbsolutePath());
                    return true;
                }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
