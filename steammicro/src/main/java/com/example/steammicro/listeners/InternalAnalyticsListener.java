package com.example.steammicro.listeners;

import events.GameDiscountAddedEvent;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InternalAnalyticsListener {

    @RabbitListener(
            bindings = @QueueBinding(
                    // Нужно другое имя очереди!
                    value = @Queue(name = "q.demorest.analytics.log", durable = "true"),
                    exchange = @Exchange(name = "analytics-fanout", type = "fanout")
            )
    )
    public void logAddingDiscount(GameDiscountAddedEvent event) {
        System.out.println("We just added discount to game: " + event.gameId());
    }
}
