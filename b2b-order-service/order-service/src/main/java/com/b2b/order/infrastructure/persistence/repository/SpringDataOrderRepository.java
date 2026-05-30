package com.b2b.order.infrastructure.persistence.repository;

import com.b2b.order.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrderRepository extends
        JpaRepository<OrderJpaEntity, UUID>,
        JpaSpecificationExecutor<OrderJpaEntity> {

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findById(UUID id);
}