package com.dmart.oms.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.security.dto.TokenResponse;
import com.dmart.oms.security.exception.AccountLockedException;
import com.dmart.oms.security.exception.AuthenticationFailedException;
import com.dmart.oms.security.jwt.JwtProperties;
import com.dmart.oms.security.jwt.JwtTokenProvider;
import com.dmart.oms.security.model.Role;
import com.dmart.oms.security.model.User;
import com.dmart.oms.security.repository.UserRepository;

import io.jsonwebtoken.JwtException;

/**
 * Authenticates credentials and issues, validates, and refreshes tokens
 * (Auth_Service). Implements brute-force protection: 5 failed attempts locks
 * the account for 15 minutes (Requirement 6).
 */
@Service
public class AuthService {

	static final int MAX_FAILED_ATTEMPTS = 5;
	static final long LOCK_DURATION_MINUTES = 15;

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final JwtProperties jwtProperties;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider,
			JwtProperties jwtProperties) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
		this.jwtProperties = jwtProperties;
	}

	@Transactional
	public TokenResponse login(String username, String rawPassword) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthenticationFailedException("Invalid username or password"));

		// While locked, reject regardless of password correctness (Req 6.4).
		if (user.isLocked()) {
			throw new AccountLockedException("Account is locked. Try again later.");
		}

		if (!user.isEnabled()) {
			throw new AuthenticationFailedException("Account is disabled");
		}

		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			registerFailedAttempt(user);
			throw new AuthenticationFailedException("Invalid username or password");
		}

		// Successful login: reset counters and any expired lock (Req 6.2, 6.5).
		user.setFailedAttempts(0);
		user.setLockUntil(null);
		userRepository.save(user);

		return issueTokens(user);
	}

	@Transactional
	public TokenResponse refresh(String refreshToken) {
		final String username;
		final String type;
		try {
			username = tokenProvider.getUsername(refreshToken);
			type = tokenProvider.getTokenType(refreshToken);
		} catch (JwtException | IllegalArgumentException ex) {
			// Covers expired, malformed, and signature-validation failures (Req 2.2, 2.3).
			throw new AuthenticationFailedException("Invalid or expired refresh token");
		}

		if (!JwtTokenProvider.REFRESH_TYPE.equals(type)) {
			throw new AuthenticationFailedException("Provided token is not a refresh token");
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AuthenticationFailedException("Invalid or expired refresh token"));

		if (!user.isEnabled()) {
			throw new AuthenticationFailedException("Account is disabled");
		}

		// Refreshed access token gets a fresh 15 minute lifetime (Req 2.4).
		String accessToken = tokenProvider.generateAccessToken(user.getUsername(), roleNames(user));
		return new TokenResponse(accessToken, refreshToken, jwtProperties.getAccessTokenExpirationMs() / 1000);
	}

	private void registerFailedAttempt(User user) {
		int attempts = user.getFailedAttempts() + 1;
		user.setFailedAttempts(attempts);
		if (attempts >= MAX_FAILED_ATTEMPTS) {
			user.setLockUntil(Instant.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES));
		}
		userRepository.save(user);
	}

	private TokenResponse issueTokens(User user) {
		String accessToken = tokenProvider.generateAccessToken(user.getUsername(), roleNames(user));
		String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());
		return new TokenResponse(accessToken, refreshToken, jwtProperties.getAccessTokenExpirationMs() / 1000);
	}

	private List<String> roleNames(User user) {
		return user.getRoles().stream().map(Role::getName).toList();
	}
}
