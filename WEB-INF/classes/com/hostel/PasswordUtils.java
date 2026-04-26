package com.hostel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordUtils {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    /**
     * Generate a random password of 8 characters
     */
    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Hash a password using MD5
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback to plain text
        }
    }

    /**
     * Verify a password against its hash
     */
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    /**
     * Generate a username from student name (first 3 chars of first name + first 3 chars of last name + random 3 digits)
     */
    public static String generateUsername(String fullName) {
        String[] parts = fullName.trim().toLowerCase().split("\\s+");
        StringBuilder username = new StringBuilder();

        if (parts.length > 0) {
            username.append(parts[0].substring(0, Math.min(3, parts[0].length())));
        }
        if (parts.length > 1) {
            username.append(parts[parts.length - 1].substring(0, Math.min(3, parts[parts.length - 1].length())));
        }

        // Add random 3 digits to make it unique
        for (int i = 0; i < 3; i++) {
            username.append(RANDOM.nextInt(10));
        }

        return username.toString();
    }
}
