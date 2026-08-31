package com.dmart.oms.security.exception;

/**
 * Raised when a create-user request references a role that does not exist.
 * Mapped to HTTP 400.
 */
public class InvalidRoleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidRoleException(String message) {
		super(message);
	}
}
