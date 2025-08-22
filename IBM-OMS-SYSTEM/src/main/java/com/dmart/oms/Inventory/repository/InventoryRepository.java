package com.dmart.oms.Inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.Inventory.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByProductCodeAndVendorName(String productCode, String vendorName);
	List<Inventory> findByProductCode(String productCode);
}