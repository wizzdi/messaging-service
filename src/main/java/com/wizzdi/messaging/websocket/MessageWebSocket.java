package com.wizzdi.messaging.websocket;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.WebSocketPlugin;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;
import com.wizzdi.messaging.websocket.encoders.MessageMessageEncoder;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Asaf on 31/08/2016.
 */
@ServerEndpoint(value = "/FlexiCore/messageWS/{authenticationKey}", encoders = {MessageMessageEncoder.class})
@PluginInfo(version = 1)
@Extension
@Component
public class MessageWebSocket implements WebSocketPlugin {

	private static final Logger logger= LoggerFactory.getLogger(MessageWebSocket.class);

	private final static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
	private static final String SECURITY_CONTEXT_KEY = "securityContext";

	@OnMessage
	public void receiveMessage(String message, Session session) {
		logger.info("Received : " + message + ", session:" + session.getId());
	}

	@OnOpen
	public void open(@PathParam("authenticationKey") String authenticationKey, Session session) {
		String id = session.getId();
		logger.info("Open session:" + id);
		sessionMap.put(id, session);

	}

	@OnClose
	public void close(@PathParam("authenticationKey") String authenticationKey,Session session, CloseReason c) {
		logger.info("Closing:" + session.getId());
		sessionMap.remove(session.getId());
	}

	public static List<Session> getSessionsForUsers(Set<String> targetUserIds) {
		return sessionMap.values().stream()
				.filter(f->f.getUserProperties()!=null&&
						f.getUserProperties().containsKey(SECURITY_CONTEXT_KEY)&&
						targetUserIds.contains(((SecurityContext)f.getUserProperties().get(SECURITY_CONTEXT_KEY)).getUser().getId()))
				.collect(Collectors.toList());
	}

	public static void removeAll(List<Session> sessions){
		for (Session session : sessions) {
			sessionMap.remove(session.getId(),session);
		}
	}

}
