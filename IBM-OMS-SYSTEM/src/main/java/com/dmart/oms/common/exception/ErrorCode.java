package com.dmart.oms.common.exception;

public enum ErrorCode {
	 INV_001, // Inventory error
	    ORD_001, // Order not found
	    ORD_002, // Invalid order state (e.g., cannot approve cancelled order)
	    PAY_001,
	    SHP_001,
	    ANA_001,
	    VALIDATION_ERROR,
	    SERVER_ERROR
}
