package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;

public class MessageCreate extends BasicCreate {

	private String chatId;
	@JsonIgnore
	private Chat chat;
	@JsonIgnore
	private ChatUser sender;

	public String getChatId() {
		return chatId;
	}

	public <T extends MessageCreate> T setChatId(String chatId) {
		this.chatId = chatId;
		return (T) this;
	}

	@JsonIgnore
	public Chat getChat() {
		return chat;
	}

	public <T extends MessageCreate> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getSender() {
		return sender;
	}

	public <T extends MessageCreate> T setSender(ChatUser sender) {
		this.sender = sender;
		return (T) this;
	}
}
