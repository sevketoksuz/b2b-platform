package com.b2b.inventory.infrastructure.persistence.adapter;

import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.StockMovementRepositoryPort;
import com.b2b.inventory.application.port.out.StockMovementSearchCriteria;
import com.b2b.inventory.domain.model.StockMovement;
import com.b2b.inventory.infrastructure.persistence.entity.StockMovementJpaEntity;
import com.b2b.inventory.infrastructure.persistence.mapper.StockMovementPersistenceMapper;
import com.b2b.inventory.infrastructure.persistence.repository.SpringDataStockMovementRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StockMovementPersistenceAdapter implements StockMovementRepositoryPort {

    private final SpringDataStockMovementRepository springDataStockMovementRepository;
    private final StockMovementPersistenceMapper stockMovementPersistenceMapper;

    public StockMovementPersistenceAdapter(
            SpringDataStockMovementRepository springDataStockMovementRepository,
            StockMovementPersistenceMapper stockMovementPersistenceMapper
    ) {
        this.springDataStockMovementRepository = springDataStockMovementRepository;
        this.stockMovementPersistenceMapper = stockMovementPersistenceMapper;
    }

    @Override
    public StockMovement save(StockMovement stockMovement) {
        StockMovementJpaEntity entity = stockMovementPersistenceMapper.toJpaEntity(stockMovement);
        StockMovementJpaEntity savedEntity = springDataStockMovementRepository.save(entity);

        return stockMovementPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public PagedResult<StockMovement> findStockMovements(StockMovementSearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<StockMovementJpaEntity> page = springDataStockMovementRepository.findAll(
                buildSpecification(criteria),
                pageRequest
        );

        List<StockMovement> stockMovements = page.getContent()
                .stream()
                .map(stockMovementPersistenceMapper::toDomainModel)
                .toList();

        return new PagedResult<>(
                stockMovements,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<StockMovementJpaEntity> buildSpecification(
            StockMovementSearchCriteria criteria
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.companyId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("companyId"),
                        criteria.companyId()
                ));
            }

            if (criteria.productId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("productId"),
                        criteria.productId()
                ));
            }

            if (criteria.movementType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("movementType"),
                        criteria.movementType()
                ));
            }

            if (criteria.fromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        criteria.fromDate()
                ));
            }

            if (criteria.toDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        criteria.toDate()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}