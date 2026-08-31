package com.dmart.oms.common.kafka;

/**
 * Shared Kafka topic names for the QuickBasket &lt;-&gt; OMS integration. These
 * names are part of the cross-system contract and must match on both sides.
 *
 * <p>These constants are the <em>contract defaults</em>. At runtime the names
 * are resolved from the {@code app.kafka.topic.*} properties (which default to
 * these values), so they can be overridden per environment for config parity
 * with QuickBasket.
 */
public final class KafkaTopics {

	/** Orders placed by the storefront; OMS consumes to ingest. */
	public static final String ORDERS_INBOUND = "oms.orders.inbound";

	/** Order status changes produced by OMS; QuickBasket (and OMS SSE) consume. */
	public static final String ORDERS_STATUS = "oms.orders.status";

	private KafkaTopics() {
	}
}
