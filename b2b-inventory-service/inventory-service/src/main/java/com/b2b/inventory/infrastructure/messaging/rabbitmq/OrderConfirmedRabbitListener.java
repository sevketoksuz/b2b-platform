package com.b2b.inventory.infrastructure.messaging.rabbitmq;

import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEvent;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedRabbitListener {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmedRabbitListener.class);

    private final DecreaseStockUseCase decreaseStockUseCase;

    public OrderConfirmedRabbitListener(DecreaseStockUseCase decreaseStockUseCase) {
        this.decreaseStockUseCase = decreaseStockUseCase;
    }

    @RabbitListener(queues = RabbitMqInventoryConfig.ORDER_CONFIRMED_QUEUE)
    public void handle(OrderConfirmedEvent event) {
        log.info(
                "Order confirmed event received. orderId={}, buyerCompanyId={}, sellerCompanyId={}, itemCount={}",
                event.orderId(),
                event.buyerCompanyId(),
                event.sellerCompanyId(),
                event.items().size()
        );

        event.items().forEach(item -> decreaseStock(event, item));
    }

    private void decreaseStock(OrderConfirmedEvent event, OrderConfirmedEventItem item) {
        log.info(
                "Decreasing stock from order confirmed event. orderId={}, sellerCompanyId={}, productId={}, quantity={}",
                event.orderId(),
                event.sellerCompanyId(),
                item.productId(),
                item.quantity()
        );

        DecreaseStockCommand command = new DecreaseStockCommand(
                event.sellerCompanyId(),
                item.productId(),
                item.quantity(),
                "ORDER_CONFIRMED: " + event.orderId()
        );

        decreaseStockUseCase.handle(command);
    }
}