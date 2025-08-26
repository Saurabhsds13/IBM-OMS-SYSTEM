package com.dmart.oms.common.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private String errorCode; // optional error code for failures
	private LocalDateTime timestamp;

	private ApiResponse(boolean success, String message, T data, String errorCode) {
		this.success = success;
		this.message = message;
		this.data = data;
		this.errorCode = errorCode;
		this.timestamp = LocalDateTime.now();
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, "Success", data, null);
	}

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(true, message, data, null);
	}

	public static <T> ApiResponse<T> failure(String message, String errorCode) {
		return new ApiResponse<>(false, message, null, errorCode);
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ApiResponse [success=" + success + ", message=" + message + ", data=" + data + ", errorCode="
				+ errorCode + ", timestamp=" + timestamp + "]";
	}

	public ApiResponse() {
		super();
	}
}