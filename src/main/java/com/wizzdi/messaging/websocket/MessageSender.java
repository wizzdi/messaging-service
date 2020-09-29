package com.wizzdi.messaging.websocket;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.wizzdi.messaging.websocket.messages.IWSMessage;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class MessageSender implements ServicePlugin {


	private static final Logger logger=LoggerFactory.getLogger(MessageSender.class);


	@Async
	@EventListener
	public void sendEvent(IWSMessage iwsMessage) {
		List<Session> toRemove = new ArrayList<>();


		List<Session> sessionsForUsers = MessageWebSocket.getSessionsForUsers(iwsMessage.getTarget());
		logger.info("Received ui event " + iwsMessage + " to send to " + sessionsForUsers.size() + " sessions");
		for (Session session : sessionsForUsers) {
			try {
				if (!session.isOpen()) {
					toRemove.add(session);
					continue;
				}
				session.getBasicRemote().sendObject(iwsMessage);
			} catch (EncodeException | IOException e) {
				logger.error( "unable to send message", e);
				try {
					session.close();
				} catch (IOException e1) {
					logger.error( "unable to close session");
				}
				toRemove.add(session);
			}
		}
		MessageWebSocket.removeAll(toRemove);
	}



}
