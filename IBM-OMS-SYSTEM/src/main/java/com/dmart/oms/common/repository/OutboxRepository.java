package com.dmart.oms.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dmart.oms.common.model.OutboxEvent;

//notification/repository/OutboxRepository.java
@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
	
	List<OutboxEvent> findTop100ByStatusOrderByCreatedAt(String status);

	List<OutboxEvent> findTop10ByStatusOrderByCreatedAtAsc(String status);

	long countByStatus(String status);
}