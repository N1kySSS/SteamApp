package com.example.statistics_service.listeners;

import com.rabbitmq.client.Channel;
import events.GameCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatisticsListener {
    private static final Logger log = LoggerFactory.getLogger(StatisticsListener.class);
    private static final String EXCHANGE_NAME = "games-exchange";
    private static final String QUEUE_NAME = "statistics-queue";

    private final Map<Long, String> gamesStats = new ConcurrentHashMap<>();

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = QUEUE_NAME,
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
        try {
            log.info("Received GameCreatedEvent: {}.", event);

            gamesStats.put(event.gameId(), event.title());
            log.info("Total games: {}", gamesStats.size());

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to process statistics for game: {}", event, e);
            channel.basicNack(deliveryTag, false, true);
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
}
