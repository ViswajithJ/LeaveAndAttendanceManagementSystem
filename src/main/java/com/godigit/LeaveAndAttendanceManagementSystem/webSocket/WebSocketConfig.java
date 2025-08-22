package com.godigit.LeaveAndAttendanceManagementSystem.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PunchInWebSocketHandler punchInWebSocketHandler;

    public WebSocketConfig(PunchInWebSocketHandler punchInWebSocketHandler) {
        this.punchInWebSocketHandler = punchInWebSocketHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(punchInWebSocketHandler,"/ws/punchIn").setAllowedOrigins("*");
    }
}

