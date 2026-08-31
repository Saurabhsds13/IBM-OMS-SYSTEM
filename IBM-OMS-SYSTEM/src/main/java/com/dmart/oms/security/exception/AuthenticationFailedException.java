package com.dmart.oms.security.exception;

/**
 * Raised when credentials are invalid, the account is unknown, or a token fails
 * validation. Mapped to HTTP 401 (Requirements 1.6, 1.7, 2.2, 2.3).
 */
public class AuthenticationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AuthenticationFailedException(String message) {
		super(message);
	}
}
