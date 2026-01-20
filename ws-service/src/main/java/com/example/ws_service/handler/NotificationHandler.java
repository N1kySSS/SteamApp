package com.example.ws_service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);

    // ConcurrentHashMap.newKeySet() — потокобезопасный Set,
    // эффективнее CopyOnWriteArrayList при частых изменениях
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, WebSocketSession> sessionss = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserIdFromSession(session);
        sessionss.put(userId, session);
        log.info("Новое подключение: id={}, всего активных: {}",
                userId, getActiveConnections());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.debug("Сообщение от {}: {}", session.getId(), payload);

        if ("PING".equals(payload)) {
            sendMessage(session, new TextMessage("PONG"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserIdFromSession(session);

        sessionss.remove(userId);
        log.info("Отключение: id={}, причина={}, осталось: {}",
                session.getId(), status.getReason(), getActiveConnections());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String userId = getUserIdFromSession(session);
        log.error("Ошибка транспорта для сессии {}: {}",
                userId, exception.getMessage());

        sessionss.remove(userId);
    }

    public int broadcast(String message) {
        TextMessage textMessage = new TextMessage(message);
        int sent = 0;

        for (WebSocketSession session : sessionss.values()) {
            if (sendMessage(session, textMessage)) {
                sent++;
            }
        }

        log.info("Broadcast: отправлено {}/{} клиентам", sent, getActiveConnections());
        return sent;
    }

    public void sendMessageTo(String userId, String message) {
        TextMessage textMessage = new TextMessage(message);

        WebSocketSession session = getUserSession(userId);

        sendMessage(session, textMessage);

        log.info("Message: отправлено пользователю {}", userId);
    }

    private boolean sendMessage(WebSocketSession session, TextMessage message) {
        if (!session.isOpen()) {
            String userId = getUserIdFromSession(session);

            sessionss.remove(userId);
            return false;
        }
        try {
            // ConcurrentWebSocketSessionDecorator — альтернатива для высоких нагрузок
            synchronized (session) {
                session.sendMessage(message);
            }

            return true;
        } catch (IOException e) {
            String userId = getUserIdFromSession(session);
            log.warn("Ошибка отправки пользователю {}: {}", userId, e.getMessage());

            sessionss.remove(userId);
            return false;
        }
    }

    public int getActiveConnections() {
        return sessionss.size();
    }

    private WebSocketSession getUserSession(String userId) {
        return sessionss.get(userId);
    }

    private String getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        String[] parts = query.split("=");
        String userId = (parts.length > 1) ? parts[1] : null;

        return userId;
    }
}
