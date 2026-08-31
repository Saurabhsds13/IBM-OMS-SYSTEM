package com.dmart.oms.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration. The signing secret is sourced from the environment via the
 * Configuration_Provider (Requirement 10). Token lifetimes default to the
 * values confirmed with the stakeholder: 15 minute access, 7 day refresh
 * (Requirements 1.3, 1.4).
 */
@ConfigurationProperties(prefix = "oms.security.jwt")
public class JwtProperties {

	/** Base64 or raw signing secret. Must be supplied via env; no default. */
	private String secret;

	/** Access token lifetime in milliseconds (default 15 minutes). */
	private long accessTokenExpirationMs = 15L * 60L * 1000L;

	/** Refresh token lifetime in milliseconds (default 7 days). */
	private long refreshTokenExpirationMs = 7L * 24L * 60L * 60L * 1000L;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public long getAccessTokenExpirationMs() {
		return accessTokenExpirationMs;
	}

	public void setAccessTokenExpirationMs(long accessTokenExpirationMs) {
		this.accessTokenExpirationMs = accessTokenExpirationMs;
	}

	public long getRefreshTokenExpirationMs() {
		return refreshTokenExpirationMs;
	}

	public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) {
		this.refreshTokenExpirationMs = refreshTokenExpirationMs;
	}
}
