package com.dmart.oms.dashboard.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.Inventory.repository.InventoryRepository;
import com.dmart.oms.analytics.dto.KpiResponse;
import com.dmart.oms.analytics.service.AnalyticsService;
import com.dmart.oms.common.repository.OutboxRepository;
import com.dmart.oms.dashboard.dto.DashboardSummary;
import com.dmart.oms.order.repository.OrderRepository;

/**
 * Assembles the dashboard summary from the order, inventory, outbox, and
 * analytics sources.
 */
@Service
public class DashboardService {

	/** SKUs with available quantity at or below this are considered low stock. */
	static final int LOW_STOCK_THRESHOLD = 10;

	private final OrderRepository orderRepository;
	private final InventoryRepository inventoryRepository;
	private final OutboxRepository outboxRepository;
	private final AnalyticsService analyticsService;

	public DashboardService(OrderRepository orderRepository, InventoryRepository inventoryRepository,
			OutboxRepository outboxRepository, AnalyticsService analyticsService) {
		this.orderRepository = orderRepository;
		this.inventoryRepository = inventoryRepository;
		this.outboxRepository = outboxRepository;
		this.analyticsService = analyticsService;
	}

	@Transactional(readOnly = true)
	public DashboardSummary buildSummary() {
		Map<String, Long> ordersByStatus = new LinkedHashMap<>();
		long totalOrders = 0;
		for (Object[] row : orderRepository.countGroupedByStatus()) {
			String status = row[0] == null ? "UNKNOWN" : row[0].toString();
			long count = ((Number) row[1]).longValue();
			ordersByStatus.put(status, count);
			totalOrders += count;
		}

		long inventorySkuCount = inventoryRepository.count();
		long lowStockCount = inventoryRepository.countByAvailableQtyLessThanEqual(LOW_STOCK_THRESHOLD);
		long pendingOutbox = outboxRepository.countByStatus("PENDING");
		KpiResponse latestKpi = analyticsService.getLatestKpi().orElse(null);

		return new DashboardSummary(
				totalOrders,
				ordersByStatus,
				inventorySkuCount,
				lowStockCount,
				LOW_STOCK_THRESHOLD,
				pendingOutbox,
				latestKpi);
	}
}
