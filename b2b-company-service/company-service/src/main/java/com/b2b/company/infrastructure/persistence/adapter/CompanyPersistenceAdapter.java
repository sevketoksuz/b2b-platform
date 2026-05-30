package com.b2b.company.infrastructure.persistence.adapter;

import com.b2b.company.application.port.out.CompanyRepositoryPort;
import com.b2b.company.application.port.out.CompanySearchCriteria;
import com.b2b.company.application.port.out.PagedResult;
import com.b2b.company.domain.model.Company;
import com.b2b.company.domain.valueobject.TaxNumber;
import com.b2b.company.infrastructure.persistence.entity.CompanyJpaEntity;
import com.b2b.company.infrastructure.persistence.mapper.CompanyPersistenceMapper;
import com.b2b.company.infrastructure.persistence.repository.SpringDataCompanyRepository;
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
public class CompanyPersistenceAdapter implements CompanyRepositoryPort {

    private final SpringDataCompanyRepository springDataCompanyRepository;
    private final CompanyPersistenceMapper companyPersistenceMapper;

    public CompanyPersistenceAdapter(
            SpringDataCompanyRepository springDataCompanyRepository,
            CompanyPersistenceMapper companyPersistenceMapper
    ) {
        this.springDataCompanyRepository = springDataCompanyRepository;
        this.companyPersistenceMapper = companyPersistenceMapper;
    }

    @Override
    public Company save(Company company) {
        CompanyJpaEntity entity = companyPersistenceMapper.toJpaEntity(company);
        CompanyJpaEntity savedEntity = springDataCompanyRepository.save(entity);

        return companyPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return springDataCompanyRepository.findById(id)
                .map(companyPersistenceMapper::toDomainModel);
    }

    @Override
    public boolean existsByTaxNumber(TaxNumber taxNumber) {
        return springDataCompanyRepository.existsByTaxNumber(taxNumber.getValue());
    }

    @Override
    public boolean existsByTaxNumberAndIdNot(TaxNumber taxNumber, UUID id) {
        return springDataCompanyRepository.existsByTaxNumberAndIdNot(
                taxNumber.getValue(),
                id
        );
    }

    @Override
    public PagedResult<Company> findCompanies(CompanySearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CompanyJpaEntity> page = springDataCompanyRepository.findAll(
                buildSpecification(criteria),
                pageRequest
        );

        List<Company> companies = page.getContent()
                .stream()
                .map(companyPersistenceMapper::toDomainModel)
                .toList();

        return new PagedResult<>(
                companies,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<CompanyJpaEntity> buildSpecification(CompanySearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.companyType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("companyType"),
                        criteria.companyType()
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

                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        searchTerm
                );

                Predicate taxNumberPredicate = criteriaBuilder.like(
                        root.get("taxNumber"),
                        "%" + criteria.search().trim() + "%"
                );

                predicates.add(criteriaBuilder.or(namePredicate, taxNumberPredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}