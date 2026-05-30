package com.b2b.inventory.infrastructure.persistence.mapper;

import com.b2b.inventory.domain.model.Product;
import com.b2b.inventory.domain.valueobject.Sku;
import com.b2b.inventory.infrastructure.persistence.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductPersistenceMapper {

    public ProductJpaEntity toJpaEntity(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getCompanyId(),
                product.getSku().getValue(),
                product.getName(),
                product.getDescription(),
                product.getUnit(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public Product toDomainModel(ProductJpaEntity entity) {
        return Product.restore(
                entity.getId(),
                entity.getCompanyId(),
                Sku.of(entity.getSku()),
                entity.getName(),
                entity.getDescription(),
                entity.getUnit(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}