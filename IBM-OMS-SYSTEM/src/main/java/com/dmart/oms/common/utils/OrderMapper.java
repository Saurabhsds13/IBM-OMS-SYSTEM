package com.dmart.oms.common.utils;

import com.dmart.oms.order.dto.OrderDTO;
import com.dmart.oms.order.dto.OrderItemDTO;
import com.dmart.oms.order.model.Order;
import com.dmart.oms.order.model.OrderItem;

public class OrderMapper {

	public static OrderDTO toDTO(Order order) {
		
		if (order == null)
			return null;

		return new OrderDTO(order.getOrderNumber(), order.getStatus(), order.getCreatedAt(),
				order.getItems().stream().map(OrderMapper::toDTO).toList());
	}

	public static OrderItemDTO toDTO(OrderItem item) {
		if (item == null)
			return null;

		return new OrderItemDTO(item.getProductCode(), item.getQuantity(), item.getShippedQuantity());
	}
}
