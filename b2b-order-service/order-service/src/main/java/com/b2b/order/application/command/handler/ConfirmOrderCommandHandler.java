package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.ConfirmOrderCommand;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.in.ConfirmOrderUseCase;
import com.b2b.order.application.port.out.InventoryClientPort;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfirmOrderCommandHandler implements ConfirmOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryClientPort inventoryClientPort;

    public ConfirmOrderCommandHandler(
            OrderRepositoryPort orderRepositoryPort,
            InventoryClientPort inventoryClientPort
    ) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.inventoryClientPort = inventoryClientPort;
    }

    @Override
    @Transactional
    public ChangeOrderStatusResult handle(ConfirmOrderCommand command) {
        Order order = orderRepositoryPort.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + command.orderId()
                ));

        order.confirm();

        decreaseStocks(order);

        Order savedOrder = orderRepositoryPort.save(order);

        return toResult(savedOrder);
    }

    private void decreaseStocks(Order order) {
        for (OrderItem item : order.getItems()) {
            inventoryClientPort.decreaseStock(
                    order.getSellerCompanyId(),
                    item.getProductId(),
                    item.getQuantity().getValue(),
                    "Order confirmed: " + order.getId()
            );
        }
    }

    private ChangeOrderStatusResult toResult(Order order) {
        return new ChangeOrderStatusResult(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getConfirmedAt(),
                order.getCancelledAt(),
                order.getCompletedAt()
        );
    }
}