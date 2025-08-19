package com.dmart.oms.analytics.dto;

import java.time.LocalDate;

//analytics/dto/TimeseriesPoint.java
public record TimeseriesPoint(LocalDate date, double revenue, long orders, double aov, long cancellations,
		long backorders) {
}
