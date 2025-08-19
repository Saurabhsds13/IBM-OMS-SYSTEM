package com.dmart.oms.notification.service;

public interface NotificationSender {
	void send(String eventType, String payload, Long eventId) throws Exception;
}
