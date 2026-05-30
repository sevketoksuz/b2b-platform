package com.b2b.company.infrastructure.persistence.mapper;

import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import com.b2b.company.infrastructure.persistence.entity.CompanyJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CompanyPersistenceMapper {

    public CompanyJpaEntity toJpaEntity(Company company) {
        return new CompanyJpaEntity(
                company.getId(),
                company.getName(),
                company.getTaxNumber().getValue(),
                company.getCompanyType(),
                company.getStatus(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }

    public Company toDomainModel(CompanyJpaEntity entity) {
        return Company.restore(
                entity.getId(),
                entity.getName(),
                TaxNumber.of(entity.getTaxNumber()),
                entity.getCompanyType(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}