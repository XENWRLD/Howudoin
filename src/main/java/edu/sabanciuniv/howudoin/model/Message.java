package edu.sabanciuniv.howudoin.model;

import com.mongodb.lang.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead; // Indicates if the message has been read
    private String type;

    @Nullable
    private String groupId;
    // Optional field for group messages

    // Default constructor
    public Message() {}

    // Constructor for direct messages with type and isRead
    public Message(String sender, String receiver, String content, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.isRead = false;
    }

    // Constructor for group messages
    public Message(String sender, String groupId, String content, boolean isGroupMessage) {
        this.sender = sender;
        this.groupId = groupId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for full initialization
    public Message(String id, String sender, String receiver, String content, LocalDateTime timestamp, String groupId) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        this.groupId = groupId;
    }


    // Getters and setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    // Check if the message is part of a group
    public boolean isGroupMessage() {
        return groupId != null && !groupId.isEmpty();
    }

    // Format timestamp as a readable string
    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.toString() : "No timestamp available";
    }

    // Create a utility method to summarize the message
    public String getMessageSummary() {
        return String.format("From: %s, To: %s, Content: %s", sender, receiver, content);
    }

}
