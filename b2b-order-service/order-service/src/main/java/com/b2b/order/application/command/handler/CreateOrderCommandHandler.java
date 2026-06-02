package com.b2b.order.application.command.handler;

import com.b2b.order.application.command.dto.CreateOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderItemCommand;
import com.b2b.order.application.command.dto.CreateOrderResult;
import com.b2b.order.application.command.dto.OrderItemResult;
import com.b2b.order.application.port.in.CreateOrderUseCase;
import com.b2b.order.application.port.out.OrderEventPublisherPort;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CreateOrderCommandHandler implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisherPort orderEventPublisherPort;

    public CreateOrderCommandHandler(
            OrderRepositoryPort orderRepositoryPort,
            OrderEventPublisherPort orderEventPublisherPort
    ) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
    }

    @Override
    @Transactional
    public CreateOrderResult handle(CreateOrderCommand command) {
        List<OrderItem> orderItems = toOrderItems(command.items());

        Order order = Order.create(
                command.buyerCompanyId(),
                command.sellerCompanyId(),
                orderItems
        );

        Order savedOrder = orderRepositoryPort.save(order);

        orderEventPublisherPort.publishOrderCreated(savedOrder);

        return toResult(savedOrder);
    }

    private List<OrderItem> toOrderItems(List<CreateOrderItemCommand> itemCommands) {
        if (itemCommands == null) {
            return List.of();
        }

        return itemCommands.stream()
                .map(this::toOrderItem)
                .toList();
    }

    private OrderItem toOrderItem(CreateOrderItemCommand itemCommand) {
        return OrderItem.create(
                itemCommand.productId(),
                itemCommand.productName(),
                OrderQuantity.of(itemCommand.quantity()),
                Money.of(itemCommand.unitPrice(), itemCommand.currency())
        );
    }

    private CreateOrderResult toResult(Order order) {
        return new CreateOrderResult(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                toItemResults(order),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt()
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