package com.hostel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();
    private static final int BCRYPT_LOG_ROUNDS = 10;

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
     * Hash a password using BCrypt with salt
     * This is the primary hashing method for all new passwords
     */
    public static String hashPassword(String password) {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
        } catch (IllegalArgumentException e) {
            // Fallback to MD5 if BCrypt fails (should not happen)
            return hashPasswordMD5(password);
        }
    }

    /**
     * Verify a password against its hash
     * Supports both BCrypt and legacy MD5 hashes
     */
    public static boolean verifyPassword(String password, String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }

        // Check if it's a BCrypt hash (starts with $2a$, $2b$, or $2y$)
        if (isBCryptHash(hash)) {
            try {
                return BCrypt.checkpw(password, hash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // Fallback to legacy MD5 verification for backward compatibility
        return verifyPasswordMD5(password, hash);
    }

    /**
     * Check if a hash is in BCrypt format
     */
    public static boolean isBCryptHash(String hash) {
        if (hash == null || hash.length() < 4) {
            return false;
        }
        return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
    }

    /**
     * Legacy MD5 hashing (for backward compatibility only)
     * This method should only be used to verify existing MD5 hashes
     */
    private static String hashPasswordMD5(String password) {
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
     * Legacy MD5 password verification (for backward compatibility only)
     */
    private static boolean verifyPasswordMD5(String password, String hash) {
        return hashPasswordMD5(password).equals(hash);
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
