package com.example.chatapp;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    private String messageId;
    private String sender;
    private String receiver;
    private String message;
    private long timestamp;
    private boolean isDeletedForEveryone = false;
    private List<String> deletedFor = new ArrayList<>();

    public ChatMessage() {
        // Required for Firebase
    }

    public ChatMessage(String messageId, String sender, String receiver, String message, long timestamp) {
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isDeletedForEveryone() {
        return isDeletedForEveryone;
    }

    public void setDeletedForEveryone(boolean deletedForEveryone) {
        isDeletedForEveryone = deletedForEveryone;
    }

    public List<String> getDeletedFor() {
        return deletedFor;
    }

    public void setDeletedFor(List<String> deletedFor) {
        this.deletedFor = deletedFor;
    }
}