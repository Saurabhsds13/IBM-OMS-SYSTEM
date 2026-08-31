package com.dmart.oms.common.event;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dmart.oms.common.kafka.KafkaTopics;
import com.dmart.oms.order.event.OrderEventBroadcaster;
import com.dmart.oms.order.event.OrderStatusEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Outbox publisher for ORDER aggregate events. Produces a compact
 * {@link OrderStatusEvent} to the {@code oms.orders.status} Kafka topic (keyed
 * by order number for per-order ordering) and fans the same event out to the
 * OMS admin UI over SSE.
 *
 * <p>Wired into the existing {@code OutboxDispatcher}, which polls all
 * {@link OutboxPublisher} beans and delegates by {@code canHandle}. This keeps
 * the reliable outbox -&gt; Kafka pattern: events are only produced after the
 * originating DB transaction has committed.
 */
@Component
public class KafkaOutboxPublisher implements OutboxPublisher {

	private static final Logger log = LoggerFactory.getLogger(KafkaOutboxPublisher.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final OrderEventBroadcaster broadcaster;
	private final ObjectMapper mapper;
	private final String statusTopic;

	public KafkaOutboxPublisher(KafkaTemplate<String, String> kafkaTemplate, OrderEventBroadcaster broadcaster,
			ObjectMapper mapper,
			@Value("${app.kafka.topic.orders-status:" + KafkaTopics.ORDERS_STATUS + "}") String statusTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.broadcaster = broadcaster;
		this.mapper = mapper;
		this.statusTopic = statusTopic;
	}

	@Override
	public boolean canHandle(String aggregateType, String eventType) {
		return "ORDER".equalsIgnoreCase(aggregateType);
	}

	@Override
	public void publish(String aggregateType, String aggregateId, String eventType, Object payload) throws Exception {
		String orderNumber = aggregateId;
		String status = OrderStatusEvent.statusForEventType(eventType);

		OrderStatusEvent event = new OrderStatusEvent(eventType, orderNumber, status, Instant.now());
		String json = mapper.writeValueAsString(event);

		// Produce to Kafka, keyed by order number so all events for one order land
		// on the same partition and stay ordered. get() surfaces send failures so
		// the dispatcher can retry / dead-letter the outbox row.
		kafkaTemplate.send(statusTopic, orderNumber, json).get();
		log.debug("Produced {} for order {} to {}", eventType, orderNumber, statusTopic);

		// Fan out to connected admin-UI SSE clients (best-effort; never fails send).
		broadcaster.broadcast(event);
	}
}
