package com.b2b.company.infrastructure.persistence.repository;

import com.b2b.company.infrastructure.persistence.entity.CompanyMemberJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataCompanyMemberRepository extends
        JpaRepository<CompanyMemberJpaEntity, UUID>,
        JpaSpecificationExecutor<CompanyMemberJpaEntity> {

    boolean existsByCompanyIdAndEmail(UUID companyId, String email);

    boolean existsByCompanyIdAndEmailAndIdNot(UUID companyId, String email, UUID id);
}