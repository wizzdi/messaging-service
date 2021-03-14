package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.MessageReceiverDeviceRepository;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.MessageReceiverDevice;
import com.wizzdi.messaging.model.MessageReceiverDevice;
import com.wizzdi.messaging.request.MessageReceiverDeviceCreate;
import com.wizzdi.messaging.request.MessageReceiverDeviceFilter;
import com.wizzdi.messaging.request.MessageReceiverDeviceUpdate;
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
public class MessageReceiverDeviceService implements Plugin {



	@Autowired
	private MessageReceiverDeviceRepository messageReceiverDeviceRepository;
	@Autowired
	private BasicService basicService;


	public MessageReceiverDevice createMessageReceiverDevice(MessageReceiverDeviceCreate messageReceiverDeviceCreate, SecurityContextBase securityContext) {
		MessageReceiverDevice messageReceiverDevice = createMessageReceiverDeviceNoMerge(messageReceiverDeviceCreate, securityContext);
		messageReceiverDeviceRepository.merge(messageReceiverDevice);
		return messageReceiverDevice;
	}

	public void merge(Object o) {
		messageReceiverDeviceRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		messageReceiverDeviceRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return messageReceiverDeviceRepository.listByIds(c, ids, securityContext);
	}

	public MessageReceiverDevice createMessageReceiverDeviceNoMerge(MessageReceiverDeviceCreate messageReceiverDeviceCreate, SecurityContextBase securityContext) {
		MessageReceiverDevice messageReceiverDevice = new MessageReceiverDevice();
		messageReceiverDevice.setId(Baseclass.getBase64ID());
		updateMessageReceiverDeviceNoMerge(messageReceiverDeviceCreate, messageReceiverDevice);
		return messageReceiverDevice;
	}

	public boolean updateMessageReceiverDeviceNoMerge(MessageReceiverDeviceCreate messageReceiverDeviceCreate, MessageReceiverDevice messageReceiverDevice) {
		boolean update = basicService.updateBasicNoMerge(messageReceiverDeviceCreate, messageReceiverDevice);
		if(messageReceiverDeviceCreate.getOwner()!=null&&(messageReceiverDevice.getOwner()==null||!messageReceiverDeviceCreate.getOwner().getId().equals(messageReceiverDevice.getOwner().getId()))){
			messageReceiverDevice.setOwner(messageReceiverDeviceCreate.getOwner());
			update=true;
		}
		if(messageReceiverDeviceCreate.getExternalId()!=null&&!messageReceiverDeviceCreate.getExternalId().equals(messageReceiverDevice.getExternalId())){
			messageReceiverDevice.setExternalId(messageReceiverDeviceCreate.getExternalId());
			update=true;
		}
		return update;
	}

	public MessageReceiverDevice updateMessageReceiverDevice(MessageReceiverDeviceUpdate messageReceiverDeviceUpdate, SecurityContextBase securityContext) {
		MessageReceiverDevice MessageReceiverDevice = messageReceiverDeviceUpdate.getMessageReceiverDevice();
		if (updateMessageReceiverDeviceNoMerge(messageReceiverDeviceUpdate, MessageReceiverDevice)) {
			messageReceiverDeviceRepository.merge(MessageReceiverDevice);
		}
		return MessageReceiverDevice;
	}

	public void validate(MessageReceiverDeviceCreate messageReceiverDeviceCreate, SecurityContextBase securityContext) {
		basicService.validate(messageReceiverDeviceCreate,securityContext);
		String ownerId=messageReceiverDeviceCreate.getOwnerId();
		ChatUser owner=ownerId!=null?getByIdOrNull(ownerId,ChatUser.class,securityContext):null;
		if(ownerId!=null&&owner==null){
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"no owner with id "+ownerId);
		}
		messageReceiverDeviceCreate.setOwner(owner);
		if(messageReceiverDeviceCreate.getOwner()==null&&securityContext.getUser() instanceof ChatUser){
			messageReceiverDeviceCreate.setOwner((ChatUser) securityContext.getUser());
		}
	}

	public void validate(MessageReceiverDeviceFilter messageReceiverDeviceFilter, SecurityContextBase securityContext) {
		basicService.validate(messageReceiverDeviceFilter, securityContext);


	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return messageReceiverDeviceRepository.getByIdOrNull(id, c, securityContext);
	}


	public PaginationResponse<MessageReceiverDevice> getAllMessageReceiverDevices(MessageReceiverDeviceFilter MessageReceiverDeviceFilter, SecurityContextBase securityContext) {
		List<MessageReceiverDevice> list = listAllMessageReceiverDevices(MessageReceiverDeviceFilter, securityContext);
		long count = messageReceiverDeviceRepository.countAllMessageReceiverDevices(MessageReceiverDeviceFilter, securityContext);
		return new PaginationResponse<>(list, MessageReceiverDeviceFilter, count);
	}

	public List<MessageReceiverDevice> listAllMessageReceiverDevices(MessageReceiverDeviceFilter MessageReceiverDeviceFilter, SecurityContextBase securityContext) {
		return messageReceiverDeviceRepository.listAllMessageReceiverDevices(MessageReceiverDeviceFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return messageReceiverDeviceRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return messageReceiverDeviceRepository.findByIdOrNull(type, id);
	}
}
