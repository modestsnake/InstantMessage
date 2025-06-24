/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.2.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.instantmessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID; // For unique message IDs

public class Message {
    private String messageId; // Unique ID for each message
    private String recipient;
    private String messageContent;
    private String messageHash; // MD5 hash of the message content
    private String status; // "Sent", "Disregarded", "Stored"

    // Constructor for creating a new message (used by SecureLoginApp for new inputs)
    public Message(int dummyId, String recipient, String messageContent) {
        this.messageId = UUID.randomUUID().toString(); // Generate unique ID
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = generateMD5Hash(messageContent);
        this.status = "Pending"; // Default status before actioned
    }

    // Constructor for loading messages from file (used by loadStoredMessagesFromFile)
    public Message(String messageId, String recipient, String messageContent, String messageHash, String status) {
        this.messageId = messageId;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = messageHash;
        this.status = status;
    }

    // Getters
    public String getMessageId() {
        return messageId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getStatus() { // <-- THIS IS THE MISSING METHOD
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Method to handle message action choice
    public void handleMessageChoice(int choice) {
        switch (choice) {
            case 1:
                this.status = "Sent";
                break;
            case 2:
                this.status = "Disregarded";
                break;
            case 3:
                this.status = "Stored";
                break;
            default:
                this.status = "Invalid Choice"; // Or handle error
                break;
        }
    }

    // Helper method to generate MD5 hash
    private String generateMD5Hash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageBytes = content.getBytes();
            md.update(messageBytes);
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Or throw an exception
        }
    }

    @Override
    public String toString() {
        return "Message ID: " + messageId +
               "\nRecipient: " + recipient +
               "\nContent: " + messageContent +
               "\nHash: " + messageHash +
               "\nStatus: " + status;
    }
}