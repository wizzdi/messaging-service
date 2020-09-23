package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;

import com.flexicore.security.SecurityContext;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import com.wizzdi.messaging.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.stream.Collectors;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/message")
@Tag(name = "message")
@Extension
@Component
public class MessageRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private MessageService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllMessages", description = "Gets All Messages Filtered")
	@Path("getAllMessages")
	public PaginationResponse<Message> getAllMessages(
			@HeaderParam("authenticationKey") String authenticationKey,
			MessageFilter filtering,
			@Context SecurityContext securityContext) {
		service.validate(filtering, securityContext);
		return service.getAllMessages(filtering, securityContext);


	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createMessage", description = "creates Message")
	@Path("createMessage")
	public Message createMessage(
			@HeaderParam("authenticationKey") String authenticationKey,
			MessageCreate messageCreate,
			@Context SecurityContext securityContext) {

		service.validateCreate(messageCreate, securityContext);
		return service.createMessage(messageCreate, securityContext);
	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateMessage", description = "Updates Message")
	@Path("updateMessage")
	public Message updateMessage(
			@HeaderParam("authenticationKey") String authenticationKey,
			MessageUpdate messageUpdate,
			@Context SecurityContext securityContext) {
		Message message = messageUpdate.getId() != null ? service.getByIdOrNull(messageUpdate.getId(), Message.class, null, securityContext) : null;
		if (message == null) {
			throw new BadRequestException("No MessageUpdate with id " + messageUpdate.getId());
		}
		messageUpdate.setMessage(message);
		service.validate(messageUpdate, securityContext);
		return service.updateMessage(messageUpdate, securityContext);
	}

}
