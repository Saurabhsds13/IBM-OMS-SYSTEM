package com.dmart.oms.payment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dmart.oms.payment.model.Payment;
import com.dmart.oms.payment.service.PaymentService;

@RestController
@RequestMapping("/api/admin/payments")
public class PaymentController {
	private final PaymentService service;

	public PaymentController(PaymentService service) {
		this.service = service;
	}

	@PostMapping("/initiate")
	public Payment initiate(@RequestParam String orderNumber, @RequestParam double amount) {
		return service.initiatePayment(orderNumber, amount);
	}

	@PostMapping("/{id}/refund")
	public Payment refund(@PathVariable Long id) {
		return service.refund(id);
	}

	@PostMapping("/{id}/success")
	public Payment markSuccess(@PathVariable Long id) {
		return service.markSuccess(id);
	}

	@PostMapping("/{id}/fail")
	public Payment markFailed(@PathVariable Long id) {
		return service.markFailed(id);
	}
}
