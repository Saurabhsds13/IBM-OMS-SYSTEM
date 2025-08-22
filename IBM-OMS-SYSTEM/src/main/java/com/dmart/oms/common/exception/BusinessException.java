package com.dmart.oms.common.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;

	public BusinessException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
