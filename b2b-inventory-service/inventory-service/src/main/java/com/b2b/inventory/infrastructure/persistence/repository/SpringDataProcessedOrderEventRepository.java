package com.b2b.inventory.infrastructure.persistence.repository;

import com.b2b.inventory.infrastructure.persistence.entity.ProcessedOrderEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataProcessedOrderEventRepository extends JpaRepository<ProcessedOrderEventJpaEntity, UUID> {

    @Modifying
    @Query(
            value = """
                    INSERT INTO processed_order_events (
                        id,
                        event_type,
                        order_id,
                        product_id,
                        processed_at
                    )
                    VALUES (
                        :id,
                        :eventType,
                        :orderId,
                        :productId,
                        :processedAt
                    )
                    ON CONFLICT (event_type, order_id, product_id) DO NOTHING
                    """,
            nativeQuery = true
    )
    int insertIfNotExists(
            @Param("id") UUID id,
            @Param("eventType") String eventType,
            @Param("orderId") UUID orderId,
            @Param("productId") UUID productId,
            @Param("processedAt") LocalDateTime processedAt
    );
}