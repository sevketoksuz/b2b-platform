package com.b2b.order.infrastructure.persistence.adapter;

import com.b2b.order.application.port.out.OrderRepositoryPort;
import com.b2b.order.application.port.out.OrderSearchCriteria;
import com.b2b.order.application.port.out.PagedResult;
import com.b2b.order.domain.model.Order;
import com.b2b.order.infrastructure.persistence.entity.OrderJpaEntity;
import com.b2b.order.infrastructure.persistence.mapper.OrderPersistenceMapper;
import com.b2b.order.infrastructure.persistence.repository.SpringDataOrderRepository;
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
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    public OrderPersistenceAdapter(
            SpringDataOrderRepository springDataOrderRepository,
            OrderPersistenceMapper orderPersistenceMapper
    ) {
        this.springDataOrderRepository = springDataOrderRepository;
        this.orderPersistenceMapper = orderPersistenceMapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderPersistenceMapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = springDataOrderRepository.save(entity);

        return orderPersistenceMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataOrderRepository.findById(id)
                .map(orderPersistenceMapper::toDomainModel);
    }

    @Override
    public PagedResult<Order> findOrders(OrderSearchCriteria criteria) {
        PageRequest pageRequest = PageRequest.of(
                criteria.page(),
                criteria.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<OrderJpaEntity> page = springDataOrderRepository.findAll(
                buildSpecification(criteria),
                pageRequest
        );

        List<Order> orders = page.getContent()
                .stream()
                .map(orderPersistenceMapper::toDomainModel)
                .toList();

        return new PagedResult<>(
                orders,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<OrderJpaEntity> buildSpecification(OrderSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.buyerCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("buyerCompanyId"),
                        criteria.buyerCompanyId()
                ));
            }

            if (criteria.sellerCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("sellerCompanyId"),
                        criteria.sellerCompanyId()
                ));
            }

            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.status()
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