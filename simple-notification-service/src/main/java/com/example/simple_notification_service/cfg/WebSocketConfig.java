package com.example.simple_notification_service.cfg;

import com.example.simple_notification_service.handler.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationHandler;

    public WebSocketConfig(NotificationWebSocketHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationHandler, "/ws/notifications")
                // ВНИМАНИЕ: "*" только для разработки!
                // В продакшене: .setAllowedOrigins("https://yourdomain.com")
                .setAllowedOrigins("*");

        // Для старых браузеров без WebSocket можно добавить SockJS fallback:
        // .withSockJS();
    }
}
