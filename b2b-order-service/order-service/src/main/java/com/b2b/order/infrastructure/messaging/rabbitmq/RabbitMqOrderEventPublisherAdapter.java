package com.b2b.order.infrastructure.messaging.rabbitmq;

import com.b2b.order.application.event.OrderCreatedEvent;
import com.b2b.order.application.event.OrderCreatedEventItem;
import com.b2b.order.application.port.out.OrderEventPublisherPort;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMqOrderEventPublisherAdapter implements OrderEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqOrderEventPublisherAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = toEvent(order);

        rabbitTemplate.convertAndSend(
                RabbitMqOrderConfig.ORDER_EXCHANGE,
                RabbitMqOrderConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );
    }

    private OrderCreatedEvent toEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                toEventItems(order.getItems()),
                order.getCreatedAt()
        );
    }

    private List<OrderCreatedEventItem> toEventItems(List<OrderItem> items) {
        return items.stream()
                .map(this::toEventItem)
                .toList();
    }

    private OrderCreatedEventItem toEventItem(OrderItem item) {
        return new OrderCreatedEventItem(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity().getValue(),
                item.getUnitPrice().getAmount(),
                item.getUnitPrice().getCurrency(),
                item.getLineTotal().getAmount()
        );
    }
}