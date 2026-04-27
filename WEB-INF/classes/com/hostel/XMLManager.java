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
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read student by username
     */
    public Map<String, String> getStudentByUsername(String username) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return null;
            }
            Document doc = documentBuilder.parse(studentsFile);
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
    public boolean addStudent(String name, String year, String email, String rollNumber, String username, String passwordHash, String plainPassword) {
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
            addElement(doc, student, "plain_password", plainPassword);
            addElement(doc, student, "role", "student");
            addElement(doc, student, "year", year);
            addElement(doc, student, "payment_status", "completed");
            addElement(doc, student, "allocation_status", "UNALLOCATED");
            addElement(doc, student, "registration_date", getCurrentDate());

            students.appendChild(student);
            saveDocument(doc, studentsFile.getAbsolutePath());
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
            Document doc = documentBuilder.parse(new File(getFilePath()));
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
            Document doc = documentBuilder.parse(new File(getFilePath()));
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
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return students;
            }
            Document doc = documentBuilder.parse(studentsFile);
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
        } catch (IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * Allocate bed to student - updates hostel.xml bed status
     */
    public boolean allocateRoom(String rollNumber, String block, String floor, String roomNo, String bedNo) {
        try {
            File roomsFile = new File(getFilePath());
            Document doc = documentBuilder.parse(roomsFile);
            NodeList blockList = doc.getElementsByTagName("block");
            for (int i = 0; i < blockList.getLength(); i++) {
                Element blockElem = (Element) blockList.item(i);
                if (!block.equals(blockElem.getAttribute("name"))) continue;
                NodeList floorList = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floorElem = (Element) floorList.item(j);
                    if (!floor.equals(floorElem.getAttribute("number"))) continue;
                    NodeList roomList = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element room = (Element) roomList.item(k);
                        if (!roomNo.equals(room.getAttribute("number"))) continue;
                        NodeList beds = room.getElementsByTagName("bed");
                        for (int l = 0; l < beds.getLength(); l++) {
                            Element bed = (Element) beds.item(l);
                            if (bedNo.equals(bed.getAttribute("number")) &&
                                "available".equalsIgnoreCase(bed.getAttribute("status"))) {
                                bed.setAttribute("status", "occupied");
                                bed.setAttribute("rollNo", rollNumber);
                                saveDocument(doc, roomsFile.getAbsolutePath());
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            Document hostelDoc = documentBuilder.parse(new File(getFilePath()));
            NodeList beds = hostelDoc.getElementsByTagName("bed");
            int totalBeds = beds.getLength();
            int occupiedBeds = 0;
            int availableBeds = 0;

            for (int i = 0; i < totalBeds; i++) {
                Element bed = (Element) beds.item(i);
                String rollNo = bed.getAttribute("rollNo");
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
        data.put("plain_password", getElementValue(student, "plain_password"));
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

    private void setOrCreateElement(Document doc, Element parent, String tagName, String value) {
        NodeList nodes = parent.getElementsByTagName(tagName);

        if (nodes.getLength() > 0) {
            nodes.item(0).setTextContent(value);
        } else {
            Element element = doc.createElement(tagName);
            element.setTextContent(value);
            parent.appendChild(element);
        }
    }

    private int getNextStudentNumber() {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return 1;
            }
            Document doc = documentBuilder.parse(studentsFile);
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
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    /**
     * Add a room to a specific block and floor
     */
    public boolean addRoom(String blockName, String floorNumber, String roomNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Find the floor
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            // Check if room already exists
                            NodeList roomNodes = floor.getElementsByTagName("room");
                            for (int k = 0; k < roomNodes.getLength(); k++) {
                                Element room = (Element) roomNodes.item(k);
                                if (roomNumber.equals(room.getAttribute("number"))) {
                                    return false; // Room already exists
                                }
                            }

                            // Add new room with default 3 beds
                            Element room = doc.createElement("room");
                            room.setAttribute("number", roomNumber);
                            floor.appendChild(room);

                            // Add 3 beds per room
                            for (int bedNum = 1; bedNum <= 3; bedNum++) {
                                Element bed = doc.createElement("bed");
                                bed.setAttribute("number", String.valueOf(bedNum));
                                bed.setAttribute("status", "available");
                                bed.setAttribute("rollNo", "");
                                room.appendChild(bed);
                            }

                            doc.normalize();
                            saveDocument(doc, getFilePath());
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a room from a specific block and floor
     */
    public boolean removeRoom(String blockName, String floorNumber, String roomNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Find the floor
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            // Find and remove the room
                            NodeList roomNodes = floor.getElementsByTagName("room");
                            for (int k = 0; k < roomNodes.getLength(); k++) {
                                Element room = (Element) roomNodes.item(k);
                                if (roomNumber.equals(room.getAttribute("number"))) {
                                    floor.removeChild(room);
                                    doc.normalize();
                                    saveDocument(doc, getFilePath());
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a bed to a specific room
     */
    public boolean addBed(String blockName, String floorNumber, String roomNumber, String bedNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Find the floor
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            // Find the room
                            NodeList roomNodes = floor.getElementsByTagName("room");
                            for (int k = 0; k < roomNodes.getLength(); k++) {
                                Element room = (Element) roomNodes.item(k);
                                if (roomNumber.equals(room.getAttribute("number"))) {
                                    // Check if bed already exists
                                    NodeList bedNodes = room.getElementsByTagName("bed");
                                    for (int l = 0; l < bedNodes.getLength(); l++) {
                                        Element bed = (Element) bedNodes.item(l);
                                        if (bedNumber.equals(bed.getAttribute("number"))) {
                                            return false; // Bed already exists
                                        }
                                    }

                                    // Add new bed
                                    Element bed = doc.createElement("bed");
                                    bed.setAttribute("number", bedNumber);
                                    bed.setAttribute("status", "available");
                                    bed.setAttribute("rollNo", "");
                                    room.appendChild(bed);

                                    doc.normalize();
                                    saveDocument(doc, getFilePath());
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a bed from a specific room
     */
    public boolean removeBed(String blockName, String floorNumber, String roomNumber, String bedNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Find the floor
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            // Find the room
                            NodeList roomNodes = floor.getElementsByTagName("room");
                            for (int k = 0; k < roomNodes.getLength(); k++) {
                                Element room = (Element) roomNodes.item(k);
                                if (roomNumber.equals(room.getAttribute("number"))) {
                                    // Find and remove the bed
                                    NodeList bedNodes = room.getElementsByTagName("bed");
                                    for (int l = 0; l < bedNodes.getLength(); l++) {
                                        Element bed = (Element) bedNodes.item(l);
                                        if (bedNumber.equals(bed.getAttribute("number"))) {
                                            // Check if bed is occupied
                                            if ("occupied".equals(bed.getAttribute("status"))) {
                                                return false; // Cannot remove occupied bed
                                            }
                                            room.removeChild(bed);
                                            doc.normalize();
                                            saveDocument(doc, getFilePath());
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            File file = new File(getFilePath());
            if (!file.exists()) return rooms;
            Document doc = documentBuilder.parse(file);
            NodeList blockNodes = doc.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element blockElem = (Element) blockNodes.item(i);
                if (!block.equals(blockElem.getAttribute("name"))) continue;
                NodeList floorNodes = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorNodes.getLength(); j++) {
                    Element floorElem = (Element) floorNodes.item(j);
                    if (!floor.equals(floorElem.getAttribute("number"))) continue;
                    NodeList roomNodes = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomNodes.getLength(); k++) {
                        Element roomElem = (Element) roomNodes.item(k);
                        String roomNo = roomElem.getAttribute("number");
                        Map<String, Object> roomData = new HashMap<>();
                        roomData.put("roomNo", roomNo);
                        roomData.put("roomId", block + "-" + floor + "-" + roomNo);
                        List<Map<String, String>> beds = new ArrayList<>();
                        NodeList bedNodes = roomElem.getElementsByTagName("bed");
                        for (int l = 0; l < bedNodes.getLength(); l++) {
                            Element bedElem = (Element) bedNodes.item(l);
                            Map<String, String> bedData = new HashMap<>();
                            bedData.put("bedNo", bedElem.getAttribute("number"));
                            bedData.put("bedId", block + "-" + floor + "-" + roomNo + "-" + bedElem.getAttribute("number"));
                            bedData.put("status", bedElem.getAttribute("status"));
                            bedData.put("rollNo", bedElem.getAttribute("rollNo"));
                            beds.add(bedData);
                        }
                        roomData.put("beds", beds);
                        rooms.add(roomData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Add a block to the hostel structure
     */
    public boolean addBlock(String blockName) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Check if block already exists
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    return false; // Block already exists
                }
            }

            // Add new block
            Element block = doc.createElement("block");
            block.setAttribute("name", blockName);
            rooms.appendChild(block);

            doc.normalize();
            saveDocument(doc, getFilePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a block from the hostel structure
     */
    public boolean removeBlock(String blockName) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find and remove the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Check if block has occupied beds
                    if (hasOccupiedBeds(block)) {
                        return false; // Cannot remove block with occupied beds
                    }
                    rooms.removeChild(block);
                    doc.normalize();
                    saveDocument(doc, getFilePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add a floor to a specific block
     */
    public boolean addFloor(String blockName, String floorNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Check if floor already exists
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            return false; // Floor already exists
                        }
                    }

                    // Add new floor
                    Element floor = doc.createElement("floor");
                    floor.setAttribute("number", floorNumber);
                    block.appendChild(floor);

                    doc.normalize();
                    saveDocument(doc, getFilePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove a floor from a specific block
     */
    public boolean removeFloor(String blockName, String floorNumber) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);

            // Find the block
            NodeList blockNodes = rooms.getElementsByTagName("block");
            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    // Find and remove the floor
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        if (floorNumber.equals(floor.getAttribute("number"))) {
                            // Check if floor has occupied beds
                            if (hasOccupiedBeds(floor)) {
                                return false; // Cannot remove floor with occupied beds
                            }
                            block.removeChild(floor);
                            doc.normalize();
                            saveDocument(doc, getFilePath());
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all blocks
     */
    public List<String> getAllBlocks() {
        List<String> blocks = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);
            NodeList blockNodes = rooms.getElementsByTagName("block");

            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                blocks.add(block.getAttribute("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blocks;
    }

    /**
     * Get floors by block
     */
    public List<String> getFloorsByBlock(String blockName) {
        List<String> floors = new ArrayList<>();
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            Element hostel = doc.getDocumentElement();
            Element rooms = (Element) hostel.getElementsByTagName("rooms").item(0);
            NodeList blockNodes = rooms.getElementsByTagName("block");

            for (int i = 0; i < blockNodes.getLength(); i++) {
                Element block = (Element) blockNodes.item(i);
                if (blockName.equals(block.getAttribute("name"))) {
                    NodeList floorNodes = block.getElementsByTagName("floor");
                    for (int j = 0; j < floorNodes.getLength(); j++) {
                        Element floor = (Element) floorNodes.item(j);
                        floors.add(floor.getAttribute("number"));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return floors;
    }

    /**
     * Check if an element has occupied beds
     */
    private boolean hasOccupiedBeds(Element element) {
        NodeList bedNodes = element.getElementsByTagName("bed");
        for (int i = 0; i < bedNodes.getLength(); i++) {
            Element bed = (Element) bedNodes.item(i);
            if ("occupied".equals(bed.getAttribute("status"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update student allocation status in students.xml
     */
    public boolean updateStudentAllocationStatus(String rollNumber, String status, String block, String floor, String roomNo, String bedNo) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) return false;

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String roll = getElementValue(student, "roll_number");

                if (roll != null && roll.trim().equals(rollNumber.trim())) {
                    setOrCreateElement(doc, student, "allocation_status", status);
                    setOrCreateElement(doc, student, "allocated_block", block);
                    setOrCreateElement(doc, student, "allocated_floor", floor);
                    setOrCreateElement(doc, student, "allocated_room", roomNo);
                    setOrCreateElement(doc, student, "allocated_bed", bedNo);

                    saveDocument(doc, studentsFile.getAbsolutePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update student password hash in students.xml
     */
    public boolean updateStudentPassword(String rollNumber, String passwordHash) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) return false;

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String roll = getElementValue(student, "roll_number");

                if (roll != null && roll.trim().equals(rollNumber.trim())) {
                    setOrCreateElement(doc, student, "password_hash", passwordHash);

                    saveDocument(doc, studentsFile.getAbsolutePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get student by ID (student_id like S001, S002, etc.)
     */
    public Map<String, String> getStudentById(String studentId) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return null;
            }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList students = doc.getElementsByTagName("student");

            for (int i = 0; i < students.getLength(); i++) {
                Element student = (Element) students.item(i);
                String sid = getElementValue(student, "student_id");
                if (sid != null && sid.equals(studentId)) {
                    return parseStudentElement(student);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update student details (name, email, year only - not roll number)
     */
    public boolean updateStudent(String studentId, String name, String email, String year) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return false;
            }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String sid = getElementValue(student, "student_id");

                if (sid != null && sid.equals(studentId)) {
                    setOrCreateElement(doc, student, "name", name);
                    setOrCreateElement(doc, student, "email", email);
                    setOrCreateElement(doc, student, "year", year);

                    saveDocument(doc, studentsFile.getAbsolutePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a student from students.xml and release allocated bed if allocated
     */
    public boolean deleteStudent(String studentId) {
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return false;
            }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String sid = getElementValue(student, "student_id");

                if (sid != null && sid.equals(studentId)) {
                    // If student is allocated, release the bed
                    String allocStatus = getElementValue(student, "allocation_status");
                    if ("ALLOCATED".equals(allocStatus)) {
                        String block = getElementValue(student, "allocated_block");
                        String floor = getElementValue(student, "allocated_floor");
                        String room = getElementValue(student, "allocated_room");
                        String bed = getElementValue(student, "allocated_bed");

                        if (block != null && floor != null && room != null && bed != null) {
                            // Release bed in hostel.xml
                            releaseBed(block, floor, room, bed);
                        }
                    }

                    // Remove student element from DOM
                    Element root = doc.getDocumentElement();
                    root.removeChild(student);
                    saveDocument(doc, studentsFile.getAbsolutePath());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search students by name or year
     * Returns list of matching students
     */
    public List<Map<String, String>> searchStudents(String searchTerm) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                return results;
            }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");
            String searchLower = searchTerm.toLowerCase().trim();

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                String role = getElementValue(student, "role");

                // Skip warden accounts
                if ("warden".equals(role)) {
                    continue;
                }

                String name = getElementValue(student, "name");
                String year = getElementValue(student, "year");

                // Check if name or year matches search term
                if ((name != null && name.toLowerCase().contains(searchLower)) ||
                    (year != null && year.equals(searchLower))) {
                    results.add(parseStudentElement(student));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Check if a bed is available in hostel.xml
     */
    public boolean isBedAvailable(String block, String floor, String room, String bed) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList blockList = doc.getElementsByTagName("block");

            for (int i = 0; i < blockList.getLength(); i++) {
                Element blockElem = (Element) blockList.item(i);
                if (!block.equals(blockElem.getAttribute("name"))) continue;

                NodeList floorList = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floorElem = (Element) floorList.item(j);
                    if (!floor.equals(floorElem.getAttribute("number"))) continue;

                    NodeList roomList = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element roomElem = (Element) roomList.item(k);
                        if (!room.equals(roomElem.getAttribute("number"))) continue;

                        NodeList bedList = roomElem.getElementsByTagName("bed");
                        for (int l = 0; l < bedList.getLength(); l++) {
                            Element bedElem = (Element) bedList.item(l);
                            if (bed.equals(bedElem.getAttribute("number"))) {
                                String status = bedElem.getAttribute("status");
                                return "available".equalsIgnoreCase(status);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Release a bed (mark as available and clear rollNo)
     */
    private boolean releaseBed(String block, String floor, String room, String bed) {
        try {
            File hostelFile = new File(getFilePath());
            Document doc = documentBuilder.parse(hostelFile);
            NodeList blockList = doc.getElementsByTagName("block");

            for (int i = 0; i < blockList.getLength(); i++) {
                Element blockElem = (Element) blockList.item(i);
                if (!block.equals(blockElem.getAttribute("name"))) continue;

                NodeList floorList = blockElem.getElementsByTagName("floor");
                for (int j = 0; j < floorList.getLength(); j++) {
                    Element floorElem = (Element) floorList.item(j);
                    if (!floor.equals(floorElem.getAttribute("number"))) continue;

                    NodeList roomList = floorElem.getElementsByTagName("room");
                    for (int k = 0; k < roomList.getLength(); k++) {
                        Element roomElem = (Element) roomList.item(k);
                        if (!room.equals(roomElem.getAttribute("number"))) continue;

                        NodeList bedList = roomElem.getElementsByTagName("bed");
                        for (int l = 0; l < bedList.getLength(); l++) {
                            Element bedElem = (Element) bedList.item(l);
                            if (bed.equals(bedElem.getAttribute("number"))) {
                                bedElem.setAttribute("status", "available");
                                bedElem.setAttribute("rollNo", "");
                                saveDocument(doc, hostelFile.getAbsolutePath());
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get count of available beds for a specific year
     */
    public int getAvailableBedCountForYear(String year) {
        try {
            Document doc = documentBuilder.parse(new File(getFilePath()));
            NodeList beds = doc.getElementsByTagName("bed");
            int count = 0;

            for (int i = 0; i < beds.getLength(); i++) {
                Element bed = (Element) beds.item(i);
                String status = bed.getAttribute("status");
                if ("available".equalsIgnoreCase(status)) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Check if duplicate roll number exists
     */
    public boolean isDuplicateStudent(String rollNumber) {
        return rollNumberExists(rollNumber);
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Get all students (including wardens and allocated/unallocated)
     */
    public List<Map<String, String>> getAllStudents() {
        List<Map<String, String>> students = new ArrayList<>();
        try {
            File studentsFile = new File(baseDir + File.separator + DATA_DIR + "students.xml");
            if (!studentsFile.exists()) {
                System.out.println("Students file does not exist: " + studentsFile.getAbsolutePath());
                return students;
            }

            Document doc = documentBuilder.parse(studentsFile);
            NodeList studentList = doc.getElementsByTagName("student");

            for (int i = 0; i < studentList.getLength(); i++) {
                Element student = (Element) studentList.item(i);
                try {
                    Map<String, String> studentData = parseStudentElement(student);
                    if (studentData != null) {
                        students.add(studentData);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing student element " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getAllStudents: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
}



