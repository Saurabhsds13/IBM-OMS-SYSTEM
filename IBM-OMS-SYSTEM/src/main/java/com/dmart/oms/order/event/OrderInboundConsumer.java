package com.dmart.oms.order.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dmart.oms.common.kafka.KafkaTopics;
import com.dmart.oms.order.dto.OrderIntakeRequest;
import com.dmart.oms.order.dto.OrderIntakeResult;
import com.dmart.oms.order.dto.OrderItemIntakeRequest;
import com.dmart.oms.order.service.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Consumes orders placed by the storefront from {@code oms.orders.inbound} and
 * ingests them via {@link OrderService#ingestOrder}. Ingestion is idempotent on
 * order number, so Kafka's at-least-once delivery (redeliveries) is safe.
 */
@Component
public class OrderInboundConsumer {

	private static final Logger log = LoggerFactory.getLogger(OrderInboundConsumer.class);

	private final OrderService orderService;
	private final ObjectMapper mapper;

	public OrderInboundConsumer(OrderService orderService, ObjectMapper mapper) {
		this.orderService = orderService;
		this.mapper = mapper;
	}

	@KafkaListener(topics = "${app.kafka.topic.orders-inbound:oms.orders.inbound}", groupId = "${spring.kafka.consumer.group-id:oms}")
	public void onInbound(String message) {
		try {
			InboundOrder inbound = mapper.readValue(message, InboundOrder.class);
			if (inbound.orderNumber() == null || inbound.items() == null || inbound.items().isEmpty()) {
				log.warn("Discarding invalid inbound order message: {}", message);
				return;
			}

			List<OrderItemIntakeRequest> items = inbound.items().stream()
					.map(i -> new OrderItemIntakeRequest(i.productCode(), i.quantity()))
					.toList();

			OrderIntakeResult result = orderService.ingestOrder(new OrderIntakeRequest(inbound.orderNumber(), items));
			log.info("Ingested inbound order {} (created={})", inbound.orderNumber(), result.created());
		} catch (Exception ex) {
			// Log and swallow so a single poison message doesn't stall the partition.
			// A real deployment would route to a dead-letter topic here.
			log.error("Failed to process inbound order message: {}", message, ex);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record InboundOrder(String eventType, String orderNumber, List<InboundItem> items, String occurredAt) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	record InboundItem(String productCode, int quantity) {
	}
}
