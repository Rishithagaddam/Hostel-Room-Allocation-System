package com.hostel;

public class EmailService {
    /**
     * Send allocation email to student
     */
    public static void sendAllocationEmail(String studentName, String email, String rollNo,
                                           String block, String floor, String room, String bed) {
        try {
            String subject = "Hostel Room Allocation Details";
            String body = buildEmailBody(studentName, rollNo, block, floor, room, bed);

            // For now, just log the email (actual email sending requires mail.jar)
            System.out.println("=== ALLOCATION EMAIL ===");
            System.out.println("To: " + email);
            System.out.println("Subject: " + subject);
            System.out.println("Body:\n" + body);
            System.out.println("========================");

            // TODO: Implement actual email sending when javax.mail is available
            // Properties props = new Properties();
            // props.put("mail.smtp.host", "smtp.gmail.com");
            // props.put("mail.smtp.port", "587");
            // props.put("mail.smtp.auth", "true");
            // props.put("mail.smtp.starttls.enable", "true");
            // Session session = Session.getInstance(props, new Authenticator() { ... });
            // Message message = new MimeMessage(session);
            // message.setFrom(new InternetAddress("your-email@gmail.com"));
            // message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            // message.setSubject(subject);
            // message.setText(body);
            // Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildEmailBody(String studentName, String rollNo, String block,
                                        String floor, String room, String bed) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(studentName).append(",\n\n");
        body.append("Your hostel room has been allocated successfully.\n\n");
        body.append("Roll No: ").append(rollNo).append("\n");
        body.append("Block: ").append(block).append("\n");
        body.append("Floor: ").append(floor).append("\n");
        body.append("Room: ").append(room).append("\n");
        body.append("Bed: ").append(bed).append("\n\n");
        body.append("Login Link: http://localhost:8080/hostel-allocation\n\n");
        body.append("Regards,\n");
        body.append("Hostel Warden\n");
        return body.toString();
    }
}
