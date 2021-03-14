package com.wizzdi.messaging;


import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.app.App;
import com.wizzdi.messaging.app.SecurityInterceptor;
import com.wizzdi.messaging.app.SecurityServiceTest;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.MessageReceiverDevice;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.MessageReceiverDeviceCreate;
import com.wizzdi.messaging.request.MessageReceiverDeviceFilter;
import com.wizzdi.messaging.request.MessageReceiverDeviceUpdate;
import com.wizzdi.messaging.service.ChatUserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class MessageReceiverDeviceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    @Lazy
    @Qualifier("adminSecurityContext")
    private SecurityContextBase securityContextBase;
    @Autowired
    private SecurityServiceTest securityServiceTest;


    private ChatUser chatUser;
    private MessageReceiverDevice messageReceiverDevice;


    @BeforeAll
    private void init() {
        AtomicReference<String> reference=new AtomicReference<>("admin");
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", reference.get());
                    return execution.execute(request, body);
                }));
        chatUser = chatUserService.createChatUser(new ChatUserCreate(), securityContextBase);
        SecurityInterceptor.setSecurityContext(securityServiceTest.getSecurityContext(chatUser));
    }

    @Test
    @Order(1)
    public void createMessageReceiverDevice() throws InterruptedException {
        MessageReceiverDeviceCreate request = new MessageReceiverDeviceCreate()
                .setExternalId("test")
                .setName("test messageReceiverDevice");

        ParameterizedTypeReference<MessageReceiverDevice> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<MessageReceiverDevice> response = this.restTemplate.exchange("/messageReceiverDevice/createMessageReceiverDevice", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        messageReceiverDevice = response.getBody();
        Assertions.assertNotNull(messageReceiverDevice);
        Assertions.assertEquals(request.getName(),messageReceiverDevice.getName());
        Assertions.assertEquals(request.getExternalId(),messageReceiverDevice.getExternalId());
        Assertions.assertEquals(chatUser.getId(),messageReceiverDevice.getOwner().getId());


    }

    @Test
    @Order(2)
    public void testGetAllMessageReceiverDevices() throws InterruptedException {
        MessageReceiverDeviceFilter request = new MessageReceiverDeviceFilter();
        ParameterizedTypeReference<PaginationResponse<MessageReceiverDevice>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<MessageReceiverDevice>> response = this.restTemplate.exchange("/messageReceiverDevice/getAllMessageReceiverDevices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<MessageReceiverDevice> body = response.getBody();
        Assertions.assertNotNull(body);
        List<MessageReceiverDevice> messageReceiverDevices = body.getList();
        Assertions.assertTrue(messageReceiverDevices.stream().anyMatch(f->f.getId().equals(messageReceiverDevice.getId())));

    }

    @Test
    @Order(2)
    public void updateMessageReceiverDevice() throws InterruptedException {
        MessageReceiverDeviceUpdate request = new MessageReceiverDeviceUpdate()
                .setId(messageReceiverDevice.getId())
                .setExternalId("test new ")
                .setName("test messageReceiverDevice new");

        ParameterizedTypeReference<MessageReceiverDevice> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<MessageReceiverDevice> response = this.restTemplate.exchange("/messageReceiverDevice/updateMessageReceiverDevice", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        messageReceiverDevice = response.getBody();
        Assertions.assertNotNull(messageReceiverDevice);
        Assertions.assertEquals(request.getName(),messageReceiverDevice.getName());
        Assertions.assertEquals(request.getExternalId(),messageReceiverDevice.getExternalId());
        Assertions.assertEquals(chatUser.getId(),messageReceiverDevice.getOwner().getId());


    }


}
