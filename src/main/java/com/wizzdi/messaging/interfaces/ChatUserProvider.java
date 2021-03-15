package com.wizzdi.messaging.interfaces;

import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.messaging.model.ChatUser;

public interface ChatUserProvider<T extends SecurityUser> {

	ChatUser getChatUser(SecurityContextBase<?,T,?,?> securityContextBase);
	Class<T> getType();

}
