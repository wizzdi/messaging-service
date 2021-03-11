package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.request.SecurityUserCreate;
import com.wizzdi.messaging.model.ChatUser;

public class ChatUserCreate extends SecurityUserCreate {

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public <T extends ChatUserCreate> T setUsername(String username) {
		this.username = username;
		return (T) this;
	}

	public String getPassword() {
		return password;
	}

	public <T extends ChatUserCreate> T setPassword(String password) {
		this.password = password;
		return (T) this;
	}
}
