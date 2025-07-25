package com.properyservice.dto;

import java.io.Serializable;

public class EmailRequest implements Serializable {

    private String to;
    private String subject;
    private String body;

    // Default no-args constructor - IMPORTANT for Jackson
    public EmailRequest() {
    }

    // All args constructor (optional, but useful)
    public EmailRequest(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    // Getters and setters (important!)
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
}
