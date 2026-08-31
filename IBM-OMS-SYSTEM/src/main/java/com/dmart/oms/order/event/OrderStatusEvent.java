package com.dmart.oms.order.event;

import java.time.Instant;

/**
 * Cross-system order status event, produced by OMS to {@code oms.orders.status}
 * and also fanned out to the OMS admin UI over SSE.
 *
 * @param eventType  e.g. ORDER_PLACED, ORDER_APPROVED, ORDER_CANCELLED,
 *                   ORDER_PARTIALLY_SHIPPED, ORDER_SHIPPED
 * @param orderNumber correlation key across systems
 * @param status     resulting order status
 * @param occurredAt when the change happened
 */
public record OrderStatusEvent(String eventType, String orderNumber, String status, Instant occurredAt) {

	/** Maps an outbox event type to the resulting order status. */
	public static String statusForEventType(String eventType) {
		return switch (eventType) {
			case "ORDER_PLACED" -> "PENDING";
			case "ORDER_APPROVED" -> "APPROVED";
			case "ORDER_CANCELLED" -> "CANCELLED";
			case "ORDER_PARTIALLY_SHIPPED" -> "PARTIALLY_SHIPPED";
			case "ORDER_SHIPPED" -> "SHIPPED";
			default -> "UNKNOWN";
		};
	}
}
