package com.dmart.oms.analytics.dto;

import java.time.LocalDateTime;

//analytics/dto/KpiResponse.java
public record KpiResponse(LocalDateTime computedAt, double revenueLast24h, long ordersLast24h, double aovLast24h,
		double refundRateLast7d, double fulfillmentSlaHitPct) {
}
