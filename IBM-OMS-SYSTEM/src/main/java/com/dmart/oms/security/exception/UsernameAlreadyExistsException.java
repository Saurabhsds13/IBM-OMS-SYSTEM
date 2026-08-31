package com.dmart.oms.security.exception;

/**
 * Raised when creating a user with a username that already exists. Mapped to
 * HTTP 409 (Requirement 7.4).
 */
public class UsernameAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExistsException(String message) {
		super(message);
	}
}
