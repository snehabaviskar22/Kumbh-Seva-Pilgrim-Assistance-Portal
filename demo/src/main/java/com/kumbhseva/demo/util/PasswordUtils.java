package com.kumbhseva.demo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    /**
     * Hashes password using SHA-256 producing a 64-character lowercase hex string.
     */
    public static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies user password. Supports both modern SHA-256 hashes and backward-compatible
     * plaintext passwords for existing demo accounts.
     */
    public static boolean verifyPassword(String rawPassword, String storedHashOrPlain) {
        if (rawPassword == null || storedHashOrPlain == null) return false;
        
        // Exact match (legacy plaintext accounts e.g. '12345', 'admin123', 'volunteer123')
        if (rawPassword.equals(storedHashOrPlain)) {
            return true;
        }

        // Hash comparison
        String hashed = hashPassword(rawPassword);
        return hashed.equalsIgnoreCase(storedHashOrPlain);
    }
}
