package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.CancelOrderCommand;
import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.in.CancelOrderUseCase;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelOrderCommandHandler implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public CancelOrderCommandHandler(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @Transactional
    public ChangeOrderStatusResult handle(CancelOrderCommand command) {
        Order order = orderRepositoryPort.findById(command.orderId())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + command.orderId()
                ));

        order.cancel();

        Order savedOrder = orderRepositoryPort.save(order);

        return toResult(savedOrder);
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