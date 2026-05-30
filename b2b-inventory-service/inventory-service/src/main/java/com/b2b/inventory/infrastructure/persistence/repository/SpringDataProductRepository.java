package com.b2b.inventory.infrastructure.persistence.repository;

import com.b2b.inventory.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataProductRepository extends
        JpaRepository<ProductJpaEntity, UUID>,
        JpaSpecificationExecutor<ProductJpaEntity> {

    boolean existsByCompanyIdAndSku(UUID companyId, String sku);

    boolean existsByCompanyIdAndSkuAndIdNot(UUID companyId, String sku, UUID id);
}