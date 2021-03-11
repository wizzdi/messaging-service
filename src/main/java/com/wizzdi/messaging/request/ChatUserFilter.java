package com.wizzdi.messaging.request;

import com.wizzdi.flexicore.security.request.SecurityUserFilter;

public class ChatUserFilter extends SecurityUserFilter {

	private String userNameLike;


	public String getUserNameLike() {
		return userNameLike;
	}

	public <T extends ChatUserFilter> T setUserNameLike(String userNameLike) {
		this.userNameLike = userNameLike;
		return (T) this;
	}
}
