package com.wizzdi.messaging.websocket.messages;

import com.wizzdi.messaging.model.Message;

import java.util.Collections;
import java.util.Set;

public class NewMessage implements IWSMessage{

    private Message newMessage;

    public Message getNewMessage() {
        return newMessage;
    }

    public <T extends NewMessage> T setNewMessage(Message newMessage) {
        this.newMessage = newMessage;
        return (T) this;
    }

    @Override
    public Set<String> getTarget() {
        return Collections.singleton(newMessage.getToUser().getId());
    }
}
