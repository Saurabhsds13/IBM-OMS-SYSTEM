package com.dmart.oms.security.web;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.dmart.oms.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Returns an HTTP 403 with an ApiResponse error envelope when an authenticated
 * caller lacks the role required for an operation (Requirement 4.4, 4.5, 7.5).
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper mapper;

	public RestAccessDeniedHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ApiResponse<Void> body = ApiResponse.failure("Access denied", "FORBIDDEN");
		mapper.writeValue(response.getWriter(), body);
	}
}
