package com.dmart.oms.order.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/**
 * A fulfillment request: ship the given quantities per line item. Quantities are
 * additive to what has already shipped and are validated so an item is never
 * over-shipped beyond its ordered quantity.
 */
public record FulfillmentRequest(

		@NotEmpty(message = "lines must not be empty") @Valid List<FulfillmentLine> lines) {
}
