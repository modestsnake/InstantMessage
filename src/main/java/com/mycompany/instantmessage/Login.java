package com.mycompany.instantmessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException; // Import added
import java.security.spec.KeySpec; // Import added
import javax.crypto.SecretKeyFactory; // Import added
import javax.crypto.spec.PBEKeySpec; // Import added
import java.util.ArrayList;
import java.util.Arrays; // Import added for Arrays.equals
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login {

    public static class User implements java.io.Serializable {
        private static final long serialVersionUID = 1L; // For serialization
        private String firstName;
        private String lastName;
        private String username;
        private String passwordHash; // Store hashed password
        private String cellPhoneNumber;
        private String department;

        public User(String firstName, String lastName, String username, String passwordHash, String cellPhoneNumber, String department) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.passwordHash = passwordHash;
            this.cellPhoneNumber = cellPhoneNumber;
            this.department = department;
        }

        // Getters
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getUsername() { return username; }
        public String getPasswordHash() { return passwordHash; }
        public String getCellPhoneNumber() { return cellPhoneNumber; }
        public String getDepartment() { return department; }

        @Override
        public String toString() {
            return "User{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", username='" + username + '\'' +
                    ", cellPhoneNumber='" + cellPhoneNumber + '\'' +
                    ", department='" + department + '\'' +
                    '}';
        }
    }

    public String checkUserName(String username) {
        if (username == null || username.length() < 5 || username.length() > 15) {
            return "Username must be between 5 and 15 characters long.";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) { // Allows letters, numbers, and underscore
            return "Username can only contain alphanumeric characters and underscores.";
        }
        // If all checks pass
        return "Username successfully captured";
    }

    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false; // Minimum 8 characters
        }

        Pattern uppercasePattern = Pattern.compile(".*[A-Z].*");
        Pattern digitPattern = Pattern.compile(".*\\d.*");
        Pattern specialCharPattern = Pattern.compile(".*[^a-zA-Z0-9].*"); // Any non-alphanumeric

        return uppercasePattern.matcher(password).matches() &&
               digitPattern.matcher(password).matches() &&
               specialCharPattern.matcher(password).matches();
    }

    // Changed to public so SecureLoginApp can use it
    public String hashPassword(String password) {
        if (password == null) { // Added null check for password
            return null;
        }
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt); // Generate a random salt

            // Using PBKDF2 for password hashing, which is more secure than simple SHA-256
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128); // 128-bit hash
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

            // Combine salt and hash for storage (prepend salt to hash)
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return null; // Handle error appropriately
        }
    }

    // Public method to verify password
    public boolean verifyPassword(String password, String storedPasswordHash) {
        // ADDED/CORRECTED NULL CHECKS
        if (password == null || storedPasswordHash == null || storedPasswordHash.isEmpty()) {
            return false;
        }
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(storedPasswordHash);
            
            // Ensure the decoded hash is long enough to contain salt and hash
            if (saltAndHash.length < 16) { // 16 bytes for salt
                return false;
            }

            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16); // Extract salt
            
            // Removed duplicate KeySpec and SecretKeyFactory declarations
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Extract the original hash part
            byte[] originalHash = new byte[saltAndHash.length - 16];
            System.arraycopy(saltAndHash, 16, originalHash, 0, originalHash.length);

            // Compare the newly generated hash with the stored hash part
            return Arrays.equals(testHash, originalHash);

        } catch (IllegalArgumentException e) {
            // This can happen if storedPasswordHash is not valid Base64
            System.err.println("Error decoding stored password hash (invalid Base64): " + e.getMessage());
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    // Changed to return Login.User or null
    public User returnLoginStatus(String username, String password, ArrayList<User> registeredUsers) {
        // ADDED NULL CHECKS FOR INPUTS
        if (username == null || password == null || registeredUsers == null) {
            return null;
        }
        for (User user : registeredUsers) {
            if (user != null && user.getUsername().equals(username)) { // Added null check for user
                if (verifyPassword(password, user.getPasswordHash())) {
                    return user; // Return the logged-in User object
                } else {
                    return null; // Password incorrect
                }
            }
        }
        return null; // User not found
    }

    // Method to check cell phone number format
    public boolean checkCellPhoneNumber(String phoneNumber) {
        // ADDED NULL/EMPTY CHECK
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // Regex for phone numbers like +27831234567
        // Starts with '+', followed by 10 to 12 digits.
        String regex = "^\\+[0-9]{10,12}$"; // Used [0-9] for clarity and consistency
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}