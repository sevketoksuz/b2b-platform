package com.b2b.company.infrastructure.persistence.adapter;

import com.b2b.company.application.port.out.CompanyMemberRepositoryPort;
import com.b2b.company.domain.model.CompanyMember;
import com.b2b.company.domain.valueobject.Email;
import com.b2b.company.infrastructure.persistence.entity.CompanyMemberJpaEntity;
import com.b2b.company.infrastructure.persistence.mapper.CompanyMemberPersistenceMapper;
import com.b2b.company.infrastructure.persistence.repository.SpringDataCompanyMemberRepository;
import com.b2b.company.application.port.out.CompanyMemberSearchCriteria;
import com.b2b.company.application.port.out.PagedResult;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyMemberPersistenceAdapter implements CompanyMemberRepositoryPort {

    private final SpringDataCompanyMemberRepository springDataCompanyMemberRepository;
    private final CompanyMemberPersistenceMapper companyMemberPersistenceMapper;

    public CompanyMemberPersistenceAdapter(
            SpringDataCompanyMemberRepository springDataCompanyMemberRepository,
            CompanyMemberPersistenceMapper companyMemberPersistenceMapper
    ) {
        this.springDataCompanyMemberRepository = springDataCompanyMemberRepository;
        this.companyMemberPersistenceMapper = companyMemberPersistenceMapper;
    }

    @Override
    public CompanyMember save(CompanyMember companyMember) {
        CompanyMemberJpaEntity entity = companyMemberPersistenceMapper.toJpaEntity(companyMember);
        CompanyMemberJpaEntity savedEntity = springDataCompanyMemberRepository.save(entity);

        return companyMemberPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<CompanyMember> findById(UUID id) {
        return springDataCompanyMemberRepository.findById(id)
                .map(companyMemberPersistenceMapper::toDomainModel);
    }

    @Override
    public boolean existsByCompanyIdAndEmail(UUID companyId, Email email) {
        return springDataCompanyMemberRepository.existsByCompanyIdAndEmail(
                companyId,
                email.getValue()
        );
    }

    @Override
    public PagedResult<CompanyMember> findCompanyMembers(CompanyMemberSearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CompanyMemberJpaEntity> page = springDataCompanyMemberRepository.findAll(
                buildSpecification(criteria),
                pageRequest
        );

        List<CompanyMember> members = page.getContent()
                .stream()
                .map(companyMemberPersistenceMapper::toDomainModel)
                .toList();

        return new PagedResult<>(
                members,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<CompanyMemberJpaEntity> buildSpecification(
            CompanyMemberSearchCriteria criteria
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(
                    root.get("companyId"),
                    criteria.companyId()
            ));

            if (criteria.role() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("role"),
                        criteria.role()
                ));
            }

            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.status()
                ));
            }

            if (criteria.search() != null && !criteria.search().isBlank()) {
                String searchTerm = "%" + criteria.search().trim().toLowerCase() + "%";

                Predicate fullNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("fullName")),
                        searchTerm
                );

                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        searchTerm
                );

                predicates.add(criteriaBuilder.or(fullNamePredicate, emailPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public boolean existsByCompanyIdAndEmailAndIdNot(UUID companyId, Email email, UUID id) {
        return springDataCompanyMemberRepository.existsByCompanyIdAndEmailAndIdNot(
                companyId,
                email.getValue(),
                id
        );
    }
}