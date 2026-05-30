package com.b2b.inventory.application.query.handler;

import com.b2b.inventory.application.port.in.GetProductsUseCase;
import com.b2b.inventory.application.port.out.PagedResult;
import com.b2b.inventory.application.port.out.ProductRepositoryPort;
import com.b2b.inventory.application.port.out.ProductSearchCriteria;
import com.b2b.inventory.application.query.dto.GetProductsQuery;
import com.b2b.inventory.application.query.dto.GetProductsResult;
import com.b2b.inventory.application.query.dto.ProductListItemResult;
import com.b2b.inventory.domain.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetProductsQueryHandler implements GetProductsUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public GetProductsQueryHandler(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductsResult handle(GetProductsQuery query) {
        validatePagination(query.page(), query.size());

        ProductSearchCriteria criteria = new ProductSearchCriteria(
                query.page(),
                query.size(),
                query.companyId(),
                query.status(),
                query.unit(),
                query.search()
        );

        PagedResult<Product> pagedProducts = productRepositoryPort.findProducts(criteria);

        List<ProductListItemResult> content = pagedProducts.content()
                .stream()
                .map(this::toListItemResult)
                .toList();

        return new GetProductsResult(
                content,
                pagedProducts.page(),
                pagedProducts.size(),
                pagedProducts.totalElements(),
                pagedProducts.totalPages()
        );
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative.");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100.");
        }
    }

    private ProductListItemResult toListItemResult(Product product) {
        return new ProductListItemResult(
                product.getId(),
                product.getCompanyId(),
                product.getSku().getValue(),
                product.getName(),
                product.getUnit(),
                product.getStatus(),
                product.getCreatedAt()
        );
    }
}