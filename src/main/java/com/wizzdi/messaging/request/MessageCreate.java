package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.User;
import com.flexicore.request.BaseclassCreate;

public class MessageCreate extends BaseclassCreate {
    private String subject;
    private String content;
    private String fromUserId;
    @JsonIgnore
    private User fromUser;
    private String toUserId;
    @JsonIgnore
    private User toUser;

    public String getSubject() {
        return subject;
    }

    public <T extends MessageCreate> T setSubject(String subject) {
        this.subject = subject;
        return (T) this;
    }

    public String getContent() {
        return content;
    }

    public <T extends MessageCreate> T setContent(String content) {
        this.content = content;
        return (T) this;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public <T extends MessageCreate> T setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
        return (T) this;
    }

    @JsonIgnore
    public User getFromUser() {
        return fromUser;
    }

    public <T extends MessageCreate> T setFromUser(User fromUser) {
        this.fromUser = fromUser;
        return (T) this;
    }

    public String getToUserId() {
        return toUserId;
    }

    public <T extends MessageCreate> T setToUserId(String toUserId) {
        this.toUserId = toUserId;
        return (T) this;
    }

    @JsonIgnore
    public User getToUser() {
        return toUser;
    }

    public <T extends MessageCreate> T setToUser(User toUser) {
        this.toUser = toUser;
        return (T) this;
    }
}
