package com.b2b.order.application.query.handler;

import com.b2b.order.application.command.dto.OrderItemResult;
import com.b2b.order.application.exception.OrderNotFoundException;
import com.b2b.order.application.port.in.GetOrderByIdUseCase;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.application.query.dto.GetOrderByIdQuery;
import com.b2b.order.application.query.dto.GetOrderByIdResult;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetOrderByIdQueryHandler implements GetOrderByIdUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrderByIdQueryHandler(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetOrderByIdResult handle(GetOrderByIdQuery query) {
        Order order = orderRepositoryPort.findById(query.orderId())
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + query.orderId()
                ));

        return toResult(order);
    }

    private GetOrderByIdResult toResult(Order order) {
        return new GetOrderByIdResult(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                toItemResults(order),
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

    private List<OrderItemResult> toItemResults(Order order) {
        return order.getItems()
                .stream()
                .map(this::toItemResult)
                .toList();
    }

    private OrderItemResult toItemResult(OrderItem item) {
        return new OrderItemResult(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity().getValue(),
                item.getUnitPrice().getAmount(),
                item.getUnitPrice().getCurrency(),
                item.getLineTotal().getAmount(),
                item.getLineTotal().getCurrency()
        );
    }
}