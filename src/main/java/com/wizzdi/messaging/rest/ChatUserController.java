package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.ChatUserFilter;
import com.wizzdi.messaging.request.ChatUserUpdate;
import com.wizzdi.messaging.service.ChatUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chatUser")
@Extension
@Tag(name = "chatUser")
@OperationsInside
public class ChatUserController implements Plugin {

	@Autowired
	private ChatUserService chatUserService;

	@PostMapping("/createChatUser")
	@Operation(description = "creates ChatUser",summary = "creates ChatUser")
	public ChatUser createChatUser(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody ChatUserCreate chatUserCreate, @RequestAttribute SecurityContextBase securityContext){
		chatUserService.validate(chatUserCreate,securityContext);
		return chatUserService.createChatUser(chatUserCreate,securityContext);
	}

	@PostMapping("/getAllChatUsers")
	@Operation(description = "returns ChatUsers",summary = "returns ChatUsers")

	public PaginationResponse<ChatUser> getAllChatUsers(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody ChatUserFilter chatUserFilter, @RequestAttribute SecurityContextBase securityContext){
		chatUserService.validate(chatUserFilter,securityContext);
		return chatUserService.getAllChatUsers(chatUserFilter,securityContext);
	}

	@PutMapping("/updateChatUser")
	@Operation(description = "updates ChatUser",summary = "updates ChatUser")

	public ChatUser updateChatUser(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody ChatUserUpdate chatUserUpdate, @RequestAttribute SecurityContextBase securityContext){
		String id=chatUserUpdate.getId();
		ChatUser chatUser=id!=null? chatUserService.getChatUserByIdOrNull(id,ChatUser.class,securityContext):null;
		if(chatUser==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no ChatUser user with id "+id);
		}
		chatUserUpdate.setChatUser(chatUser);
		chatUserService.validate(chatUserUpdate,securityContext);
		return chatUserService.updateChatUser(chatUserUpdate,securityContext);
	}
}
