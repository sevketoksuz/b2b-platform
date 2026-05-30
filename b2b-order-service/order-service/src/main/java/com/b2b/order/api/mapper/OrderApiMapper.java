package com.b2b.order.api.mapper;

import com.b2b.order.api.request.CreateOrderItemRequest;
import com.b2b.order.api.request.CreateOrderRequest;
import com.b2b.order.api.response.OrderItemResponse;
import com.b2b.order.api.response.OrderListItemResponse;
import com.b2b.order.api.response.OrderResponse;
import com.b2b.order.api.response.PagedResponse;
import com.b2b.order.application.command.dto.CreateOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderItemCommand;
import com.b2b.order.application.command.dto.CreateOrderResult;
import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.OrderItemResult;
import com.b2b.order.application.query.dto.GetOrderByIdResult;
import com.b2b.order.application.query.dto.GetOrdersResult;
import com.b2b.order.application.query.dto.OrderListItemResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderApiMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(
                request.buyerCompanyId(),
                request.sellerCompanyId(),
                toItemCommands(request.items())
        );
    }

    private List<CreateOrderItemCommand> toItemCommands(
            List<CreateOrderItemRequest> requests
    ) {
        return requests.stream()
                .map(this::toItemCommand)
                .toList();
    }

    private CreateOrderItemCommand toItemCommand(CreateOrderItemRequest request) {
        return new CreateOrderItemCommand(
                request.productId(),
                request.productName(),
                request.quantity(),
                request.unitPrice(),
                request.currency()
        );
    }

    public OrderResponse toResponse(CreateOrderResult result) {
        return new OrderResponse(
                result.id(),
                result.buyerCompanyId(),
                result.sellerCompanyId(),
                toItemResponses(result.items()),
                result.status(),
                result.totalAmount(),
                result.currency(),
                result.createdAt(),
                result.updatedAt(),
                null,
                null,
                null
        );
    }

    public OrderResponse toResponse(GetOrderByIdResult result) {
        return new OrderResponse(
                result.id(),
                result.buyerCompanyId(),
                result.sellerCompanyId(),
                toItemResponses(result.items()),
                result.status(),
                result.totalAmount(),
                result.currency(),
                result.createdAt(),
                result.updatedAt(),
                result.confirmedAt(),
                result.cancelledAt(),
                result.completedAt()
        );
    }

    public OrderResponse toResponse(ChangeOrderStatusResult result) {
        return new OrderResponse(
                result.id(),
                result.buyerCompanyId(),
                result.sellerCompanyId(),
                List.of(),
                result.status(),
                result.totalAmount(),
                result.currency(),
                result.createdAt(),
                result.updatedAt(),
                result.confirmedAt(),
                result.cancelledAt(),
                result.completedAt()
        );
    }

    public PagedResponse<OrderListItemResponse> toResponse(GetOrdersResult result) {
        List<OrderListItemResponse> content = result.content()
                .stream()
                .map(this::toListItemResponse)
                .toList();

        return new PagedResponse<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }

    private OrderListItemResponse toListItemResponse(OrderListItemResult result) {
        return new OrderListItemResponse(
                result.id(),
                result.buyerCompanyId(),
                result.sellerCompanyId(),
                result.status(),
                result.totalAmount(),
                result.currency(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    private List<OrderItemResponse> toItemResponses(List<OrderItemResult> items) {
        return items.stream()
                .map(this::toItemResponse)
                .toList();
    }

    private OrderItemResponse toItemResponse(OrderItemResult item) {
        return new OrderItemResponse(
                item.id(),
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPriceAmount(),
                item.unitPriceCurrency(),
                item.lineTotalAmount(),
                item.lineTotalCurrency()
        );
    }
}