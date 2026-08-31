package com.dmart.oms.common.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the integration topics so they are auto-created on a broker that
 * allows it. Topic names are externalized (see application.properties) for
 * config parity with QuickBasket; defaults match the shared contract. On
 * managed brokers where auto-creation is disabled, these are provisioned out of
 * band with matching names.
 */
@Configuration
public class KafkaTopicConfig {

	@Value("${app.kafka.topic.orders-inbound:" + KafkaTopics.ORDERS_INBOUND + "}")
	private String ordersInbound;

	@Value("${app.kafka.topic.orders-status:" + KafkaTopics.ORDERS_STATUS + "}")
	private String ordersStatus;

	@Bean
	public NewTopic ordersInboundTopic() {
		return TopicBuilder.name(ordersInbound).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic ordersStatusTopic() {
		return TopicBuilder.name(ordersStatus).partitions(3).replicas(1).build();
	}
}
