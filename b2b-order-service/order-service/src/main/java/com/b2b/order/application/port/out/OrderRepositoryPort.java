package com.b2b.order.application.port.out;

import com.b2b.order.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    PagedResult<Order> findOrders(OrderSearchCriteria criteria);
}