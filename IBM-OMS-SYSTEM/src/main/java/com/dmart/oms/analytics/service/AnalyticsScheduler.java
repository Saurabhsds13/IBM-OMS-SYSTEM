package com.dmart.oms.analytics.service;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

//analytics/service/AnalyticsScheduler.java
@EnableScheduling
@Service
public class AnalyticsScheduler {
	private final AnalyticsService svc;

	public AnalyticsScheduler(AnalyticsService s) {
		this.svc = s;
	}

	@Scheduled(cron = "0 5 0 * * *") // nightly rollup @ 00:05
	public void nightlyRollup() {
		svc.rebuildDailyAgg(LocalDate.now().minusDays(1));
	}

	@Scheduled(fixedRate = 15 * 60 * 1000) // every 15 minutes
	public void refreshKpis() {
		svc.computeKpis(Duration.ofHours(24));
	}
}
