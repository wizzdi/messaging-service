package com.wizzdi.messaging.events;

import com.wizzdi.messaging.model.Message;

public class NewMessageEvent {

	private final Message message;

	public NewMessageEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
