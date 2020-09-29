package com.wizzdi.messaging.config;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CrossLoaderResolver;
import com.flexicore.events.PluginsLoadedEvent;
import com.flexicore.interfaces.ServicePlugin;
import com.wizzdi.messaging.websocket.messages.NewMessage;
import org.pf4j.Extension;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Component
@Extension
public class Config implements ServicePlugin {

    @EventListener
    public void init(PluginsLoadedEvent pluginsLoadedEvent){
        CrossLoaderResolver.registerClass(NewMessage.class);
    }
}
