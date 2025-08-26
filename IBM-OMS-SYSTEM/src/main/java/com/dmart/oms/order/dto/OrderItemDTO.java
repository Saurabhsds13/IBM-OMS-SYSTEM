package com.dmart.oms.order.dto;

public record OrderItemDTO(String productCode, int quantity, int shippedQuantity) {
}
