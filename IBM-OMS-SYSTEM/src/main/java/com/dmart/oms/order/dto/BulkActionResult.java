package com.dmart.oms.order.dto;

import java.util.List;

/**
 * Outcome of a bulk order action. Each item reports success or failure
 * independently so a partial batch does not fail the whole request.
 */
public record BulkActionResult(int total, int succeeded, int failed, List<Item> results) {

	/**
	 * @param orderId the target order id
	 * @param success whether the action applied
	 * @param message failure reason when not successful; null on success
	 */
	public record Item(Long orderId, boolean success, String message) {
	}
}
