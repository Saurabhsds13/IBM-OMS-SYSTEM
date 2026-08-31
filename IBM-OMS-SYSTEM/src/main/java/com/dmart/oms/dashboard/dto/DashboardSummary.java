package com.dmart.oms.dashboard.dto;

import java.util.Map;

import com.dmart.oms.analytics.dto.KpiResponse;

/**
 * Aggregated operational snapshot for the admin dashboard, assembled in a single
 * call so the UI does not have to stitch together multiple endpoints or compute
 * counts client-side.
 *
 * @param totalOrders       total number of orders in the system
 * @param ordersByStatus    map of order status -> count (e.g. PENDING, APPROVED)
 * @param inventorySkuCount total number of inventory SKUs
 * @param lowStockCount     number of SKUs at or below the low-stock threshold
 * @param lowStockThreshold the threshold used to compute lowStockCount
 * @param pendingOutbox     number of outbox events awaiting dispatch
 * @param latestKpi         most recent KPI snapshot, or null if none computed
 */
public record DashboardSummary(
		long totalOrders,
		Map<String, Long> ordersByStatus,
		long inventorySkuCount,
		long lowStockCount,
		int lowStockThreshold,
		long pendingOutbox,
		KpiResponse latestKpi) {
}
