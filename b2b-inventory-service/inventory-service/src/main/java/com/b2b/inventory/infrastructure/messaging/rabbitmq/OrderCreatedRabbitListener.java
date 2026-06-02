package com.b2b.inventory.infrastructure.messaging.rabbitmq;

import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderCreatedEvent;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderCreatedEventItem;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedRabbitListener {

    private final DecreaseStockUseCase decreaseStockUseCase;

    public OrderCreatedRabbitListener(DecreaseStockUseCase decreaseStockUseCase) {
        this.decreaseStockUseCase = decreaseStockUseCase;
    }

    @RabbitListener(queues = RabbitMqInventoryConfig.ORDER_CREATED_QUEUE)
    public void handle(OrderCreatedEvent event) {
        event.items().forEach(item -> decreaseStock(event, item));
    }

    private void decreaseStock(OrderCreatedEvent event, OrderCreatedEventItem item) {
        DecreaseStockCommand command = new DecreaseStockCommand(
                event.sellerCompanyId(), // KRİTİK: stok sellerCompanyId üzerinden düşer
                item.productId(),
                item.quantity(),
                "ORDER_CREATED: " + event.orderId()
        );

        decreaseStockUseCase.handle(command);
    }
}