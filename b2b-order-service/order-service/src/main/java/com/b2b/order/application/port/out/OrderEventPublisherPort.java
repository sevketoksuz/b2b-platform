package com.b2b.order.application.port.out;

import com.b2b.order.domain.model.Order;

public interface OrderEventPublisherPort {

    void publishOrderConfirmed(Order order);

}