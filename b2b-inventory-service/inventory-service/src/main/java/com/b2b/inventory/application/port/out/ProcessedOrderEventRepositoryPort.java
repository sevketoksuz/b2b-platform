package com.b2b.inventory.application.port.out;

import java.util.UUID;

public interface ProcessedOrderEventRepositoryPort {

    boolean markAsProcessed(String eventType, UUID orderId, UUID productId);

}