package com.wizzdi.messaging.websocket.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type")
@JsonTypeIdResolver(CrossLoaderResolver.class)
public interface IWSMessage {

    Set<String> getTarget();
}
