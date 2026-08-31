package com.dmart.oms.common.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.common.model.OutboxEvent;
import com.dmart.oms.common.repository.OutboxRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

//notification/controller/OutboxAdminController.java
@RestController
@RequestMapping("/api/admin/notifications")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class OutboxAdminController {
	private final OutboxRepository repo;

	public OutboxAdminController(OutboxRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/pending")
	public List<OutboxEvent> pending() {
		return repo.findTop100ByStatusOrderByCreatedAt("PENDING");
	}

	@PostMapping("/retry/{id}")
	public void retry(@PathVariable Long id) {
		repo.findById(id).ifPresent(e -> {
			e.setStatus("PENDING");
			repo.save(e);
		});
	}
}
