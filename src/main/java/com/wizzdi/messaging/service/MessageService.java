package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.MessageRepository;
import com.wizzdi.messaging.events.NewMessageEvent;
import com.wizzdi.messaging.events.UpdatedMessageEvent;
import com.wizzdi.messaging.interfaces.ChatUserProvider;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component
public class MessageService implements Plugin {



	@Autowired
	private MessageRepository messageRepository;
	@Autowired
	private BasicService basicService;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	private ChatUserService chatUserService;

	public Message createMessage(MessageCreate messageCreate, SecurityContextBase securityContext) {
		Message message = createMessageNoMerge(messageCreate, securityContext);
		messageRepository.merge(message);
		applicationEventPublisher.publishEvent(new NewMessageEvent(message,securityContext));
		return message;
	}

	public void merge(Object o) {
		messageRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		messageRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return messageRepository.listByIds(c, ids, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return messageRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public Message createMessageNoMerge(MessageCreate messageCreate, SecurityContextBase securityContext) {
		Message message = new Message();
		message.setId(Baseclass.getBase64ID());
		updateMessageNoMerge(messageCreate, message);
		return message;
	}

	public boolean updateMessageNoMerge(MessageCreate messageCreate, Message message) {
		boolean update = basicService.updateBasicNoMerge(messageCreate, message);
		if(messageCreate.getChat()!=null&&(message.getChat()==null||!messageCreate.getChat().getId().equals(message.getChat().getId()))){
			message.setChat(messageCreate.getChat());
			update=true;
		}
		if(messageCreate.getSender()!=null&&(message.getSender()==null||!messageCreate.getSender().getId().equals(message.getSender().getId()))){
			message.setSender(messageCreate.getSender());
			update=true;
		}
		if(messageCreate.getContent()!=null&&!messageCreate.getContent().equals(message.getContent())){
			Map<String, Object> copy=new HashMap<>(message.getOther());
			message.setOther(copy);
			message.setContent(messageCreate.getContent());
			update=true;
		}
		if(messageCreate.getMedia()!=null&&!messageCreate.getMedia().equals(message.getMedia())){
			Map<String, Object> copy=new HashMap<>(message.getOther());
			message.setOther(copy);
			message.setMedia(messageCreate.getMedia());
			update=true;
		}
		if(messageCreate.getChatUsers()!=null&&!messageCreate.getChatUsers().equals(message.getChatUsers())){
			Map<String, Object> copy=new HashMap<>(message.getChatUsers());
			message.setOther(copy);
			message.setChatUsers(messageCreate.getChatUsers());
			update=true;
		}

		if (messageCreate.getOther() != null && !messageCreate.getOther().isEmpty()) {
			Map<String, Object> jsonNode = message.getOther();
			if (jsonNode == null) {
				message.setOther(messageCreate.getOther());
				update = true;
			} else {
				Map<String, Object> copy=new HashMap<>(messageCreate.getOther());
				for (Map.Entry<String, Object> entry : messageCreate.getOther().entrySet()) {
					String key = entry.getKey();
					Object newVal = entry.getValue();
					Object val = jsonNode.get(key);
					if (newVal!=null&&!newVal.equals(val)) {
						copy.put(key, newVal);
						update = true;
					}
				}
				if(update){
					message.setOther(copy);
				}
			}


		}
		return update;
	}

	public Message updateMessage(MessageUpdate messageUpdate, SecurityContextBase securityContext) {
		Message message = messageUpdate.getMessage();
		if (updateMessageNoMerge(messageUpdate, message)) {
			messageRepository.merge(message);
			applicationEventPublisher.publishEvent(new UpdatedMessageEvent(message));

		}
		return message;
	}

	public void validate(MessageCreate messageCreate, SecurityContextBase securityContext) {
		basicService.validate(messageCreate,securityContext);
		if(messageCreate.getSender()==null){
			ChatUser chatUser = chatUserService.getChatUser(securityContext);
			if(chatUser==null){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"must be a chat user");
			}
			messageCreate.setSender(chatUser);

		}
		String chatId=messageCreate.getChatId();
		Chat chat=chatId!=null?getByIdOrNull(chatId,Chat.class, Chat_.security,securityContext):null;
		if(chatId!=null&&chat==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no chat with id "+chatId);
		}
		messageCreate.setChat(chat);

	}



	public void validate(MessageFilter messageFilter, SecurityContextBase securityContext) {
		basicService.validate(messageFilter, securityContext);
		Set<String> chatIds=messageFilter.getChatsIds();
		Map<String,Chat> chatMap=chatIds.isEmpty()?new HashMap<>():messageRepository.listByIds(Chat.class,chatIds,Chat_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		chatIds.removeAll(chatMap.keySet());
		if(!chatIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chats with ids " + chatIds);
		}
		messageFilter.setChats(new ArrayList<>(chatMap.values()));

		Set<String> senderIds=messageFilter.getSenderIds();
		Map<String,ChatUser> chatUserMap=senderIds.isEmpty()?new HashMap<>():listByIds(ChatUser.class,senderIds, ChatUser_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(), f->f));
		senderIds.removeAll(chatUserMap.keySet());
		if(!senderIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat users with ids " + senderIds);
		}
		messageFilter.setSenders(new ArrayList<>(chatUserMap.values()));
		if(messageFilter.getSenders().isEmpty()&&messageFilter.getChats().isEmpty()){
			throw new BadRequestException("must specify at least one chat or chat user");
		}



	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return messageRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return messageRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return messageRepository.findByIdOrNull(type, id);
	}

	public PaginationResponse<Message> getAllMessages(MessageFilter MessageFilter, SecurityContextBase securityContext) {
		List<Message> list = listAllMessages(MessageFilter, securityContext);
		long count = messageRepository.countAllMessages(MessageFilter, securityContext);
		return new PaginationResponse<>(list, MessageFilter, count);
	}

	public List<Message> listAllMessages(MessageFilter MessageFilter, SecurityContextBase securityContext) {
		return messageRepository.listAllMessages(MessageFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return messageRepository.findByIds(c, requested);
	}

}
