package com.b2b.inventory.infrastructure.persistence.adapter;

import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.ProductSearchCriteria;
import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import com.b2b.inventory.infrastructure.persistence.entity.ProductJpaEntity;
import com.b2b.inventory.infrastructure.persistence.mapper.ProductPersistenceMapper;
import com.b2b.inventory.infrastructure.persistence.repository.SpringDataProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataProductRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    public ProductPersistenceAdapter(
            SpringDataProductRepository springDataProductRepository,
            ProductPersistenceMapper productPersistenceMapper
    ) {
        this.springDataProductRepository = springDataProductRepository;
        this.productPersistenceMapper = productPersistenceMapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = productPersistenceMapper.toJpaEntity(product);
        ProductJpaEntity savedEntity = springDataProductRepository.save(entity);

        return productPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return springDataProductRepository.findById(id)
                .map(productPersistenceMapper::toDomainModel);
    }

    @Override
    public boolean existsByCompanyIdAndSku(UUID companyId, Sku sku) {
        return springDataProductRepository.existsByCompanyIdAndSku(
                companyId,
                sku.getValue()
        );
    }

    @Override
    public boolean existsByCompanyIdAndSkuAndIdNot(UUID companyId, Sku sku, UUID id) {
        return springDataProductRepository.existsByCompanyIdAndSkuAndIdNot(
                companyId,
                sku.getValue(),
                id
        );
    }

    @Override
    public PagedResult<Product> findProducts(ProductSearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ProductJpaEntity> page = springDataProductRepository.findAll(
                buildSpecification(criteria),
                pageRequest
        );

        List<Product> products = page.getContent()
                .stream()
                .map(productPersistenceMapper::toDomainModel)
                .toList();

        return new PagedResult<>(
                products,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<ProductJpaEntity> buildSpecification(ProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.companyId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("companyId"),
                        criteria.companyId()
                ));
            }

            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.status()
                ));
            }

            if (criteria.unit() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("unit"),
                        criteria.unit()
                ));
            }

            if (criteria.search() != null && !criteria.search().isBlank()) {
                String searchTerm = "%" + criteria.search().trim().toLowerCase() + "%";

                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        searchTerm
                );

                Predicate skuPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("sku")),
                        searchTerm
                );

                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        searchTerm
                );

                predicates.add(criteriaBuilder.or(
                        namePredicate,
                        skuPredicate,
                        descriptionPredicate
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}