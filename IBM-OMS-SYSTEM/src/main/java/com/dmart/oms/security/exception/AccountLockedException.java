package com.dmart.oms.security.exception;

/**
 * Raised when a login attempt targets a locked account. Mapped to HTTP 423
 * (Requirement 6.4).
 */
public class AccountLockedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccountLockedException(String message) {
		super(message);
	}
}
