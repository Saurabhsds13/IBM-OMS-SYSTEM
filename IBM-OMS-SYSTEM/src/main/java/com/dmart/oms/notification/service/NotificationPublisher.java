package com.dmart.oms.notification.service;

import org.springframework.stereotype.Component;

import com.dmart.oms.common.event.OutboxPublisher;

@Component
public class NotificationPublisher implements OutboxPublisher {

	private final NotificationSender sender;

	public NotificationPublisher(NotificationSender sender) {
		this.sender = sender;
	}

	@Override
	public boolean canHandle(String aggregateType, String eventType) {
		return "NOTIFICATION".equalsIgnoreCase(aggregateType);
	}

	@Override
	public void publish(String aggregateType, String aggregateId, String eventType, Object payload) throws Exception {
		sender.send(eventType, payload.toString(), Long.valueOf(aggregateId));
	}
}
