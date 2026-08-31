package com.dmart.oms.order.dto;

/**
 * Outcome of an idempotent order ingestion. {@code created} is true when a new
 * order was persisted, false when an order with the same orderNumber already
 * existed and was returned unchanged.
 */
public record OrderIntakeResult(OrderDTO order, boolean created) {
}
