package com.b2b.company.infrastructure.persistence.repository;

import com.b2b.company.infrastructure.persistence.entity.CompanyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataCompanyRepository extends
        JpaRepository<CompanyJpaEntity, UUID>,
        JpaSpecificationExecutor<CompanyJpaEntity> {

    boolean existsByTaxNumber(String taxNumber);

    boolean existsByTaxNumberAndIdNot(String taxNumber, UUID id);
}