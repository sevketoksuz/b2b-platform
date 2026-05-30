package com.b2b.inventory.application.port.out;

import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    boolean existsByCompanyIdAndSku(UUID companyId, Sku sku);

    boolean existsByCompanyIdAndSkuAndIdNot(UUID companyId, Sku sku, UUID id);

    PagedResult<Product> findProducts(ProductSearchCriteria criteria);
}