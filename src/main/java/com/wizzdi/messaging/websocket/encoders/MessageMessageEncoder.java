package com.wizzdi.messaging.websocket.encoders;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.data.jsoncontainers.Views;
import com.wizzdi.messaging.websocket.messages.IWSMessage;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by Asaf on 12/02/2017.
 */
public class MessageMessageEncoder implements Encoder.TextStream<IWSMessage> {

	private ObjectMapper objectMapper;

	@Override
	public void init(EndpointConfig config) {
		this.objectMapper = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

	}

	@Override
	public void destroy() {

	}

	@Override
	public void encode(IWSMessage object, Writer writer) throws IOException {
		objectMapper.writeValue(writer, object);

	}
}
