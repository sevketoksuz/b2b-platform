package com.b2b.order.infrastructure.messaging.rabbitmq;

import com.b2b.order.application.event.OrderConfirmedEvent;
import com.b2b.order.application.event.OrderConfirmedEventItem;
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
    public void publishOrderConfirmed(Order order) {
        OrderConfirmedEvent event = toEvent(order);

        rabbitTemplate.convertAndSend(
                RabbitMqOrderConfig.ORDER_EXCHANGE,
                RabbitMqOrderConfig.ORDER_CONFIRMED_ROUTING_KEY,
                event
        );
    }

    private OrderConfirmedEvent toEvent(Order order) {
        return new OrderConfirmedEvent(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                toEventItems(order.getItems()),
                order.getConfirmedAt()
        );
    }

    private List<OrderConfirmedEventItem> toEventItems(List<OrderItem> items) {
        return items.stream()
                .map(this::toEventItem)
                .toList();
    }

    private OrderConfirmedEventItem toEventItem(OrderItem item) {
        return new OrderConfirmedEventItem(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity().getValue(),
                item.getUnitPrice().getAmount(),
                item.getUnitPrice().getCurrency(),
                item.getLineTotal().getAmount()
        );
    }
}