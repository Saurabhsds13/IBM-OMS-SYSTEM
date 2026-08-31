package com.dmart.oms.order.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * A request to apply the same action to multiple orders in one call.
 *
 * @param action   the action to apply (APPROVE or CANCEL)
 * @param orderIds the target order ids
 */
public record BulkOrderActionRequest(

		@NotNull(message = "action is required") BulkAction action,

		@NotEmpty(message = "orderIds must not be empty") List<Long> orderIds) {

	public enum BulkAction {
		APPROVE, CANCEL
	}
}
