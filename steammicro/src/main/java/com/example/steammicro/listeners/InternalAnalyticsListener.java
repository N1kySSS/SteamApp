package com.example.steammicro.listeners;

import events.GameDiscountAddedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InternalAnalyticsListener {

    private static final Logger log = LoggerFactory.getLogger(InternalAnalyticsListener.class);

    @RabbitListener(
            bindings = @QueueBinding(
                    // Нужно другое имя очереди!
                    value = @Queue(name = "q.demorest.analytics.log", durable = "true"),
                    exchange = @Exchange(name = "analytics-fanout", type = "fanout")
            )
    )
    public void logAddingDiscount(GameDiscountAddedEvent event) {
        log.info("Applied discount {}% to game {}. New price: {}", event.gamePercentDiscount(), event.gameId(), event.gameFinalPrice());
        System.out.println("We just added discount to game: " + event.gameId());
    }
}
