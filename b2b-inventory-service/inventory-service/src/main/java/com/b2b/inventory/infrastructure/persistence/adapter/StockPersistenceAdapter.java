package com.b2b.inventory.infrastructure.persistence.adapter;

import com.b2b.inventory.application.port.out.StockRepositoryPort;
import com.b2b.inventory.domain.model.Stock;
import com.b2b.inventory.infrastructure.persistence.entity.StockJpaEntity;
import com.b2b.inventory.infrastructure.persistence.mapper.StockPersistenceMapper;
import com.b2b.inventory.infrastructure.persistence.repository.SpringDataStockRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StockPersistenceAdapter implements StockRepositoryPort {

    private final SpringDataStockRepository springDataStockRepository;
    private final StockPersistenceMapper stockPersistenceMapper;

    public StockPersistenceAdapter(
            SpringDataStockRepository springDataStockRepository,
            StockPersistenceMapper stockPersistenceMapper
    ) {
        this.springDataStockRepository = springDataStockRepository;
        this.stockPersistenceMapper = stockPersistenceMapper;
    }

    @Override
    public Stock save(Stock stock) {
        StockJpaEntity entity = stockPersistenceMapper.toJpaEntity(stock);
        StockJpaEntity savedEntity = springDataStockRepository.save(entity);

        return stockPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Stock> findByProductId(UUID productId) {
        return springDataStockRepository.findByProductId(productId)
                .map(stockPersistenceMapper::toDomainModel);
    }

    @Override
    public Optional<Stock> findByCompanyIdAndProductId(UUID companyId, UUID productId) {
        return springDataStockRepository.findByCompanyIdAndProductId(companyId, productId)
                .map(stockPersistenceMapper::toDomainModel);
    }

    @Override
    public boolean existsByCompanyIdAndProductId(UUID companyId, UUID productId) {
        return springDataStockRepository.existsByCompanyIdAndProductId(companyId, productId);
    }
}