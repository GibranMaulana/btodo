package com.example.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SecurityUtil provides security-related utilities for password hashing and verification.
 * 
 * This class implements secure password hashing using SHA-256 with salt to prevent
 * rainbow table attacks and ensure password security.
 */
public class SecurityUtil {
    
    private static final SecureRandom random = new SecureRandom();
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 16 bytes = 128 bits
    
    /**
     * Hashes a password using SHA-256 with a random salt.
     * 
     * Process:
     * 1. Generate a random salt
     * 2. Combine salt + password
     * 3. Hash the combination using SHA-256
     * 4. Store salt + hash together
     * 5. Encode as Base64 for storage
     * 
     * @param password The plain text password to hash
     * @return Base64 encoded string containing salt + hash
     */
    public static String hashPassword(String password) {
        try {
            // Step 1: Generate random salt
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Step 2: Get SHA-256 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            
            // Step 3: Add salt to the digest
            md.update(salt);
            
            // Step 4: Hash the password
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Step 5: Combine salt and hash for storage
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Step 6: Encode as Base64 for safe storage
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash.
     * 
     * Process:
     * 1. Decode the stored hash
     * 2. Extract the salt (first 16 bytes)
     * 3. Extract the stored hash (remaining bytes)
     * 4. Hash the provided password with the same salt
     * 5. Compare the results
     * 
     * @param password The plain text password to verify
     * @param storedHash The stored hash (salt + hash encoded in Base64)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Step 1: Decode the stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Step 2: Extract salt (first SALT_LENGTH bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // Step 3: Hash the provided password with the extracted salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes());
            
            // Step 4: Compare with stored hash (bytes after salt)
            if (hashedInput.length != combined.length - SALT_LENGTH) {
                return false;
            }
            
            for (int i = 0; i < hashedInput.length; i++) {
                if (hashedInput[i] != combined[i + SALT_LENGTH]) {
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            // Return false on any error (invalid hash format, etc.)
            return false;
        }
    }
    
    /**
     * Generates a secure random session token.
     * 
     * @return Base64 encoded random token
     */
    public static String generateSessionToken() {
        byte[] token = new byte[32]; // 256 bits
        random.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
    
    /**
     * Demonstrates the security utility functions.
     * Shows how hashing and verification work step by step.
     */
    public static void demonstrateSecurity() {
        System.out.println("=== SecurityUtil Demonstration ===\n");
        
        // Test passwords
        String password1 = "mySecurePassword123";
        String password2 = "anotherPassword456";
        String wrongPassword = "wrongPassword";
        
        System.out.println("1. Password Hashing:");
        System.out.println("   Original password: '" + password1 + "'");
        
        // Hash the same password multiple times to show different salts
        String hash1 = hashPassword(password1);
        String hash2 = hashPassword(password1);
        
        System.out.println("   Hash 1: " + hash1);
        System.out.println("   Hash 2: " + hash2);
        System.out.println("   → Notice: Same password produces different hashes (due to random salt)");
        
        System.out.println("\n2. Password Verification:");
        System.out.println("   Verifying '" + password1 + "' against hash1: " + verifyPassword(password1, hash1));
        System.out.println("   Verifying '" + password1 + "' against hash2: " + verifyPassword(password1, hash2));
        System.out.println("   Verifying '" + wrongPassword + "' against hash1: " + verifyPassword(wrongPassword, hash1));
        
        System.out.println("\n3. Different passwords produce different hashes:");
        String hashA = hashPassword(password1);
        String hashB = hashPassword(password2);
        System.out.println("   Password A hash: " + hashA.substring(0, 20) + "...");
        System.out.println("   Password B hash: " + hashB.substring(0, 20) + "...");
        System.out.println("   → Completely different hashes for different passwords");
        
        System.out.println("\n4. Session Token Generation:");
        System.out.println("   Token 1: " + generateSessionToken());
        System.out.println("   Token 2: " + generateSessionToken());
        System.out.println("   → Each token is unique and unpredictable");
        
        System.out.println("\n=== Security Features Explained ===");
        System.out.println("✅ Salt: Random data added to password before hashing");
        System.out.println("   - Prevents rainbow table attacks");
        System.out.println("   - Makes identical passwords have different hashes");
        System.out.println("✅ SHA-256: Cryptographically secure hash function");
        System.out.println("   - One-way function (cannot reverse)");
        System.out.println("   - Deterministic (same input = same output)");
        System.out.println("✅ Secure Storage: Salt + Hash stored together");
        System.out.println("   - Salt needed for verification");
        System.out.println("   - Base64 encoding for safe text storage");
    }
}
