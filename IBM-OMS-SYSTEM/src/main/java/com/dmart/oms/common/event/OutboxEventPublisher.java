package com.dmart.oms.common.event;

public interface OutboxEventPublisher {
	void publish(String aggregateType, String aggregateId, String eventType, Object payload);
}
