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
        String message = "New game created: " + event.title() + " (id=" + event.gameId() + ")";

        log.info("WS notify (created): {}", message);
        webSocketHandler.broadcast(message);
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
        if (event.isFavourite()) {
            String message = "Game deleted: id=" + event.gameId();

            log.info("WS notify (deleted): {}", message);
            webSocketHandler.broadcast(message);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = "ws.notifications.game-discount",
                            durable = "true"
                    ),
                    exchange = @Exchange(name = ANALYTICS_FANOUT, type = "fanout", durable = "true")
            )
    )
    public void onGameDiscount(GameDiscountAddedEvent event) {
        if (event.isFavourite()) {
            String message = "Discount applied: gameId=" + event.gameId()
                    + ", discount=" + event.gamePercentDiscount() + "%, final price=" + event.gameFinalPrice();

            log.info("WS notify (discount): {}", message);
            webSocketHandler.broadcast(message);
        }
    }
}
