package com.b2b.inventory.application.port.out;

import com.b2b.inventory.domain.model.Stock;

import java.util.Optional;
import java.util.UUID;

public interface StockRepositoryPort {

    Stock save(Stock stock);

    Optional<Stock> findByProductId(UUID productId);

    Optional<Stock> findByCompanyIdAndProductId(UUID companyId, UUID productId);

    boolean existsByCompanyIdAndProductId(UUID companyId, UUID productId);
}