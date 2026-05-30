package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.exception.StockDomainException;
import com.b2b.inventory.domain.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @Test
    void shouldCreateStockWithZeroQuantity() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Stock stock = Stock.create(companyId, productId);

        assertNotNull(stock.getId());
        assertEquals(companyId, stock.getCompanyId());
        assertEquals(productId, stock.getProductId());
        assertEquals(Quantity.zero(), stock.getQuantity());
        assertNotNull(stock.getCreatedAt());
        assertNotNull(stock.getUpdatedAt());
    }

    @Test
    void shouldIncreaseStockQuantity() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        stock.increase(Quantity.of(10));

        assertEquals(Quantity.of(10), stock.getQuantity());
    }

    @Test
    void shouldDecreaseStockQuantity() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        stock.increase(Quantity.of(10));
        stock.decrease(Quantity.of(4));

        assertEquals(Quantity.of(6), stock.getQuantity());
    }

    @Test
    void shouldAdjustStockQuantity() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        stock.adjust(Quantity.of(25));

        assertEquals(Quantity.of(25), stock.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseAmountIsZero() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> stock.increase(Quantity.zero())
        );

        assertEquals("Amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDecreaseAmountIsZero() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> stock.decrease(Quantity.zero())
        );

        assertEquals("Amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        stock.increase(Quantity.of(5));

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> stock.decrease(Quantity.of(10))
        );

        assertEquals("Insufficient stock quantity.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAdjustingToSameQuantity() {
        Stock stock = Stock.create(UUID.randomUUID(), UUID.randomUUID());

        stock.adjust(Quantity.of(10));

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> stock.adjust(Quantity.of(10))
        );

        assertEquals("New quantity is same as current quantity.", exception.getMessage());
    }
}