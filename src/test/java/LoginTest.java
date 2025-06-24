/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.mycompany.instantmessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class LoginTest {

    private Login login;
    private ArrayList<Login.User> registeredUsers;

    @BeforeEach
    void setUp() {
        login = new Login();
        registeredUsers = new ArrayList<>();
        
        // Setup some mock users for testing login scenarios
        String passHashJohn = login.hashPassword("Pass@123");
        if (passHashJohn != null) {
            registeredUsers.add(new Login.User("John", "Doe", "johndoe", passHashJohn, "+27831234567", "IT"));
        }
        
        String passHashJane = login.hashPassword("Secure#456");
        if (passHashJane != null) {
            registeredUsers.add(new Login.User("Jane", "Smith", "janesmith", passHashJane, "+27729876543", "HR"));
        }
    }

    @Test
    void testCheckUserNameValid() {
        assertEquals("Username successfully captured", login.checkUserName("valid_user"));
        assertEquals("Username successfully captured", login.checkUserName("user_name_123"));
    }

    @Test
    void testCheckUserNameTooShort() {
        assertEquals("Username must be between 5 and 15 characters long.", login.checkUserName("abc"));
    }

    @Test
    void testCheckUserNameTooLong() {
        assertEquals("Username must be between 5 and 15 characters long.", login.checkUserName("verylongusernameexample"));
    }

    @Test
    void testCheckUserNameContainsInvalidChars() {
        assertEquals("Username can only contain alphanumeric characters and underscores.", login.checkUserName("user$name"));
        assertEquals("Username can only contain alphanumeric characters and underscores.", login.checkUserName("user name"));
    }
    
    @Test
    void testCheckUserNameNull() {
        // Assuming checkUserName should handle null gracefully based on its implementation
        assertEquals("Username must be between 5 and 15 characters long.", login.checkUserName(null));
    }

    @Test
    void testCheckPasswordComplexityValid() {
        assertTrue(login.checkPasswordComplexity("Password@123"));
        assertTrue(login.checkPasswordComplexity("StrongP@ss1"));
    }

    @Test
    void testCheckPasswordComplexityTooShort() {
        assertFalse(login.checkPasswordComplexity("Short@1"));
    }

    @Test
    void testCheckPasswordComplexityNoUppercase() {
        assertFalse(login.checkPasswordComplexity("password@123"));
    }

    @Test
    void testCheckPasswordComplexityNoDigit() {
        assertFalse(login.checkPasswordComplexity("Password@abc"));
    }

    @Test
    void testCheckPasswordComplexityNoSpecialChar() {
        assertFalse(login.checkPasswordComplexity("Password123"));
    }
    
    @Test
    void testHashAndPasswordVerification() {
        String originalPassword = "MyStrongPassword@123";
        String hashedPassword = login.hashPassword(originalPassword);
        assertNotNull(hashedPassword);
        
        // Verify correct password
        assertTrue(login.verifyPassword(originalPassword, hashedPassword));
        
        // Verify incorrect password
        assertFalse(login.verifyPassword("WrongPassword", hashedPassword));
        
        // Verify null password attempt
        assertFalse(login.verifyPassword(null, hashedPassword));
        
        // Verify with invalid hash format
        assertFalse(login.verifyPassword(originalPassword, "not-a-valid-base64-hash"));
    }

    @Test
    void testReturnLoginStatusSuccessful() {
        Login.User user = login.returnLoginStatus("johndoe", "Pass@123", registeredUsers);
        assertNotNull(user);
        assertEquals("johndoe", user.getUsername());
        assertEquals("John", user.getFirstName());
    }

    @Test
    void testReturnLoginStatusIncorrectPassword() {
        Login.User user = login.returnLoginStatus("johndoe", "WrongPass", registeredUsers);
        assertNull(user); // Should return null for incorrect password
    }

    @Test
    void testReturnLoginStatusUserNotFound() {
        Login.User user = login.returnLoginStatus("nonexistentuser", "SomePass", registeredUsers);
        assertNull(user); // Should return null if user not found
    }

    @Test
    void testReturnLoginStatusEmptyRegisteredUsers() {
        ArrayList<Login.User> emptyUsers = new ArrayList<>();
        Login.User user = login.returnLoginStatus("johndoe", "Pass@123", emptyUsers);
        assertNull(user);
    }
    
    @Test
    void testCheckCellPhoneNumberValid() {
        assertTrue(login.checkCellPhoneNumber("+27831234567")); // Standard 10-digit after +
        assertTrue(login.checkCellPhoneNumber("+12125551234")); // 11 digits
        assertTrue(login.checkCellPhoneNumber("+447911123456")); // 12 digits
    }

    @Test
    void testCheckCellPhoneNumberInvalidFormat() {
        assertFalse(login.checkCellPhoneNumber("0831234567")); // Missing '+'
        assertFalse(login.checkCellPhoneNumber("27831234567")); // Missing '+'
        assertFalse(login.checkCellPhoneNumber("+278312345")); // Too short
        assertFalse(login.checkCellPhoneNumber("+2783123456789")); // Too long
        assertFalse(login.checkCellPhoneNumber("+27abc456789")); // Non-digits
        assertFalse(login.checkCellPhoneNumber("invalid")); // Completely wrong
    }
    
    @Test
    void testCheckCellPhoneNumberNullOrEmpty() {
        assertFalse(login.checkCellPhoneNumber(null));
        assertFalse(login.checkCellPhoneNumber(""));
        assertFalse(login.checkCellPhoneNumber("   ")); // Just spaces
    }
}