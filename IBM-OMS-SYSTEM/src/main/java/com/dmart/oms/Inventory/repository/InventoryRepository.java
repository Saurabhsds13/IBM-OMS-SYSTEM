package com.dmart.oms.Inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dmart.oms.Inventory.model.Inventory;

import jakarta.persistence.LockModeType;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT i FROM Inventory i WHERE i.productCode = :productCode")
	Optional<Inventory> findByProductCodeForUpdate(@Param("productCode") String productCode);

	Optional<Inventory> findByProductCodeAndVendorName(String productCode, String vendorName);

	Optional<Inventory> findByProductCode(String productCode);
}