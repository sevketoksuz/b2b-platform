package com.b2b.inventory.api.controller;

import com.b2b.inventory.api.mapper.StockApiMapper;
import com.b2b.inventory.api.request.AdjustStockRequest;
import com.b2b.inventory.api.request.DecreaseStockRequest;
import com.b2b.inventory.api.request.IncreaseStockRequest;
import com.b2b.inventory.api.response.StockResponse;
import com.b2b.inventory.application.command.dto.AdjustStockCommand;
import com.b2b.inventory.application.command.dto.ChangeStockResult;
import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.command.dto.IncreaseStockCommand;
import com.b2b.inventory.application.port.in.AdjustStockUseCase;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.application.port.in.GetStockByProductIdUseCase;
import com.b2b.inventory.application.port.in.IncreaseStockUseCase;
import com.b2b.inventory.application.query.dto.GetStockByProductIdQuery;
import com.b2b.inventory.application.query.dto.GetStockByProductIdResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StockController {

    private final GetStockByProductIdUseCase getStockByProductIdUseCase;
    private final IncreaseStockUseCase increaseStockUseCase;
    private final DecreaseStockUseCase decreaseStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;
    private final StockApiMapper stockApiMapper;

    public StockController(
            GetStockByProductIdUseCase getStockByProductIdUseCase,
            IncreaseStockUseCase increaseStockUseCase,
            DecreaseStockUseCase decreaseStockUseCase,
            AdjustStockUseCase adjustStockUseCase,
            StockApiMapper stockApiMapper
    ) {
        this.getStockByProductIdUseCase = getStockByProductIdUseCase;
        this.increaseStockUseCase = increaseStockUseCase;
        this.decreaseStockUseCase = decreaseStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
        this.stockApiMapper = stockApiMapper;
    }

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<StockResponse> getStockByProductId(
            @PathVariable UUID productId
    ) {
        GetStockByProductIdQuery query = new GetStockByProductIdQuery(productId);

        GetStockByProductIdResult result = getStockByProductIdUseCase.handle(query);

        StockResponse response = stockApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{productId}/stock/increase")
    public ResponseEntity<StockResponse> increaseStock(
            @PathVariable UUID productId,
            @Valid @RequestBody IncreaseStockRequest request
    ) {
        IncreaseStockCommand command = stockApiMapper.toCommand(productId, request);

        ChangeStockResult result = increaseStockUseCase.handle(command);

        StockResponse response = stockApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{productId}/stock/decrease")
    public ResponseEntity<StockResponse> decreaseStock(
            @PathVariable UUID productId,
            @Valid @RequestBody DecreaseStockRequest request
    ) {
        DecreaseStockCommand command = stockApiMapper.toCommand(productId, request);

        ChangeStockResult result = decreaseStockUseCase.handle(command);

        StockResponse response = stockApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/{productId}/stock/adjust")
    public ResponseEntity<StockResponse> adjustStock(
            @PathVariable UUID productId,
            @Valid @RequestBody AdjustStockRequest request
    ) {
        AdjustStockCommand command = stockApiMapper.toCommand(productId, request);

        ChangeStockResult result = adjustStockUseCase.handle(command);

        StockResponse response = stockApiMapper.toResponse(result);

        return ResponseEntity.ok(response);
    }
}