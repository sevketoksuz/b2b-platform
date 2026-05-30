package com.b2b.inventory.infrastructure.persistence.repository;

import com.b2b.inventory.infrastructure.persistence.entity.StockJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataStockRepository extends JpaRepository<StockJpaEntity, UUID> {

    Optional<StockJpaEntity> findByProductId(UUID productId);

    Optional<StockJpaEntity> findByCompanyIdAndProductId(UUID companyId, UUID productId);

    boolean existsByCompanyIdAndProductId(UUID companyId, UUID productId);
}