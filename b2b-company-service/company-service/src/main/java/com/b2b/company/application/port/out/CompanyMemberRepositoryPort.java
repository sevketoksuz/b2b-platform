package com.b2b.company.application.port.out;

import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface CompanyMemberRepositoryPort {

    CompanyMember save(CompanyMember companyMember);

    Optional<CompanyMember> findById(UUID id);

    boolean existsByCompanyIdAndEmail(UUID companyId, Email email);

    boolean existsByCompanyIdAndEmailAndIdNot(UUID companyId, Email email, UUID id);

    PagedResult<CompanyMember> findCompanyMembers(CompanyMemberSearchCriteria criteria);
}