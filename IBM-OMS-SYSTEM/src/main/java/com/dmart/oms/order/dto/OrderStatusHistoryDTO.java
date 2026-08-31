package com.dmart.oms.order.dto;

import java.time.Instant;

public record OrderStatusHistoryDTO(
		Long id,
		String fromStatus,
		String toStatus,
		String changedBy,
		Instant changedAt) {
}
