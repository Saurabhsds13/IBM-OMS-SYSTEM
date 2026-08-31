package com.dmart.oms.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI documentation with a bearer JWT security scheme (Requirement 8). The
 * scheme named {@code bearerAuth} is referenced by controllers via
 * {@code @SecurityRequirement(name = "bearerAuth")}, and Swagger UI exposes an
 * Authorize dialog so callers can supply an Access_Token.
 */
@Configuration
public class OpenApiConfig {

	private static final String SCHEME_NAME = "bearerAuth";

	@Bean
	public OpenAPI omsOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("OMS Admin API")
						.description("Order Management System admin API. Secured admin endpoints require a bearer JWT.")
						.version("v1"))
				.components(new Components()
						.addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
								.name(SCHEME_NAME)
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")));
	}
}
