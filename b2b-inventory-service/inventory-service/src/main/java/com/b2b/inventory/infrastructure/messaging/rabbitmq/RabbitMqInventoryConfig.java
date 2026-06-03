package com.b2b.inventory.infrastructure.messaging.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqInventoryConfig {

    public static final String ORDER_EXCHANGE = "b2b.order.exchange";
    public static final String ORDER_CONFIRMED_QUEUE = "inventory.order-confirmed.queue";
    public static final String ORDER_CONFIRMED_ROUTING_KEY = "order.confirmed";

    public static final String ORDER_DEAD_LETTER_EXCHANGE = "b2b.order.dlx";
    public static final String ORDER_CONFIRMED_DEAD_LETTER_QUEUE = "inventory.order-confirmed.dlq";
    public static final String ORDER_CONFIRMED_DEAD_LETTER_ROUTING_KEY = "order.confirmed.dlq";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange orderDeadLetterExchange() {
        return new DirectExchange(ORDER_DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return new Queue(
                ORDER_CONFIRMED_QUEUE,
                true,
                false,
                false,
                Map.of(
                        "x-dead-letter-exchange", ORDER_DEAD_LETTER_EXCHANGE,
                        "x-dead-letter-routing-key", ORDER_CONFIRMED_DEAD_LETTER_ROUTING_KEY
                )
        );
    }

    @Bean
    public Queue orderConfirmedDeadLetterQueue() {
        return new Queue(ORDER_CONFIRMED_DEAD_LETTER_QUEUE, true);
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder
                .bind(orderConfirmedQueue())
                .to(orderExchange())
                .with(ORDER_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding orderConfirmedDeadLetterBinding() {
        return BindingBuilder
                .bind(orderConfirmedDeadLetterQueue())
                .to(orderDeadLetterExchange())
                .with(ORDER_CONFIRMED_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}