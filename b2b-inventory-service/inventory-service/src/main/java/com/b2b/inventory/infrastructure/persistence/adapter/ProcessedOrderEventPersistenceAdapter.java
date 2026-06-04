package com.b2b.inventory.infrastructure.persistence.adapter;

import com.b2b.inventory.application.port.out.ProcessedOrderEventRepositoryPort;
import com.b2b.inventory.infrastructure.persistence.repository.SpringDataProcessedOrderEventRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Component
public class ProcessedOrderEventPersistenceAdapter implements ProcessedOrderEventRepositoryPort {

    private final SpringDataProcessedOrderEventRepository springDataProcessedOrderEventRepository;

    public ProcessedOrderEventPersistenceAdapter(
            SpringDataProcessedOrderEventRepository springDataProcessedOrderEventRepository
    ) {
        this.springDataProcessedOrderEventRepository = springDataProcessedOrderEventRepository;
    }

    @Override
    public boolean markAsProcessed(String eventType, UUID orderId, UUID productId) {
        Objects.requireNonNull(eventType, "Event type cannot be null.");
        Objects.requireNonNull(orderId, "Order id cannot be null.");
        Objects.requireNonNull(productId, "Product id cannot be null.");

        int insertedRowCount = springDataProcessedOrderEventRepository.insertIfNotExists(
                UUID.randomUUID(),
                eventType,
                orderId,
                productId,
                LocalDateTime.now()
        );

        return insertedRowCount == 1;
    }
}