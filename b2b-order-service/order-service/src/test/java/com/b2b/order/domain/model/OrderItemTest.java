package com.b2b.order.domain.model;

import com.b2b.order.domain.exception.OrderDomainException;
import com.b2b.order.domain.valueobject.Money;
import com.b2b.order.domain.valueobject.OrderQuantity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemWhenValuesAreValid() {
        UUID productId = UUID.randomUUID();

        OrderItem item = OrderItem.create(
                productId,
                "Gaming Mouse",
                OrderQuantity.of(2),
                Money.of(new BigDecimal("750"), "TRY")
        );

        assertNotNull(item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals("Gaming Mouse", item.getProductName());
        assertEquals(OrderQuantity.of(2), item.getQuantity());
        assertEquals(Money.of(new BigDecimal("750"), "TRY"), item.getUnitPrice());
        assertEquals(Money.of(new BigDecimal("1500"), "TRY"), item.getLineTotal());
    }

    @Test
    void shouldTrimProductNameWhenCreatingOrderItem() {
        OrderItem item = OrderItem.create(
                UUID.randomUUID(),
                "  Gaming Mouse  ",
                OrderQuantity.of(1),
                Money.of(new BigDecimal("100"), "TRY")
        );

        assertEquals("Gaming Mouse", item.getProductName());
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> OrderItem.create(
                        null,
                        "Gaming Mouse",
                        OrderQuantity.of(1),
                        Money.of(new BigDecimal("100"), "TRY")
                )
        );

        assertEquals("Product id cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsBlank() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderItem.create(
                        UUID.randomUUID(),
                        " ",
                        OrderQuantity.of(1),
                        Money.of(new BigDecimal("100"), "TRY")
                )
        );

        assertEquals("Product name cannot be blank.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductNameIsTooShort() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderItem.create(
                        UUID.randomUUID(),
                        "A",
                        OrderQuantity.of(1),
                        Money.of(new BigDecimal("100"), "TRY")
                )
        );

        assertEquals("Product name must contain at least 2 characters.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsZero() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderItem.create(
                        UUID.randomUUID(),
                        "Gaming Mouse",
                        OrderQuantity.of(1),
                        Money.zero("TRY")
                )
        );

        assertEquals("Order item unit price must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldRestoreOrderItemWhenValuesAreValid() {
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        OrderItem item = OrderItem.restore(
                itemId,
                productId,
                "Gaming Mouse",
                OrderQuantity.of(2),
                Money.of(new BigDecimal("750"), "TRY"),
                Money.of(new BigDecimal("1500"), "TRY")
        );

        assertEquals(itemId, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(Money.of(new BigDecimal("1500"), "TRY"), item.getLineTotal());
    }

    @Test
    void shouldThrowExceptionWhenRestoredLineTotalIsInvalid() {
        OrderDomainException exception = assertThrows(
                OrderDomainException.class,
                () -> OrderItem.restore(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Gaming Mouse",
                        OrderQuantity.of(2),
                        Money.of(new BigDecimal("750"), "TRY"),
                        Money.of(new BigDecimal("1400"), "TRY")
                )
        );

        assertEquals("Order item line total is invalid.", exception.getMessage());
    }
}