package com.b2b.order.api.controller;

import com.b2b.order.api.mapper.OrderApiMapper;
import com.b2b.order.api.request.CreateOrderRequest;
import com.b2b.order.api.response.OrderListItemResponse;
import com.b2b.order.api.response.OrderResponse;
import com.b2b.order.api.response.PagedResponse;
import com.b2b.order.application.command.dto.CancelOrderCommand;
import com.b2b.order.application.command.dto.ChangeOrderStatusResult;
import com.b2b.order.application.command.dto.CompleteOrderCommand;
import com.b2b.order.application.command.dto.ConfirmOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderCommand;
import com.b2b.order.application.command.dto.CreateOrderResult;
import com.b2b.order.application.port.in.CancelOrderUseCase;
import com.b2b.order.application.port.in.CompleteOrderUseCase;
import com.b2b.order.application.port.in.ConfirmOrderUseCase;
import com.b2b.order.application.port.in.CreateOrderUseCase;
import com.b2b.order.application.port.in.GetOrderByIdUseCase;
import com.b2b.order.application.port.in.GetOrdersUseCase;
import com.b2b.order.application.query.dto.GetOrderByIdQuery;
import com.b2b.order.application.query.dto.GetOrderByIdResult;
import com.b2b.order.application.query.dto.GetOrdersQuery;
import com.b2b.order.application.query.dto.GetOrdersResult;
import com.b2b.order.domain.enumtype.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompleteOrderUseCase completeOrderUseCase;
    private final OrderApiMapper orderApiMapper;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderByIdUseCase getOrderByIdUseCase,
            GetOrdersUseCase getOrdersUseCase,
            ConfirmOrderUseCase confirmOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            CompleteOrderUseCase completeOrderUseCase,
            OrderApiMapper orderApiMapper
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.getOrdersUseCase = getOrdersUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.completeOrderUseCase = completeOrderUseCase;
        this.orderApiMapper = orderApiMapper;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        CreateOrderCommand command = orderApiMapper.toCommand(request);

        CreateOrderResult result = createOrderUseCase.handle(command);

        OrderResponse response = orderApiMapper.toResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID id
    ) {
        GetOrderByIdQuery query = new GetOrderByIdQuery(id);

        GetOrderByIdResult result = getOrderByIdUseCase.handle(query);

        OrderResponse response = orderApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<OrderListItemResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID buyerCompanyId,
            @RequestParam(required = false) UUID sellerCompanyId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDate
    ) {
        GetOrdersQuery query = new GetOrdersQuery(
                page,
                size,
                buyerCompanyId,
                sellerCompanyId,
                status,
                fromDate,
                toDate
        );

        GetOrdersResult result = getOrdersUseCase.handle(query);

        PagedResponse<OrderListItemResponse> response = orderApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable UUID id
    ) {
        ConfirmOrderCommand command = new ConfirmOrderCommand(id);

        ChangeOrderStatusResult result = confirmOrderUseCase.handle(command);

        OrderResponse response = orderApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id
    ) {
        CancelOrderCommand command = new CancelOrderCommand(id);

        ChangeOrderStatusResult result = cancelOrderUseCase.handle(command);

        OrderResponse response = orderApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @PathVariable UUID id
    ) {
        CompleteOrderCommand command = new CompleteOrderCommand(id);

        ChangeOrderStatusResult result = completeOrderUseCase.handle(command);

        OrderResponse response = orderApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}