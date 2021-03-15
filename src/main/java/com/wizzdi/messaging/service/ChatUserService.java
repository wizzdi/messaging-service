package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.flexicore.security.service.SecurityUserService;
import com.wizzdi.messaging.data.ChatUserRepository;
import com.wizzdi.messaging.interfaces.ChatUserProvider;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.ChatUserFilter;
import com.wizzdi.messaging.request.ChatUserUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class ChatUserService implements Plugin {



	@Autowired
	private ChatUserRepository chatUserRepository;
	@Autowired
	private BasicService basicService;
	@Autowired
	private ObjectProvider<ChatUserProvider<?>> chatUserProviders;

	public ChatUser getChatUser(SecurityContextBase securityContext) {
		return chatUserProviders.stream().filter(f -> f.getType().equals(securityContext.getUser().getClass())).findFirst().map(f -> f.getChatUser(securityContext)).orElse(null);
	}

	public ChatUser createChatUser(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		ChatUser chatUser = createChatUserNoMerge(chatUserCreate, securityContext);
		Baseclass baseclass=new Baseclass(chatUser.getName(),securityContext);
		chatUser.setSecurity(baseclass);
		chatUserRepository.massMerge(Arrays.asList(baseclass,chatUser));
		return chatUser;
	}

	public void merge(Object o) {
		chatUserRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		chatUserRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return chatUserRepository.listByIds(c, ids, securityContext);
	}

	public ChatUser createChatUserNoMerge(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		ChatUser chatUser = new ChatUser();
		chatUser.setId(Baseclass.getBase64ID());
		updateChatUserNoMerge(chatUserCreate, chatUser);
		return chatUser;
	}

	public boolean updateChatUserNoMerge(ChatUserCreate chatUserCreate, ChatUser chatUser) {
		return basicService.updateBasicNoMerge(chatUserCreate, chatUser);

	}

	public ChatUser updateChatUser(ChatUserUpdate chatUserUpdate, SecurityContextBase securityContext) {
		ChatUser ChatUser = chatUserUpdate.getChatUser();
		if (updateChatUserNoMerge(chatUserUpdate, ChatUser)) {
			chatUserRepository.merge(ChatUser);
		}
		return ChatUser;
	}

	public void validate(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		basicService.validate(chatUserCreate,securityContext);

	}

	public void validate(ChatUserFilter chatUserFilter, SecurityContextBase securityContext) {
		basicService.validate(chatUserFilter, securityContext);


	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatUserRepository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends ChatUser> T getChatUserByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatUserRepository.getChatUserByIdOrNull(id, c, securityContext);
	}

	public PaginationResponse<ChatUser> getAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
		List<ChatUser> list = listAllChatUsers(ChatUserFilter, securityContext);
		long count = chatUserRepository.countAllChatUsers(ChatUserFilter, securityContext);
		return new PaginationResponse<>(list, ChatUserFilter, count);
	}

	public List<ChatUser> listAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
		return chatUserRepository.listAllChatUsers(ChatUserFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return chatUserRepository.findByIds(c, requested);
	}

}
