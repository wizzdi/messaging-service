package com.wizzdi.messaging.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNewService;
import com.wizzdi.messaging.data.MessageRepository;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@PluginInfo(version = 1)
@Component
public class MessageService implements ServicePlugin {

    @Autowired
    private BaseclassNewService baseclassNewService;

    @Autowired
    @PluginInfo(version = 1)
    private MessageRepository messageRepository;


    public Message createMessage(MessageCreate messageCreate, SecurityContext securityContext) {
        Message message=createMessageNoMerge(messageCreate,securityContext);
        messageRepository.merge(message);
        return message;
    }

    public boolean updateMessageNoMerge(MessageCreate messageCreate,Message message){
        boolean update=baseclassNewService.updateBaseclassNoMerge(messageCreate,message);
        if(messageCreate.getContent()!=null&&!messageCreate.getContent().equals(message.getContent())){
            message.setContent(messageCreate.getContent());
            update=true;
        }
        if(messageCreate.getSubject()!=null&&!messageCreate.getSubject().equals(message.getSubject())){
            message.setSubject(messageCreate.getSubject());
            update=true;
        }
        if(messageCreate.getFromUser()!=null&&(message.getFromUser()==null||!messageCreate.getFromUser().getId().equals(message.getFromUser().getId()))){
            message.setFromUser(messageCreate.getFromUser());
            update=true;
        }

        if(messageCreate.getToUser()!=null&&(message.getToUser()==null||!messageCreate.getToUser().getId().equals(message.getToUser().getId()))){
            message.setToUser(messageCreate.getToUser());
            update=true;
        }

        return update;
    }

    private Message createMessageNoMerge(MessageCreate messageCreate, SecurityContext securityContext) {
        Message message=new Message(messageCreate.getName(),securityContext);
        updateMessageNoMerge(messageCreate,message);
        return message;
    }

    public void validateCreate(MessageCreate messageCreate,SecurityContext securityContext){
        validate(messageCreate,securityContext);
        if(messageCreate.getToUser()==null){
            throw new BadRequestException("No User with id "+messageCreate.getToUserId());

        }
    }

    public void validate(MessageCreate messageCreate, SecurityContext securityContext) {
        String fromUserId=messageCreate.getFromUserId();
        User fromUser=fromUserId!=null?messageRepository.getByIdOrNull(fromUserId,User.class,null,securityContext):securityContext.getUser();
        messageCreate.setFromUser(fromUser);

        String toUserId=messageCreate.getToUserId();
        User toUser=toUserId!=null?messageRepository.getByIdOrNull(toUserId,User.class,null,securityContext):null;
        if(toUserId!=null&&toUser==null){
            throw new BadRequestException("No User with id "+toUserId);
        }
        messageCreate.setToUser(toUser);

    }

    public void validate(MessageFilter filtering, SecurityContext securityContext) {
        baseclassNewService.validateFilter(filtering,securityContext);
        Set<String> fromUserIds=filtering.getFromUsersIds();
        Map<String, User> fromUsers=fromUserIds.isEmpty()?new HashMap<>():messageRepository.listByIds(User.class,fromUserIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        fromUserIds.removeAll(fromUsers.keySet());
        if(!fromUserIds.isEmpty()){
            throw new BadRequestException("No Users with ids "+fromUserIds);
        }
        filtering.setFromUsers(new ArrayList<>(fromUsers.values()));

        Set<String> toUsersIds=filtering.getToUsersIds();
        Map<String, User> toUsers=toUsersIds.isEmpty()?new HashMap<>():messageRepository.listByIds(User.class,toUsersIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        toUsersIds.removeAll(toUsers.keySet());
        if(!toUsersIds.isEmpty()){
            throw new BadRequestException("No Users with ids "+toUsersIds);
        }
        filtering.setToUsers(new ArrayList<>(toUsers.values()));


    }

    public PaginationResponse<Message> getAllMessages(MessageFilter filtering, SecurityContext securityContext) {
        List<Message> list=listAllMessages(filtering,securityContext);
        long count=messageRepository.countAllMessages(filtering,securityContext);
        return new PaginationResponse<>(list,filtering,count);
    }

    private List<Message> listAllMessages(MessageFilter filtering, SecurityContext securityContext) {
        return messageRepository.listAllMessages(filtering,securityContext);
    }

    public Message updateMessage(MessageUpdate messageUpdate, SecurityContext securityContext) {
        Message message=messageUpdate.getMessage();
        if(updateMessageNoMerge(messageUpdate,message)){
            messageRepository.merge(message);
        }
        return message;
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return messageRepository.getByIdOrNull(id, c, batchString, securityContext);
    }
}
