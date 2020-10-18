package com.wizzdi.messaging.rest;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.init.FlexiCoreApplication;
import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import com.wizzdi.messaging.data.MessageRepository;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.service.MessageService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {FlexiCoreApplication.class, EntitiesSeedProvider.class,MessageRESTService.class, MessageService.class, MessageRepository.class,})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class MessageRESTServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeAll
    private void init() {
        ResponseEntity<AuthenticationResponse> authenticationResponse = this.restTemplate.postForEntity("/FlexiCore/rest/authenticationNew/login", new AuthenticationRequest().setEmail("admin@flexicore.com").setPassword("admin"), AuthenticationResponse.class);
        String authenticationKey = authenticationResponse.getBody().getAuthenticationKey();
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", authenticationKey);
                    return execution.execute(request, body);
                }));
    }


    @Test
    @Order(1)
    public void testGetAllMessages() throws InterruptedException {
        MessageFilter request = new MessageFilter();
        ParameterizedTypeReference<PaginationResponse<Message>> t = new ParameterizedTypeReference<PaginationResponse<Message>>() {};
        ResponseEntity<PaginationResponse<Message>> response = this.restTemplate.exchange("/FlexiCore/rest/plugins/message/getAllMessages", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Message> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Message> auditingEvents = body.getList();
        Assertions.assertFalse(auditingEvents.isEmpty());

    }


}
