package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.ChatRepository;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatCreate;
import com.wizzdi.messaging.request.ChatFilter;
import com.wizzdi.messaging.request.ChatUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Extension
@Component
public class ChatService implements Plugin {



	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private BasicService basicService;


	public Chat createChat(ChatCreate chatCreate, SecurityContextBase securityContext) {
		Chat chat = createChatNoMerge(chatCreate, securityContext);
		Baseclass baseclass=new Baseclass(chat.getName(),securityContext);
		chat.setSecurity(baseclass);
		chatRepository.massMerge(Arrays.asList(chat,baseclass));
		return chat;
	}

	public void merge(Object o) {
		chatRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		chatRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return chatRepository.listByIds(c, ids, securityContext);
	}

	public Chat createChatNoMerge(ChatCreate chatCreate, SecurityContextBase securityContext) {
		Chat chat = new Chat();
		chat.setId(Baseclass.getBase64ID());
		updateChatNoMerge(chatCreate, chat);
		return chat;
	}

	public boolean updateChatNoMerge(ChatCreate chatCreate, Chat chat) {
		boolean update = basicService.updateBasicNoMerge(chatCreate, chat);
		if(chatCreate.getOwner()!=null&&(chat.getOwner()==null||!chatCreate.getOwner().getId().equals(chat.getOwner().getId()))){
			chat.setOwner(chatCreate.getOwner());
			update=true;
		}
		return update;
	}

	public Chat updateChat(ChatUpdate chatUpdate, SecurityContextBase securityContext) {
		Chat Chat = chatUpdate.getChat();
		if (updateChatNoMerge(chatUpdate, Chat)) {
			chatRepository.merge(Chat);
		}
		return Chat;
	}

	public void validate(ChatCreate chatCreate, SecurityContextBase securityContext) {
		basicService.validate(chatCreate,securityContext);
		String ownerId=chatCreate.getOwnerId();
		ChatUser owner=ownerId!=null?getByIdOrNull(ownerId,ChatUser.class,securityContext):null;
		if(ownerId!=null&&owner==null){
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no owner with id "+ownerId);
		}
		chatCreate.setOwner(owner);
		if(chatCreate.getOwner()==null&&securityContext.getUser() instanceof ChatUser){
			chatCreate.setOwner((ChatUser) securityContext.getUser());
		}
	}

	public void validate(ChatFilter chatFilter, SecurityContextBase securityContext) {
		basicService.validate(chatFilter, securityContext);


	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatRepository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Chat> T getChatByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatRepository.getChatByIdOrNull(id, c, securityContext);
	}

	public PaginationResponse<Chat> getAllChats(ChatFilter ChatFilter, SecurityContextBase securityContext) {
		List<Chat> list = listAllChats(ChatFilter, securityContext);
		long count = chatRepository.countAllChats(ChatFilter, securityContext);
		return new PaginationResponse<>(list, ChatFilter, count);
	}

	public List<Chat> listAllChats(ChatFilter ChatFilter, SecurityContextBase securityContext) {
		return chatRepository.listAllChats(ChatFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return chatRepository.findByIds(c, requested);
	}

}
