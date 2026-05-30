package com.b2b.order.infrastructure.client.inventory;

import com.b2b.order.application.exception.InventoryClientException;
import com.b2b.order.application.port.out.InventoryClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class InventoryRestClientAdapter implements InventoryClientPort {

    private final RestClient restClient;

    public InventoryRestClientAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${clients.inventory-service.base-url}") String inventoryServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(inventoryServiceBaseUrl)
                .build();
    }

    @Override
    public void decreaseStock(
            UUID companyId,
            UUID productId,
            BigDecimal quantity,
            String reason
    ) {
        DecreaseStockRequest request = new DecreaseStockRequest(
                companyId,
                quantity,
                reason
        );

        try {
            restClient.post()
                    .uri("/api/v1/products/{productId}/stock/decrease", productId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new InventoryClientException(
                    "Inventory Service rejected stock decrease request. Status: "
                            + exception.getStatusCode()
                            + ", body: "
                            + exception.getResponseBodyAsString(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new InventoryClientException(
                    "Inventory Service stock decrease request failed.",
                    exception
            );
        }
    }
}