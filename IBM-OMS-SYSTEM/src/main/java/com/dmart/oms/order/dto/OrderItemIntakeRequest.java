package com.dmart.oms.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * A single line item in an inbound order handed off from an upstream system
 * (e.g. the QuickBasket storefront).
 */
public record OrderItemIntakeRequest(

		@NotBlank(message = "productCode is required") String productCode,

		@Min(value = 1, message = "quantity must be at least 1") int quantity) {
}
