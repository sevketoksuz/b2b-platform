package com.b2b.inventory.domain.model;

import com.b2b.inventory.domain.enumtype.StockMovementType;
import com.b2b.inventory.domain.exception.StockDomainException;
import com.b2b.inventory.domain.valueobject.Quantity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockMovementTest {

    @Test
    void shouldCreateIncreaseMovement() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        StockMovement movement = StockMovement.createIncrease(
                companyId,
                productId,
                Quantity.of(10),
                Quantity.of(5),
                Quantity.of(15),
                "Manual increase"
        );

        assertNotNull(movement.getId());
        assertEquals(companyId, movement.getCompanyId());
        assertEquals(productId, movement.getProductId());
        assertEquals(StockMovementType.INCREASE, movement.getMovementType());
        assertEquals(Quantity.of(10), movement.getQuantity());
        assertEquals(Quantity.of(5), movement.getPreviousQuantity());
        assertEquals(Quantity.of(15), movement.getNewQuantity());
        assertEquals("Manual increase", movement.getReason());
        assertNotNull(movement.getCreatedAt());
    }

    @Test
    void shouldCreateDecreaseMovement() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        StockMovement movement = StockMovement.createDecrease(
                companyId,
                productId,
                Quantity.of(4),
                Quantity.of(10),
                Quantity.of(6),
                "Damaged products"
        );

        assertEquals(StockMovementType.DECREASE, movement.getMovementType());
        assertEquals(Quantity.of(4), movement.getQuantity());
        assertEquals(Quantity.of(10), movement.getPreviousQuantity());
        assertEquals(Quantity.of(6), movement.getNewQuantity());
    }

    @Test
    void shouldCreateAdjustmentMovement() {
        UUID companyId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        StockMovement movement = StockMovement.createAdjustment(
                companyId,
                productId,
                Quantity.of(10),
                Quantity.of(25),
                "Stock count correction"
        );

        assertEquals(StockMovementType.ADJUSTMENT, movement.getMovementType());
        assertEquals(Quantity.of(15), movement.getQuantity());
        assertEquals(Quantity.of(10), movement.getPreviousQuantity());
        assertEquals(Quantity.of(25), movement.getNewQuantity());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseMovementQuantityIsZero() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> StockMovement.createIncrease(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Quantity.zero(),
                        Quantity.of(5),
                        Quantity.of(5),
                        "Invalid increase"
                )
        );

        assertEquals("Movement quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIncreaseQuantitiesAreInvalid() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> StockMovement.createIncrease(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Quantity.of(10),
                        Quantity.of(5),
                        Quantity.of(20),
                        "Invalid increase"
                )
        );

        assertEquals("Invalid stock movement quantities for increase.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDecreaseQuantitiesAreInvalid() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> StockMovement.createDecrease(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Quantity.of(4),
                        Quantity.of(10),
                        Quantity.of(8),
                        "Invalid decrease"
                )
        );

        assertEquals("Invalid stock movement quantities for decrease.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAdjustmentDoesNotChangeQuantity() {
        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> StockMovement.createAdjustment(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Quantity.of(10),
                        Quantity.of(10),
                        "No change"
                )
        );

        assertEquals("Adjustment must change stock quantity.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReasonIsTooLong() {
        String longReason = "a".repeat(256);

        StockDomainException exception = assertThrows(
                StockDomainException.class,
                () -> StockMovement.createIncrease(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Quantity.of(10),
                        Quantity.of(5),
                        Quantity.of(15),
                        longReason
                )
        );

        assertEquals("Stock movement reason cannot exceed 255 characters.", exception.getMessage());
    }
}