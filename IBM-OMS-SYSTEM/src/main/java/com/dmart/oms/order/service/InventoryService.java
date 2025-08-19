package com.dmart.oms.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.Inventory.model.Inventory;
import com.dmart.oms.Inventory.repository.InventoryRepository;

@Service
public class InventoryService {

	private final InventoryRepository repo;

	public InventoryService(InventoryRepository repo) {
		this.repo = repo;
	}

	public List<Inventory> getAllStock() {
		return repo.findAll();
	}

	@Transactional
	public Inventory reserveStock(String productCode, int qty) {
		List<Inventory> stocks = repo.findByProductCode(productCode);
		if (stocks.isEmpty())
			throw new RuntimeException("Product not found in inventory");

		for (Inventory stock : stocks) {
			if (stock.getAvailableQty() >= qty) {
				stock.setAvailableQty(stock.getAvailableQty() - qty);
				stock.setReservedQty(stock.getReservedQty() + qty);
				return repo.save(stock);
			}
		}
		throw new RuntimeException("Insufficient stock - consider backorder");
	}

	public Inventory addStock(Inventory inventory) {
		return repo.save(inventory);
	}
}
