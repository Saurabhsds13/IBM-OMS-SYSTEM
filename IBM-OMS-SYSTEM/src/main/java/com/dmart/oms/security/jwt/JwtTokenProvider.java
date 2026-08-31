package com.dmart.oms.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Issues and validates signed JWTs. Access tokens carry the authenticated
 * user's roles as a claim (Requirement 1.5). Tokens are signed with a secret
 * supplied by the Configuration_Provider (Requirement 1.2).
 */
@Component
public class JwtTokenProvider {

	public static final String TOKEN_TYPE_CLAIM = "type";
	public static final String ROLES_CLAIM = "roles";
	public static final String ACCESS_TYPE = "access";
	public static final String REFRESH_TYPE = "refresh";

	private final JwtProperties properties;
	private final SecretKey signingKey;

	public JwtTokenProvider(JwtProperties properties) {
		this.properties = properties;
		this.signingKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(String username, Collection<String> roles) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + properties.getAccessTokenExpirationMs());
		return Jwts.builder()
				.subject(username)
				.claim(TOKEN_TYPE_CLAIM, ACCESS_TYPE)
				.claim(ROLES_CLAIM, roles)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(signingKey)
				.compact();
	}

	public String generateRefreshToken(String username) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + properties.getRefreshTokenExpirationMs());
		return Jwts.builder()
				.subject(username)
				.claim(TOKEN_TYPE_CLAIM, REFRESH_TYPE)
				.issuedAt(now)
				.expiration(expiry)
				.signWith(signingKey)
				.compact();
	}

	/**
	 * Parses and validates the token signature and expiry. Throws a
	 * {@link io.jsonwebtoken.JwtException} subclass on failure.
	 */
	public Jws<Claims> parse(String token) {
		return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
	}

	public String getUsername(String token) {
		return parse(token).getPayload().getSubject();
	}

	public String getTokenType(String token) {
		return parse(token).getPayload().get(TOKEN_TYPE_CLAIM, String.class);
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoles(String token) {
		Object raw = parse(token).getPayload().get(ROLES_CLAIM);
		if (raw instanceof Collection<?> c) {
			return c.stream().map(String::valueOf).collect(Collectors.toList());
		}
		return List.of();
	}
}
