package com.dmart.oms.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Inbound order payload accepted from an upstream system (e.g. the QuickBasket
 * storefront) when an order is placed. {@code orderNumber} is the correlation
 * key shared across both systems and is used to make ingestion idempotent.
 */
public record OrderIntakeRequest(

		@NotBlank(message = "orderNumber is required") String orderNumber,

		@NotEmpty(message = "items must not be empty") @Valid List<OrderItemIntakeRequest> items) {
}
