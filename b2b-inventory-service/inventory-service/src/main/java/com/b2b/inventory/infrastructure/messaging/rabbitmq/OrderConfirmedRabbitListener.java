package com.b2b.inventory.infrastructure.messaging.rabbitmq;

import com.b2b.inventory.application.command.dto.ProcessOrderConfirmedItemCommand;
import com.b2b.inventory.application.port.in.ProcessOrderConfirmedItemUseCase;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEvent;
import com.b2b.inventory.infrastructure.messaging.rabbitmq.event.OrderConfirmedEventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedRabbitListener {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmedRabbitListener.class);

    private final ProcessOrderConfirmedItemUseCase processOrderConfirmedItemUseCase;

    public OrderConfirmedRabbitListener(ProcessOrderConfirmedItemUseCase processOrderConfirmedItemUseCase) {
        this.processOrderConfirmedItemUseCase = processOrderConfirmedItemUseCase;
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

        event.items().forEach(item -> processItem(event, item));
    }

    private void processItem(OrderConfirmedEvent event, OrderConfirmedEventItem item) {
        log.info(
                "Processing order confirmed item event. orderId={}, sellerCompanyId={}, productId={}, quantity={}",
                event.orderId(),
                event.sellerCompanyId(),
                item.productId(),
                item.quantity()
        );

        ProcessOrderConfirmedItemCommand command = new ProcessOrderConfirmedItemCommand(
                event.orderId(),
                event.sellerCompanyId(),
                item.productId(),
                item.quantity()
        );

        processOrderConfirmedItemUseCase.handle(command);
    }
}