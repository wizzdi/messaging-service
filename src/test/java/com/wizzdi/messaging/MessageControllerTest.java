package com.wizzdi.messaging;


import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.app.App;
import com.wizzdi.messaging.app.SecurityInterceptor;
import com.wizzdi.messaging.app.SecurityServiceTest;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
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
public class MessageControllerTest {

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
    private Message message;


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
        SecurityInterceptor.setChatUser(chatUser);
    }

    @Test
    @Order(1)
    public void createMessage() throws InterruptedException {
        MessageCreate request = new MessageCreate()
                .setContent("test")
                .setName("test message");

        ParameterizedTypeReference<Message> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Message> response = this.restTemplate.exchange("/message/createMessage", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(request.getName(),message.getName());
        Assertions.assertEquals(request.getContent(),message.getContent());
        Assertions.assertEquals(chatUser.getId(),message.getSender().getId());


    }

    @Test
    @Order(2)
    public void testGetAllMessages() throws InterruptedException {
        MessageFilter request = new MessageFilter();
        ParameterizedTypeReference<PaginationResponse<Message>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Message>> response = this.restTemplate.exchange("/message/getAllMessages", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Message> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Message> messages = body.getList();
        Assertions.assertTrue(messages.stream().anyMatch(f->f.getId().equals(message.getId())));

    }


}
