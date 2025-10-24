package com.example.symptom.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String userId;
    private String userMessage;
    private String botReply;
    private long timestamp;

    public Message(String userId, String userMessage, String botReply) {
        this.userId = userId;
        this.userMessage = userMessage;
        this.botReply = botReply;
        this.timestamp = System.currentTimeMillis();
    }

    // getters and setters
}
