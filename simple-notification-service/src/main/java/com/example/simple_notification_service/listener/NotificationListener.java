package com.example.simple_notification_service.listener;

import com.example.simple_notification_service.handler.NotificationWebSocketHandler;
import events.GameCreatedEvent;
import events.GameDeletedEvent;
import events.GameDiscountAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private static final String GAMES_EXCHANGE = "games-exchange";
    private static final String ANALYTICS_FANOUT = "analytics-fanout";

    private final NotificationWebSocketHandler webSocketHandler;

    public NotificationListener(NotificationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "ws.notifications.game-created",
                            durable = "true"
                    ),
                    exchange = @Exchange(name = GAMES_EXCHANGE, type = "topic", durable = "true"),
                    key = "game.created"
            )
    )
    public void onGameCreated(GameCreatedEvent event) {
        log.info("Received event from RabbitMQ: {}", event);

        String json = String.format(
                "{\"type\":\"GAME_CREATED\",\"gameId\":%d,\"title\":\"%s\"}",
                event.gameId(),
                event.title()
        );

        log.info("WS notify (created): {}", json);
        webSocketHandler.broadcast(json);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "ws.notifications.game-deleted",
                            durable = "true"
                    ),
                    exchange = @Exchange(name = GAMES_EXCHANGE, type = "topic", durable = "true"),
                    key = "game.deleted"
            )
    )
    public void onGameDeleted(GameDeletedEvent event) {
        log.info("Received event from RabbitMQ: {}", event);

        String json = String.format(
                "{\"type\":\"GAME_DELETED\",\"gameId\":%d}",
                event.gameId()
        );

        log.info("WS notify (deleted): {}", json);
        webSocketHandler.broadcast(json);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "ws.notifications.game-discount",
                            durable = "true"
                    ),
                    exchange = @Exchange(name = ANALYTICS_FANOUT, type = "fanout")
            )
    )
    public void onGameDiscount(GameDiscountAddedEvent event) {
        log.info("Received event from RabbitMQ: {}", event);

        String json = String.format(
                "{\"type\":\"DISCOUNT_ADDED\",\"gameId\":%d,\"discount\":%d,\"finalPrice\":%d}",
                event.gameId(),
                event.gamePercentDiscount(),
                event.gameFinalPrice()
        );

        log.info("WS notify (discount): {}", json);
        webSocketHandler.broadcast(json);
    }
}
