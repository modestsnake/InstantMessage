package com.mycompany.instantmessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    void testMessageCreationConstructor1() {
        Message msg = new Message(0, "recipient@example.com", "Hello, this is a test message.");
        
        assertNotNull(msg.getMessageId());
        assertFalse(msg.getMessageId().isEmpty());
        assertTrue(msg.getMessageId().length() > 20); // UUIDs are typically long
        assertEquals("recipient@example.com", msg.getRecipient());
        assertEquals("Hello, this is a test message.", msg.getMessageContent());
        assertNotNull(msg.getMessageHash());
        assertFalse(msg.getMessageHash().isEmpty());
        assertEquals("Pending", msg.getStatus()); // Default status for new message
    }

    @Test
    void testMessageCreationConstructor2ForLoading() {
        String testId = "custom-msg-id-123";
        String testRecipient = "loadtest@example.com";
        String testContent = "This message was loaded from file.";
        String testHash = "dummyhash123abc";
        String testStatus = "Loaded";

        Message loadedMsg = new Message(testId, testRecipient, testContent, testHash, testStatus);

        assertEquals(testId, loadedMsg.getMessageId());
        assertEquals(testRecipient, loadedMsg.getRecipient());
        assertEquals(testContent, loadedMsg.getMessageContent());
        assertEquals(testHash, loadedMsg.getMessageHash());
        assertEquals(testStatus, loadedMsg.getStatus());
    }

    @Test
    void testMessageStatusChangeHandleMessageChoice() {
        Message msg = new Message(0, "test@example.com", "Test status change.");
        
        msg.handleMessageChoice(1); // Send
        assertEquals("Sent", msg.getStatus());

        msg.handleMessageChoice(2); // Disregard
        assertEquals("Disregarded", msg.getStatus());

        msg.handleMessageChoice(3); // Store
        assertEquals("Stored", msg.getStatus());

        msg.handleMessageChoice(99); // Invalid choice
        assertEquals("Invalid Choice", msg.getStatus()); // Based on current implementation
    }

    @Test
    void testSetStatus() {
        Message msg = new Message(0, "user", "content");
        msg.setStatus("NewStatus");
        assertEquals("NewStatus", msg.getStatus());
    }

    @Test
    void testGenerateMD5HashConsistency() {
        Message msg1 = new Message(0, "user1", "Same content here.");
        Message msg2 = new Message(0, "user2", "Same content here.");
        
        assertEquals(msg1.getMessageHash(), msg2.getMessageHash());

        Message msg3 = new Message(0, "user3", "Different content here.");
        assertNotEquals(msg1.getMessageHash(), msg3.getMessageHash());
    }

    @Test
    void testToStringMethod() {
        Message msg = new Message(0, "recipient", "Test content for toString.");
        String expectedToStringPrefix = "Message ID: " + msg.getMessageId() +
                                        "\nRecipient: recipient" +
                                        "\nContent: Test content for toString." +
                                        "\nHash: " + msg.getMessageHash() +
                                        "\nStatus: Pending";
        assertTrue(msg.toString().startsWith("Message ID: "));
        assertTrue(msg.toString().contains("Recipient: recipient"));
        assertTrue(msg.toString().contains("Content: Test content for toString."));
        assertTrue(msg.toString().contains("Status: Pending"));
    }
}