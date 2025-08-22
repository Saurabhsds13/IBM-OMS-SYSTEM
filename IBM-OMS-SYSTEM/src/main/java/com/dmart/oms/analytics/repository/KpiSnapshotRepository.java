package com.dmart.oms.analytics.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.analytics.model.KpiSnapshot;

//analytics/repository/KpiSnapshotRepository.java
@Repository
public interface KpiSnapshotRepository extends JpaRepository<KpiSnapshot, Long> {
	Optional<KpiSnapshot> findTopByOrderByComputedAtDesc();
}
