package com.example.steam_audit.listeners;

import com.rabbitmq.client.Channel;
import events.GameCreatedEvent;
import events.GameDeletedEvent;
import events.GameDiscountAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameEventListener {
    private static final Logger log = LoggerFactory.getLogger(GameEventListener.class);
    private static final String EXCHANGE_NAME = "games-exchange";
    private static final String QUEUE_NAME_CREATE = "notification-create-queue";
    private static final String QUEUE_NAME_DELETE = "notification-delete-queue";

    private final Set<Long> processedGameCreations = ConcurrentHashMap.newKeySet();
    private final Set<Long> processedGameDeletions = ConcurrentHashMap.newKeySet();

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME_CREATE,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "game.created"
            )
    )
    public void handleGameCreatedEvent(
            @Payload GameCreatedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException  {

        if (!processedGameCreations.add(event.gameId())) {
            log.warn("Duplicate event received for gameId: {}", event.gameId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        try {
            log.info("Received GameCreatedEvent: {}.", event);
            if (event.title() != null && event.title().equalsIgnoreCase("CRASH")) {
                throw new RuntimeException("Simulating processing error for DLQ test");
            }
            // Логика отправки уведомления...
            log.info("Notification sent for new game '{}'!", event.title());
            // Отправляем подтверждение брокеру
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            // Отправляем nack и НЕ просим вернуть в очередь (requeue=false)
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME_DELETE,
                            durable = "true",
                            arguments = {
                                    @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                    @Argument(name = "x-dead-letter-routing-key", value = "dlq.notifications")
                            }),
                    exchange = @Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                    key = "game.deleted"
            )
    )
    public void handleGameDeletedEvent(
            @Payload GameDeletedEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws IOException {

        if (!processedGameDeletions.add(event.gameId())) {
            log.warn("Duplicate event received for gameId: {}", event.gameId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        try {
            log.info("Received GameDeletedEvent: {}", event);
            // Логика отмены уведомлений...
            log.info("Notifications cancelled for deleted gameId {}!", event.gameId());
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Failed to process event: {}. Sending to DLQ.", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "notification-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.notifications"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! Received message in DLQ: {}", failedMessage);
        // Здесь может быть логика оповещения администраторов
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    // Уникальное имя очереди для уведомлений!
                    value = @Queue(name = "q.audit.analytics", durable = "true"),
                    exchange = @Exchange(name = "analytics-fanout", type = "fanout")
            )
    )
    public void handleAddingDiscount(GameDiscountAddedEvent event) {
        log.info("NOTIFY: Sending email. Game {} selling with discount: {} - price {}", event.gameId(), event.gamePercentDiscount(), event.gameFinalPrice());
    }
}
