package com.dmart.oms.security.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.security.repository.UserRepository;

/**
 * Loads users for Spring Security. Roles are exposed as {@code ROLE_<name>}
 * authorities so that {@code hasRole()} / {@code @PreAuthorize} work naturally.
 */
@Service
public class OmsUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public OmsUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.dmart.oms.security.model.User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		var authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
				.collect(Collectors.toSet());

		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(authorities)
				.disabled(!user.isEnabled())
				.accountLocked(user.isLocked())
				.build();
	}
}
