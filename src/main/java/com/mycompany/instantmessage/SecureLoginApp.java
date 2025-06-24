/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.instantmessage;

import javax.swing.JOptionPane; // New import for JOptionPane
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
// <-- ADD THIS IMPORT

public class SecureLoginApp {

    // Removed the Scanner, as JOptionPane handles input/output directly

    private static ArrayList<Login.User> registeredUsers = new ArrayList<>();
    private static Login.User loggedInUser = null;

    // Static lists for messages
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static final String STORED_MESSAGES_FILE = "stored_messages.json";
    private static final String REGISTERED_USERS_FILE = "registered_users.ser"; // For user persistence

    // --- Main Application Flow ---
    public static void main(String[] args) {
        loadUsersFromFile();
        storedMessages = loadStoredMessagesFromFile();
        displayMainMenu();
        // JOptionPane applications typically don't have a specific "close" like scanner.close()
        // The JVM will exit when all non-daemon threads finish, which happens when the main dialog closes.
    }

    // Method to display the main menu using JOptionPane
    public static void displayMainMenu() {
        String choiceStr;
        int choice;
        do {
            choiceStr = JOptionPane.showInputDialog(
                null, // Parent component (null for default placement)
                "--- Main Menu ---\n" +
                "1. Register\n" +
                "2. Login\n" +
                "3. Exit\n" +
                "Enter your choice:",
                "Instant Message App", // Dialog title
                JOptionPane.QUESTION_MESSAGE // Message type (e.g., for icon)
            );

            if (choiceStr == null) { // User clicked Cancel or closed the dialog
                choice = 3; // Treat as exit
            } else {
                try {
                    choice = Integer.parseInt(choiceStr);
                    switch (choice) {
                        case 1:
                            registerUser();
                            break;
                        case 2:
                            Login.User user = login();
                            if (user != null) {
                                loggedInUser = user;
                                JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                messageMenu();
                            } else {
                                JOptionPane.showMessageDialog(null, "Login failed. Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
                            }
                            break;
                        case 3:
                            JOptionPane.showMessageDialog(null, "Exiting application. Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    choice = 0; // Set to invalid choice to re-loop
                }
            }
        } while (choice != 3);
    }

    // --- User Registration and Login (updated for JOptionPane) ---

    public static void registerUser() {
        Login loginHandler = new Login();

        String firstName;
        while (true) {
            firstName = JOptionPane.showInputDialog(null, "Enter first name:", "Registration", JOptionPane.QUESTION_MESSAGE);
            if (firstName == null) return; // User cancelled
            if (!firstName.trim().isEmpty()) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "First name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String lastName;
        while (true) {
            lastName = JOptionPane.showInputDialog(null, "Enter last name:", "Registration", JOptionPane.QUESTION_MESSAGE);
            if (lastName == null) return; // User cancelled
            if (!lastName.trim().isEmpty()) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Last name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String username;
        while (true) {
            username = JOptionPane.showInputDialog(null, "Enter desired username:", "Registration", JOptionPane.QUESTION_MESSAGE);
            if (username == null) return; // User cancelled
            String usernameValidationResult = loginHandler.checkUserName(username);
            // FIX for incompatible types: String cannot be converted to boolean
            // Now checks the String value returned by checkUserName
            if (usernameValidationResult.equals("Username successfully captured")) { // Corrected line 133
                boolean usernameExists = false;
                for (Login.User existingUser : registeredUsers) {
                    if (existingUser.getUsername().equals(username)) {
                        usernameExists = true;
                        break;
                    }
                }
                if (usernameExists) {
                    JOptionPane.showMessageDialog(null, "Username already taken. Please choose another.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            } else {
                JOptionPane.showMessageDialog(null, usernameValidationResult, "Username Validation", JOptionPane.ERROR_MESSAGE); // Show the specific error message
            }
        }

        String password;
        while (true) {
            password = JOptionPane.showInputDialog(null, "Enter desired password:", "Registration", JOptionPane.QUESTION_MESSAGE);
            if (password == null) return; // User cancelled
            if (loginHandler.checkPasswordComplexity(password)) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long, contain an uppercase letter, a digit, and a special character.", "Password Complexity", JOptionPane.ERROR_MESSAGE);
            }
        }
        String passwordHash = loginHandler.hashPassword(password); // This requires hashPassword to be public in Login.java
        if (passwordHash == null) {
            JOptionPane.showMessageDialog(null, "Failed to hash password. Registration aborted.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cellPhoneNumber;
        while (true) {
            cellPhoneNumber = JOptionPane.showInputDialog(null, "Enter cell phone number (e.g., +27831234567):", "Registration", JOptionPane.QUESTION_MESSAGE);
            if (cellPhoneNumber == null) return; // User cancelled
            // This requires checkCellPhoneNumber to return boolean in Login.java
            if (loginHandler.checkCellPhoneNumber(cellPhoneNumber)) { // Corrected line 149
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid phone number format. Must start with '+' followed by 10-12 digits.", "Phone Number Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        String department = JOptionPane.showInputDialog(null, "Enter department:", "Registration", JOptionPane.QUESTION_MESSAGE);
        if (department == null) return; // User cancelled

        Login.User newUser = new Login.User(firstName, lastName, username, passwordHash, cellPhoneNumber, department);
        registeredUsers.add(newUser);
        saveUsersToFile();
        JOptionPane.showMessageDialog(null, "Registration successful for user: " + newUser.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static Login.User login() {
        Login loginHandler = new Login();
        String username = JOptionPane.showInputDialog(null, "Enter username:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (username == null) return null; // User cancelled
        String password = JOptionPane.showInputDialog(null, "Enter password:", "Login", JOptionPane.QUESTION_MESSAGE);
        if (password == null) return null; // User cancelled
        // This requires returnLoginStatus to return Login.User and take 3 args in Login.java
        return loginHandler.returnLoginStatus(username, password, registeredUsers); // Corrected line 171
    }

    // --- User Persistence (no change needed here as it's file I/O) ---

    @SuppressWarnings("unchecked")
    private static void loadUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(REGISTERED_USERS_FILE))) {
            registeredUsers = (ArrayList<Login.User>) ois.readObject();
            System.out.println("Registered users loaded from " + REGISTERED_USERS_FILE); // Keep console logging for internal dev info
        } catch (FileNotFoundException e) {
            System.out.println("No existing registered users file found. Starting fresh."); // Keep console logging
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading registered users: " + e.getMessage()); // Keep console logging
        }
    }

    private static void saveUsersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(REGISTERED_USERS_FILE))) {
            oos.writeObject(registeredUsers);
            System.out.println("Registered users saved to " + REGISTERED_USERS_FILE); // Keep console logging
        } catch (IOException e) {
            System.err.println("Error saving registered users: " + e.getMessage()); // Keep console logging
        }
    }

    // --- Message Management Menu (updated for JOptionPane) ---

    public static void messageMenu() {
        String choiceStr;
        int choice;
        do {
            choiceStr = JOptionPane.showInputDialog(
                null,
                "--- Message Management Menu ---\n" +
                "1. Send New Message\n" +
                "2. Display All Sent Messages (Recipient and Content)\n" +
                "3. Display Longest Sent Message\n" +
                "4. Search Message by ID\n" +
                "5. Search Messages by Recipient\n" +
                "6. Delete Message by Hash\n" +
                "7. Display Full Report\n" +
                "8. Back to Main Menu\n" +
                "Enter your choice:",
                "Message Management",
                JOptionPane.QUESTION_MESSAGE
            );

            if (choiceStr == null) { // User cancelled
                choice = 8; // Treat as exit
            } else {
                try {
                    choice = Integer.parseInt(choiceStr);
                    ArrayList<Message> combinedMessages = new ArrayList<>();
                    combinedMessages.addAll(sentMessages);
                    combinedMessages.addAll(storedMessages);

                    switch (choice) {
                        case 1:
                            sendNewMessage();
                            break;
                        case 2:
                            displayAllSentMessages(sentMessages);
                            break;
                        case 3:
                            displayLongestSentMessage(sentMessages);
                            break;
                        case 4:
                            searchMessageById(combinedMessages);
                            break;
                        case 5:
                            searchMessagesByRecipient(combinedMessages);
                            break;
                        case 6:
                            deleteMessageByHash(combinedMessages);
                            // Re-filter lists after deletion to reflect changes
                            sentMessages.clear();
                            storedMessages.clear();
                            for (Message msg : combinedMessages) {
                                // These lines require getStatus() to exist in Message.java
                                if (msg.getStatus().equals("Sent")) { // Corrected line 250
                                    sentMessages.add(msg);
                                } else if (msg.getStatus().equals("Stored")) { // Corrected line 252
                                    storedMessages.add(msg);
                                }
                            }
                            saveStoredMessagesToFile(storedMessages); // Re-save stored messages if any were deleted
                            break;
                        case 7:
                            StringBuilder report = new StringBuilder();
                            report.append("--- Full Report of Sent Messages ---\n");
                            report.append(getFullReportString(sentMessages));
                            report.append("\n--- Full Report of Stored Messages ---\n");
                            report.append(getFullReportString(storedMessages));
                            JOptionPane.showMessageDialog(null, report.toString(), "Full Report", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        case 8:
                            JOptionPane.showMessageDialog(null, "Returning to Main Menu.", "Navigation", JOptionPane.INFORMATION_MESSAGE);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid choice. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    choice = 0; // Set to invalid choice to re-loop
                }
            }
        } while (choice != 8);
    }

    // --- Message Management Functions (updated for JOptionPane) ---

    public static void sendNewMessage() {
        String recipient = JOptionPane.showInputDialog(null, "Enter recipient:", "Send Message", JOptionPane.QUESTION_MESSAGE);
        if (recipient == null) return;

        String content;
        while (true) {
            content = JOptionPane.showInputDialog(null, "Enter message content (max 250 characters):", "Send Message", JOptionPane.QUESTION_MESSAGE);
            if (content == null) return; // User cancelled
            if (content.length() <= 250) {
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Message content exceeds 250 characters. Please re-enter.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        Message newMessage = new Message(0, recipient, content);
        String messageDetails = "Message ID: " + newMessage.getMessageId() + "\n" +
                                "Recipient: " + newMessage.getRecipient() + "\n" +
                                "Content: " + newMessage.getMessageContent() + "\n" +
                                "Hash: " + newMessage.getMessageHash();
        JOptionPane.showMessageDialog(null, messageDetails, "Message Details", JOptionPane.INFORMATION_MESSAGE);

        String choiceStr = JOptionPane.showInputDialog(
            null,
            "Choose action for this message:\n" +
            "1. Send\n" +
            "2. Disregard\n" +
            "3. Store\n" +
            "Enter your choice:",
            "Message Action",
            JOptionPane.QUESTION_MESSAGE
        );

        if (choiceStr == null) { // User cancelled
            JOptionPane.showMessageDialog(null, "Message action cancelled.", "Action Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int choice = Integer.parseInt(choiceStr);
            newMessage.handleMessageChoice(choice);
            // These lines require getStatus() to exist in Message.java
            JOptionPane.showMessageDialog(null, "Message actioned: " + newMessage.getStatus(), "Message Status", JOptionPane.INFORMATION_MESSAGE); // Corrected line 323

            if (newMessage.getStatus().equals("Sent")) { // Corrected line 325
                sentMessages.add(newMessage);
            } else if (newMessage.getStatus().equals("Stored")) { // Corrected line 327
                storedMessages.add(newMessage);
                saveStoredMessagesToFile(storedMessages); // Save immediately
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number for message action.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void displayAllSentMessages(ArrayList<Message> messages) {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.", "All Sent Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder messageList = new StringBuilder();
        messageList.append("--- All Sent Messages (Recipient and Content) ---\n");
        for (Message msg : messages) {
            messageList.append("Recipient: ").append(msg.getRecipient())
                       .append(", Content: ").append(msg.getMessageContent()).append("\n");
        }
        JOptionPane.showMessageDialog(null, messageList.toString(), "All Sent Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void displayLongestSentMessage(ArrayList<Message> messages) {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.", "Longest Sent Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Message longestMessage = null;
        for (Message msg : messages) {
            if (longestMessage == null || msg.getMessageContent().length() > longestMessage.getMessageContent().length()) {
                longestMessage = msg;
            }
        }

        if (longestMessage != null) {
            String longestMsgDetails = "--- Longest Sent Message ---\n" +
                                       "Recipient: " + longestMessage.getRecipient() + "\n" +
                                       "Content: " + longestMessage.getMessageContent() + "\n" +
                                       "Length: " + longestMessage.getMessageContent().length() + " characters.";
            JOptionPane.showMessageDialog(null, longestMsgDetails, "Longest Sent Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Note: Scanner parameter removed, as we use JOptionPane internally
    public static void searchMessageById(ArrayList<Message> messages) {
        String searchId = JOptionPane.showInputDialog(null, "Enter Message ID to search:", "Search by ID", JOptionPane.QUESTION_MESSAGE);
        if (searchId == null) return; // User cancelled

        Message foundMessage = null;
        for (Message msg : messages) {
            if (msg.getMessageId().equals(searchId)) {
                foundMessage = msg;
                break;
            }
        }

        if (foundMessage != null) {
            String messageDetails = "--- Search Result by Message ID ---\n" +
                                    "ID: " + foundMessage.getMessageId() + "\n" +
                                    "Recipient: " + foundMessage.getRecipient() + "\n" +
                                    "Content: " + foundMessage.getMessageContent();
            JOptionPane.showMessageDialog(null, messageDetails, "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Message with ID '" + searchId + "' not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Note: Scanner parameter removed
    public static void searchMessagesByRecipient(ArrayList<Message> messages) {
        String searchRecipient = JOptionPane.showInputDialog(null, "Enter Recipient to search:", "Search by Recipient", JOptionPane.QUESTION_MESSAGE);
        if (searchRecipient == null) return; // User cancelled

        StringBuilder results = new StringBuilder();
        boolean found = false;
        for (Message msg : messages) {
            if (msg.getRecipient().equalsIgnoreCase(searchRecipient)) {
                results.append("ID: ").append(msg.getMessageId())
                       .append(", Content: ").append(msg.getMessageContent()).append("\n");
                found = true;
            }
        }

        if (found) {
            JOptionPane.showMessageDialog(null, "--- Search Results for Recipient ---\n" + results.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No messages found for recipient '" + searchRecipient + "'.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Note: Scanner parameter removed
    public static void deleteMessageByHash(ArrayList<Message> messages) {
        String searchHash = JOptionPane.showInputDialog(null, "Enter Message Hash to delete:", "Delete Message", JOptionPane.QUESTION_MESSAGE);
        if (searchHash == null) return; // User cancelled

        boolean removed = false;
        // Corrected line 424 (missing import for Iterator)
        Iterator<Message> iterator = messages.iterator();
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            if (msg.getMessageHash().equals(searchHash)) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        if (removed) {
            JOptionPane.showMessageDialog(null, "Message with hash '" + searchHash + "' deleted successfully.", "Deletion Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Message with hash '" + searchHash + "' not found.", "Deletion Status", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Helper method to get formatted report string for JOptionPane
    public static String getFullReportString(ArrayList<Message> messages) {
        StringBuilder report = new StringBuilder();
        if (messages.isEmpty()) {
            report.append("No messages in this list to report.\n");
        } else {
            for (Message msg : messages) {
                report.append("Message ID: ").append(msg.getMessageId()).append("\n");
                report.append("  Recipient: ").append(msg.getRecipient()).append("\n");
                report.append("  Content: ").append(msg.getMessageContent()).append("\n");
                report.append("  Hash: ").append(msg.getMessageHash()).append("\n");
                // This line requires getStatus() to exist in Message.java
                report.append("  Status: ").append(msg.getStatus()).append("\n"); // Corrected line 452
                report.append("--------------------\n");
            }
        }
        return report.toString();
    }

    // Message Persistence (no change needed here as it's file I/O)
    public static ArrayList<Message> loadStoredMessagesFromFile() {
        File file = new File(STORED_MESSAGES_FILE);
        if (!file.exists()) {
            System.out.println("No existing stored messages file found. Starting fresh.");
            return new ArrayList<>();
        }

        ArrayList<Message> loadedMessages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(STORED_MESSAGES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = extractJsonValue(line, "id");
                String recipient = extractJsonValue(line, "recipient");
                String content = extractJsonValue(line, "content");
                String hash = extractJsonValue(line, "hash");
                String status = extractJsonValue(line, "status");

                if (id != null && recipient != null && content != null && hash != null && status != null) {
                    // This line requires the 5-String constructor in Message.java
                    loadedMessages.add(new Message(id, recipient, content, hash, status)); // Corrected line 478
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading stored messages: " + e.getMessage());
        }
        return loadedMessages;
    }

    public static void saveStoredMessagesToFile(ArrayList<Message> messages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STORED_MESSAGES_FILE))) {
            for (Message msg : messages) {
                String jsonLine = String.format("{\"id\":\"%s\",\"recipient\":\"%s\",\"content\":\"%s\",\"hash\":\"%s\",\"status\":\"%s\"}",
                        msg.getMessageId(), msg.getRecipient(), msg.getMessageContent(), msg.getMessageHash(), 
                        msg.getStatus()); // Corrected line 491
                writer.write(jsonLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving stored messages: " + e.getMessage());
        }
    }

    private static String extractJsonValue(String jsonLine, String key) {
        String search = "\"" + key + "\":\"";
        int startIndex = jsonLine.indexOf(search);
        if (startIndex == -1) return null;
        startIndex += search.length();
        int endIndex = jsonLine.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        return jsonLine.substring(startIndex, endIndex);
    }
    
    // --- Test Reset Methods ---
    public static void resetMessageListsForTesting() {
        sentMessages.clear();
        storedMessages.clear();
        try {
            Files.deleteIfExists(Paths.get(STORED_MESSAGES_FILE));
        } catch (IOException e) {
            System.err.println("Failed to delete stored messages file during test reset: " + e.getMessage());
        }
    }
}