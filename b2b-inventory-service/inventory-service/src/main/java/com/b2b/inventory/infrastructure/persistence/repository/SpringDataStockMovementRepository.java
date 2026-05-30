package com.b2b.inventory.infrastructure.persistence.repository;

import com.b2b.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataStockMovementRepository extends
        JpaRepository<StockMovementJpaEntity, UUID>,
        JpaSpecificationExecutor<StockMovementJpaEntity> {
}