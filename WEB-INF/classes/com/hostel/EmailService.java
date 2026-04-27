package com.hostel;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    /**
     * Send allocation email to student
     * Returns true if email sent successfully, false otherwise
     */
    public static boolean sendAllocationEmail(String studentName, String email, String rollNo,
                                           String password, String block, String floor, String room, String bed) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Email address is empty, cannot send allocation email");
            return false;
        }

        // Validate email format more thoroughly
        if (!isValidEmail(email)) {
            System.err.println("Invalid email format: " + email);
            return false;
        }

        System.out.println("DEBUG: Starting email send process...");
        System.out.println("DEBUG: Student: " + studentName + ", Email: " + email);

        try {
            String subject = "Hostel Room Allocation Confirmation";
            String body = buildEmailBody(studentName, rollNo, password, block, floor, room, bed);

            System.out.println("DEBUG: Sending email to: " + email);
            System.out.println("DEBUG: From: " + Constants.EMAIL_USERNAME);
            System.out.println("DEBUG: Password: " + Constants.EMAIL_PASSWORD);
            System.out.println("DEBUG: Subject: " + subject);

            // Set up mail properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");
            props.put("mail.debug", "true");

            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Constants.EMAIL_USERNAME, Constants.EMAIL_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Constants.EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(body);

            System.out.println("DEBUG: Message created, attempting to send...");

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", Constants.EMAIL_USERNAME, Constants.EMAIL_PASSWORD);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("Allocation email sent successfully to: " + email);
            return true;

        } catch (Exception e) {
            System.err.println("Failed to send allocation email to " + email + ": " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getName());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }

            // Check for common Gmail SMTP errors
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("535") || errorMsg.contains("Authentication failed")) {
                    System.err.println("Gmail authentication failed. Please check:");
                    System.err.println("1. 2FA is enabled on Gmail account");
                    System.err.println("2. App password is correct (generate from Google Account settings)");
                    System.err.println("3. App password has no spaces");
                } else if (errorMsg.contains("530") || errorMsg.contains("530")) {
                    System.err.println("Gmail SMTP access blocked. Please check Gmail security settings.");
                }
            }

            e.printStackTrace();
            return false;
        }
    }

    private static String buildEmailBody(String studentName, String rollNo, String password,
                                        String block, String floor, String room, String bed) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(studentName).append(",\n\n");
        body.append("Your hostel accommodation has been successfully allocated.\n\n");
        body.append("Login Credentials:\n");
        body.append("Username: ").append(rollNo).append("\n");
        body.append("Password: ").append(password).append("\n\n");
        body.append("Allocation Details:\n");
        body.append("Roll Number: ").append(rollNo).append("\n");
        body.append("Block: ").append(block).append("\n");
        body.append("Floor: ").append(floor).append("\n");
        body.append("Room Number: ").append(room).append("\n");
        body.append("Bed Number: ").append(bed).append("\n\n");
        body.append("Please keep this email for future reference.\n\n");
        body.append("Regards,\n");
        body.append("Hostel Management / Warden");
        return body.toString();
    }

    private static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Basic email regex pattern
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
