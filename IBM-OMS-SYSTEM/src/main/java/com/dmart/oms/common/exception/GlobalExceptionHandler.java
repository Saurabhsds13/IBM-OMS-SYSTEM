package com.dmart.oms.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dmart.oms.common.dto.ErrorResponse;
import com.dmart.oms.security.exception.AccountLockedException;
import com.dmart.oms.security.exception.AuthenticationFailedException;
import com.dmart.oms.security.exception.InvalidRoleException;
import com.dmart.oms.security.exception.UsernameAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ErrorResponse> handleAuthFailed(AuthenticationFailedException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), "UNAUTHORIZED", status.value(),
				request.getRequestURI(), null));
	}

	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.LOCKED;
		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), "ACCOUNT_LOCKED", status.value(),
				request.getRequestURI(), null));
	}

	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUsernameExists(UsernameAlreadyExistsException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.CONFLICT;
		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), "USERNAME_EXISTS", status.value(),
				request.getRequestURI(), null));
	}

	@ExceptionHandler(InvalidRoleException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRole(InvalidRoleException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), "INVALID_ROLE", status.value(),
				request.getRequestURI(), null));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		// Customize status mapping
		if (ex.getErrorCode() == ErrorCode.ORD_001) {
			status = HttpStatus.NOT_FOUND;

		} else if (ex.getErrorCode() == ErrorCode.ORD_002) {
			// Invalid order state transition (already approved / cancelled / shipped)
			// maps to 409 CONFLICT (Requirements 12.3, 13.2, 13.3).
			status = HttpStatus.CONFLICT;

		} else if (ex.getErrorCode() == ErrorCode.ORD_003) {
			// Semantically invalid fulfillment (unknown product / over-ship).
			status = HttpStatus.UNPROCESSABLE_ENTITY;
		}

		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), ex.getErrorCode().name(),
				status.value(), request.getRequestURI(), null));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(ResourceNotFoundException ex,
			HttpServletRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;

		return ResponseEntity.status(status).body(ErrorResponse.of(ex.getMessage(), HttpStatus.NOT_FOUND.toString(),
				status.value(), request.getRequestURI(), null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());

		return ResponseEntity.badRequest().body(ErrorResponse.of("Validation failed", "VALIDATION_ERROR",
				HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), details));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		HttpStatus status = HttpStatus.FORBIDDEN;
		return ResponseEntity.status(status).body(ErrorResponse.of("Access denied", "FORBIDDEN", status.value(),
				request.getRequestURI(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErrorResponse.of("Internal server error", "SERVER_ERROR",
						HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI(), List.of(ex.getMessage())));
	}
}
