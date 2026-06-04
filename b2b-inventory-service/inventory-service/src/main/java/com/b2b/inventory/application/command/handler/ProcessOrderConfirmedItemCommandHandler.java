package com.b2b.inventory.application.command.handler;

import com.b2b.inventory.application.command.dto.DecreaseStockCommand;
import com.b2b.inventory.application.command.dto.ProcessOrderConfirmedItemCommand;
import com.b2b.inventory.application.port.in.DecreaseStockUseCase;
import com.b2b.inventory.application.port.in.ProcessOrderConfirmedItemUseCase;
import com.b2b.inventory.application.port.out.ProcessedOrderEventRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessOrderConfirmedItemCommandHandler implements ProcessOrderConfirmedItemUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessOrderConfirmedItemCommandHandler.class);

    private static final String ORDER_CONFIRMED_EVENT_TYPE = "ORDER_CONFIRMED";

    private final ProcessedOrderEventRepositoryPort processedOrderEventRepositoryPort;
    private final DecreaseStockUseCase decreaseStockUseCase;

    public ProcessOrderConfirmedItemCommandHandler(
            ProcessedOrderEventRepositoryPort processedOrderEventRepositoryPort,
            DecreaseStockUseCase decreaseStockUseCase
    ) {
        this.processedOrderEventRepositoryPort = processedOrderEventRepositoryPort;
        this.decreaseStockUseCase = decreaseStockUseCase;
    }

    @Override
    @Transactional
    public void handle(ProcessOrderConfirmedItemCommand command) {
        boolean markedAsProcessed = processedOrderEventRepositoryPort.markAsProcessed(
                ORDER_CONFIRMED_EVENT_TYPE,
                command.orderId(),
                command.productId()
        );

        if (!markedAsProcessed) {
            log.info(
                    "Duplicate order confirmed item event ignored. eventType={}, orderId={}, productId={}",
                    ORDER_CONFIRMED_EVENT_TYPE,
                    command.orderId(),
                    command.productId()
            );
            return;
        }

        DecreaseStockCommand decreaseStockCommand = new DecreaseStockCommand(
                command.sellerCompanyId(),
                command.productId(),
                command.quantity(),
                "ORDER_CONFIRMED: " + command.orderId()
        );

        decreaseStockUseCase.handle(decreaseStockCommand);
    }
}