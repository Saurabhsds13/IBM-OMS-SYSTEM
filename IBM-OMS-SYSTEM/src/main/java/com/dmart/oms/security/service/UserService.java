package com.dmart.oms.security.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.security.dto.CreateUserRequest;
import com.dmart.oms.security.dto.UserResponse;
import com.dmart.oms.security.exception.InvalidRoleException;
import com.dmart.oms.security.exception.UsernameAlreadyExistsException;
import com.dmart.oms.security.model.Role;
import com.dmart.oms.security.model.User;
import com.dmart.oms.security.repository.RoleRepository;
import com.dmart.oms.security.repository.UserRepository;

/**
 * Admin user provisioning (Requirement 7).
 */
@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UsernameAlreadyExistsException("Username already exists: " + request.getUsername());
		}

		Set<Role> roles = new HashSet<>();
		for (String roleName : request.getRoles()) {
			Role role = roleRepository.findByName(roleName)
					.orElseThrow(() -> new InvalidRoleException("Unknown role: " + roleName));
			roles.add(role);
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEnabled(true);
		user.setRoles(roles);

		User saved = userRepository.save(user);
		return toResponse(saved);
	}

	private UserResponse toResponse(User user) {
		Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
		return new UserResponse(user.getId(), user.getUsername(), user.isEnabled(), roleNames);
	}
}
