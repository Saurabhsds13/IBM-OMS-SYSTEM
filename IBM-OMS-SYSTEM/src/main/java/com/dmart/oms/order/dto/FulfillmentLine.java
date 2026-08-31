package com.dmart.oms.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * A request to ship a specific quantity of one product code on an order.
 */
public record FulfillmentLine(

		@NotBlank(message = "productCode is required") String productCode,

		@Min(value = 1, message = "quantity must be at least 1") int quantity) {
}
