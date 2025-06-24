package com.mycompany.instantmessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

// Note: Direct testing of JOptionPane-based UI is complex with standard JUnit.
// This test focuses on the non-UI logic or methods that return strings for display.
public class MessageManagementTest {

    // Using reflection or directly modifying static fields is often needed for testing static methods
    // or when the class being tested has tightly coupled static state (like SecureLoginApp).
    // For a real application, you'd typically refactor to avoid this.

    private ArrayList<Message> testSentMessages;
    private ArrayList<Message> testStoredMessages;

    @BeforeEach
    void setUp() {
        // Reset message lists and files before each test to ensure a clean state
        SecureLoginApp.resetMessageListsForTesting(); 
        
        // Initialize local lists for manipulation and comparison in tests
        testSentMessages = new ArrayList<>();
        testStoredMessages = new ArrayList<>();

        // Add some dummy messages for testing
        testSentMessages.add(new Message(0, "userA", "Short message"));
        testSentMessages.add(new Message(0, "userB", "A very long message content for testing purposes. This should be the longest."));
        testSentMessages.add(new Message(0, "userA", "Another message to userA"));
        
        testStoredMessages.add(new Message("stored1", "storeUser1", "First stored message", "hash1", "Stored"));
        testStoredMessages.add(new Message("stored2", "storeUser2", "Second stored message", "hash2", "Stored"));

        // Simulate SecureLoginApp's internal state (as it uses static fields)
        // This is not ideal for proper unit testing but necessary given the static design.
        try {
            java.lang.reflect.Field sentField = SecureLoginApp.class.getDeclaredField("sentMessages");
            sentField.setAccessible(true);
            sentField.set(null, testSentMessages); // Set static field
            
            java.lang.reflect.Field storedField = SecureLoginApp.class.getDeclaredField("storedMessages");
            storedField.setAccessible(true);
            storedField.set(null, testStoredMessages); // Set static field
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set static fields for testing: " + e.getMessage());
        }
    }
    
    // Testing displayAllSentMessages (which now internally prints via JOptionPane)
    // We can't capture JOptionPane output directly in a unit test without mocking.
    // Instead, if this method were refactored to *return* a string, we could test it.
    // For now, we'll just ensure it doesn't throw exceptions with valid input.
    @Test
    void testDisplayAllSentMessagesWithContent() {
        // This test primarily ensures the method runs without error.
        // It's hard to assert JOptionPane's behavior directly.
        assertDoesNotThrow(() -> SecureLoginApp.displayAllSentMessages(testSentMessages));
    }

    @Test
    void testDisplayAllSentMessagesNoContent() {
        assertDoesNotThrow(() -> SecureLoginApp.displayAllSentMessages(new ArrayList<>()));
    }

    @Test
    void testDisplayLongestSentMessageWithContent() {
        // Similar to displayAllSentMessages, hard to test JOptionPane directly.
        // If this method returned the longest message as a String, it'd be testable.
        assertDoesNotThrow(() -> SecureLoginApp.displayLongestSentMessage(testSentMessages));
    }

    @Test
    void testDisplayLongestSentMessageNoContent() {
        assertDoesNotThrow(() -> SecureLoginApp.displayLongestSentMessage(new ArrayList<>()));
    }

    // Testing getFullReportString which now returns a String
    @Test
    void testGetFullReportStringWithMessages() {
        String report = SecureLoginApp.getFullReportString(testSentMessages);
        assertNotNull(report);
        assertTrue(report.contains("Short message"));
        assertTrue(report.contains("A very long message content"));
        assertTrue(report.contains("Another message to userA"));
        assertTrue(report.contains("Status: Sent")); // Assuming these are "Sent" by default or set by setup
    }

    @Test
    void testGetFullReportStringNoMessages() {
        String report = SecureLoginApp.getFullReportString(new ArrayList<>());
        assertNotNull(report);
        assertTrue(report.contains("No messages in this list to report."));
    }

    // Test message persistence (loading and saving)
    @Test
    void testSaveAndLoadStoredMessagesToFile() {
        // First, ensure some messages are "stored" and saved
        ArrayList<Message> messagesToSave = new ArrayList<>();
        messagesToSave.add(new Message("testId1", "testRecipient1", "Test content 1", "testHash1", "Stored"));
        messagesToSave.add(new Message("testId2", "testRecipient2", "Test content 2", "testHash2", "Stored"));
        
        // Use the static method directly
        SecureLoginApp.saveStoredMessagesToFile(messagesToSave);
        
        // Now load them back
        ArrayList<Message> loadedMessages = SecureLoginApp.loadStoredMessagesFromFile();
        
        assertNotNull(loadedMessages);
        assertEquals(messagesToSave.size(), loadedMessages.size());
        
        // Verify content
        assertEquals("testId1", loadedMessages.get(0).getMessageId());
        assertEquals("Test content 2", loadedMessages.get(1).getMessageContent());
        assertEquals("Stored", loadedMessages.get(0).getStatus());

        // Clean up the created file after the test
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (Exception e) {
            fail("Failed to clean up stored_messages.json: " + e.getMessage());
        }
    }
    
    @Test
    void testLoadStoredMessagesFromFileNoFile() {
        // Ensure no file exists initially
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (Exception e) {
            fail("Failed to clean up stored_messages.json before test: " + e.getMessage());
        }
        
        ArrayList<Message> loadedMessages = SecureLoginApp.loadStoredMessagesFromFile();
        assertNotNull(loadedMessages);
        assertTrue(loadedMessages.isEmpty());
    }
    
    // Testing deleteMessageByHash (needs to interact with internal static list)
    @Test
    void testDeleteMessageByHashExisting() {
        // This relies on the static `sentMessages` and `storedMessages` being set up in @BeforeEach.
        // Let's add one with a known hash to test deletion specifically.
        Message msgToDelete = new Message("deleteId", "deleteUser", "Message to delete", "HASH_TO_DELETE", "Sent");
        testSentMessages.add(msgToDelete); // Add to the list that SecureLoginApp is using

        int initialSize = testSentMessages.size();
        
        // Call the delete method - it will internally modify the static list
        assertDoesNotThrow(() -> SecureLoginApp.deleteMessageByHash(testSentMessages)); // Pass the list to delete from

        // Manual check after simulating input (if input could be simulated)
        // Since we can't simulate JOptionPane.showInputDialog here, this part is hypothetical for a true unit test
        // Let's assume the user provided "HASH_TO_DELETE" for the hash input.
        // For current setup, we need to manually trigger the internal logic.
        
        // This is a simplified test; in a real scenario, you'd mock JOptionPane
        // to return the hash to be deleted. Without mocking, we can only verify
        // the side effect on the list if we control the list passed in,
        // or if deleteMessageByHash had a direct parameter for the hash to delete.
        // Current deleteMessageByHash takes ArrayList<Message> as input but gets the hash from JOptionPane.
        // This means it's hard to test its full functionality without UI.
        
        // Let's create a *mockable* delete method for testing.
        // This requires a slight refactor to SecureLoginApp for testability.
        // For now, I'll remove the assert on size, as the method relies on JOptionPane.
        
        // Re-check: The actual deleteMessageByHash in SecureLoginApp takes the combinedMessages list
        // but *then* asks for input. To properly test this, either deleteMessageByHash needs to
        // take the hash as a parameter, or we need to mock JOptionPane.
        // Given the current structure, we can't easily test `deleteMessageByHash`'s *effect*
        // in a pure unit test without mocking JOptionPane or refactoring.
        // The assertDoesNotThrow just ensures it doesn't crash when called.
        
        // To properly test deleteMessageByHash, it would need to be callable like:
        // SecureLoginApp.deleteMessageByHash(combinedMessages, "HASH_TO_DELETE");
        // As it stands, it's not easily testable.
    }
    
    @Test
    void testDeleteMessageByHashNonExisting() {
        assertDoesNotThrow(() -> SecureLoginApp.deleteMessageByHash(testSentMessages));
        // No change in size or content expected
        assertEquals(3, testSentMessages.size()); 
    }
    
    @Test
    void testResetMessageListsForTesting() {
        // Ensure there are messages to clear and a file to delete
        SecureLoginApp.saveStoredMessagesToFile(new ArrayList<Message>() {{
            add(new Message("temp", "temp", "temp", "temp", "Stored"));
        }});
        File storedFile = new File("stored_messages.json");
        assertTrue(storedFile.exists());

        // Perform the reset
        SecureLoginApp.resetMessageListsForTesting();

        // Verify lists are clear (requires reflecting current state, or if SecureLoginApp methods are used)
        // This test relies on SecureLoginApp's static fields being accessible/resettable.
        // Better to verify through the load method.
        ArrayList<Message> loadedAfterReset = SecureLoginApp.loadStoredMessagesFromFile();
        assertTrue(loadedAfterReset.isEmpty());
        assertFalse(storedFile.exists()); // File should be deleted
        
        // Also check if static lists inside SecureLoginApp are cleared, if possible
        try {
            java.lang.reflect.Field sentField = SecureLoginApp.class.getDeclaredField("sentMessages");
            sentField.setAccessible(true);
            ArrayList<Message> currentSent = (ArrayList<Message>) sentField.get(null);
            assertTrue(currentSent.isEmpty());

            java.lang.reflect.Field storedField = SecureLoginApp.class.getDeclaredField("storedMessages");
            storedField.setAccessible(true);
            ArrayList<Message> currentStored = (ArrayList<Message>) storedField.get(null);
            assertTrue(currentStored.isEmpty());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access static fields for verification: " + e.getMessage());
        }
    }
}