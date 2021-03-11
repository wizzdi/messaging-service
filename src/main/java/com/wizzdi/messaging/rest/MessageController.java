package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import com.wizzdi.messaging.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/message")
@Extension
@Tag(name = "message")
@OperationsInside
public class MessageController implements Plugin {

	@Autowired
	private MessageService messageService;

	@PostMapping("/createMessage")
	@Operation(description = "creates Message",summary = "creates Message")
	public Message createMessage(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody MessageCreate messageCreate, @Parameter(hidden = true) SecurityContextBase securityContext){
		messageService.validate(messageCreate,securityContext);
		return messageService.createMessage(messageCreate,securityContext);
	}

	@PostMapping("/getAllMessages")
	@Operation(description = "returns Messages",summary = "returns Messages")

	public PaginationResponse<Message> getAllMessages(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody MessageFilter messageFilter, @Parameter(hidden = true) SecurityContextBase securityContext){
		messageService.validate(messageFilter,securityContext);
		return messageService.getAllMessages(messageFilter,securityContext);
	}

	@PutMapping("/updateMessage")
	@Operation(description = "updates Message",summary = "updates Message")

	public Message updateMessage(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody MessageUpdate messageUpdate, @Parameter(hidden = true) SecurityContextBase securityContext){
		String id=messageUpdate.getId();
		Message message=id!=null? messageService.findByIdOrNull(Message.class,id):null;
		if(message==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Message with id "+id);
		}
		if(!message.getSender().getId().equals(securityContext.getUser().getId())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"not allowed to change message "+id);

		}
		messageUpdate.setMessage(message);
		messageService.validate(messageUpdate,securityContext);
		return messageService.updateMessage(messageUpdate,securityContext);
	}
}
