package com.wizzdi.messaging.config;


import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.messaging.websocket.messages.NewMessage;
import org.pf4j.Extension;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Component
@Extension
@EnableTransactionManagement(proxyTargetClass = true)
public class Config implements Plugin {


}
