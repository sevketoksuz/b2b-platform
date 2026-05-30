package com.b2b.order.infrastructure.persistence.mapper;

import com.b2b.order.domain.model.Order;
import com.b2b.order.domain.model.OrderItem;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import com.b2b.order.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.b2b.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderPersistenceMapper {

    public OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity orderEntity = new OrderJpaEntity(
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

        order.getItems()
                .stream()
                .map(this::toJpaEntity)
                .forEach(orderEntity::addItem);

        return orderEntity;
    }

    private OrderItemJpaEntity toJpaEntity(OrderItem item) {
        return new OrderItemJpaEntity(
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

    public Order toDomainModel(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems()
                .stream()
                .map(this::toDomainModel)
                .toList();

        return Order.restore(
                entity.getId(),
                entity.getBuyerCompanyId(),
                entity.getSellerCompanyId(),
                items,
                entity.getStatus(),
                Money.of(entity.getTotalAmount(), entity.getCurrency()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getConfirmedAt(),
                entity.getCancelledAt(),
                entity.getCompletedAt()
        );
    }

    private OrderItem toDomainModel(OrderItemJpaEntity entity) {
        return OrderItem.restore(
                entity.getId(),
                entity.getProductId(),
                entity.getProductName(),
                OrderQuantity.of(entity.getQuantity()),
                Money.of(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency()),
                Money.of(entity.getLineTotalAmount(), entity.getLineTotalCurrency())
        );
    }
}