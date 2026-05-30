package com.b2b.company.infrastructure.persistence.mapper;

import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import com.b2b.company.infrastructure.persistence.entity.CompanyMemberJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CompanyMemberPersistenceMapper {

    public CompanyMemberJpaEntity toJpaEntity(CompanyMember companyMember) {
        return new CompanyMemberJpaEntity(
                companyMember.getId(),
                companyMember.getCompanyId(),
                companyMember.getFullName(),
                companyMember.getEmail().getValue(),
                companyMember.getRole(),
                companyMember.getStatus(),
                companyMember.getCreatedAt(),
                companyMember.getUpdatedAt()
        );
    }

    public CompanyMember toDomainModel(CompanyMemberJpaEntity entity) {
        return CompanyMember.restore(
                entity.getId(),
                entity.getCompanyId(),
                entity.getFullName(),
                Email.of(entity.getEmail()),
                entity.getRole(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}