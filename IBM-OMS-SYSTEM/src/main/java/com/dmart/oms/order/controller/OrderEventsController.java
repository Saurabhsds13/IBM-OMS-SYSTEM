package com.dmart.oms.order.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.dmart.oms.order.event.OrderEventBroadcaster;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Server-Sent Events stream of order status changes for the admin UI. The
 * browser subscribes with {@code EventSource}; each order lifecycle change is
 * pushed as an {@code order-status} event.
 *
 * <p>Note: {@code EventSource} cannot send an Authorization header, so this
 * stream path is permitted without a bearer token in the security config. It
 * carries only non-sensitive order status notifications (order number + status),
 * not full order data.
 */
@RestController
@RequestMapping("/api/v1/admin/orders")
public class OrderEventsController {

	private final OrderEventBroadcaster broadcaster;

	public OrderEventsController(OrderEventBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}

	@Operation(summary = "SSE stream of live order status changes")
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream() {
		return broadcaster.register();
	}
}
