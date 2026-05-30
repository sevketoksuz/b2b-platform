package com.b2b.order.application.query.handler;

import com.b2b.order.application.port.in.GetOrdersUseCase;
import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.application.port.out.OrderSearchCriteria;
import com.b2b.order.application.port.out.PagedResult;
import com.b2b.order.application.query.dto.GetOrdersQuery;
import com.b2b.order.application.query.dto.GetOrdersResult;
import com.b2b.order.application.query.dto.OrderListItemResult;
import com.b2b.order.domain.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetOrdersQueryHandler implements GetOrdersUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public GetOrdersQueryHandler(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetOrdersResult handle(GetOrdersQuery query) {
        validatePagination(query.page(), query.size());

        OrderSearchCriteria criteria = new OrderSearchCriteria(
                query.page(),
                query.size(),
                query.buyerCompanyId(),
                query.sellerCompanyId(),
                query.status(),
                query.fromDate(),
                query.toDate()
        );

        PagedResult<Order> pagedOrders = orderRepositoryPort.findOrders(criteria);

        List<OrderListItemResult> content = pagedOrders.content()
                .stream()
                .map(this::toListItemResult)
                .toList();

        return new GetOrdersResult(
                content,
                pagedOrders.page(),
                pagedOrders.size(),
                pagedOrders.totalElements(),
                pagedOrders.totalPages()
        );
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    private OrderListItemResult toListItemResult(Order order) {
        return new OrderListItemResult(
                order.getId(),
                order.getBuyerCompanyId(),
                order.getSellerCompanyId(),
                order.getStatus(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}