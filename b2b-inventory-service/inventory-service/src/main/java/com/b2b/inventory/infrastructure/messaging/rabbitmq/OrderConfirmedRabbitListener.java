package com.b2b.inventory.infrastructure.messaging.rabbitmq;

import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEvent;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEventItem;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedRabbitListener {

    private final DecreaseStockUseCase decreaseStockUseCase;

    public OrderConfirmedRabbitListener(DecreaseStockUseCase decreaseStockUseCase) {
        this.decreaseStockUseCase = decreaseStockUseCase;
    }

    @RabbitListener(queues = RabbitMqInventoryConfig.ORDER_CONFIRMED_QUEUE)
    public void handle(OrderConfirmedEvent event) {
        event.items().forEach(item -> decreaseStock(event, item));
    }

    private void decreaseStock(OrderConfirmedEvent event, OrderConfirmedEventItem item) {
        DecreaseStockCommand command = new DecreaseStockCommand(
                event.sellerCompanyId(), // KRİTİK: stok sellerCompanyId üzerinden düşer
                item.productId(),
                item.quantity(),
                "ORDER_CONFIRMED: " + event.orderId()
        );

        decreaseStockUseCase.handle(command);
    }
}