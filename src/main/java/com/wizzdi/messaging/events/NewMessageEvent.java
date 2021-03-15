package com.wizzdi.messaging.events;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.messaging.model.Message;

public class NewMessageEvent {

	private final Message message;
	private final SecurityContextBase securityContextBase;

	public NewMessageEvent(Message message,SecurityContextBase securityContextBase) {
		this.message = message;
		this.securityContextBase=securityContextBase;
	}

	public Message getMessage() {
		return message;
	}

	public SecurityContextBase getSecurityContextBase() {
		return securityContextBase;
	}
}
