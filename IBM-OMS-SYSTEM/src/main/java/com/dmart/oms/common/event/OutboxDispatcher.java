package com.dmart.oms.common.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dmart.oms.common.model.OutboxEvent;
import com.dmart.oms.common.repository.OutboxRepository;

@Component
public class OutboxDispatcher {

	private final OutboxRepository outboxRepo;
	private final List<OutboxPublisher> publishers; // all injected publishers

	public OutboxDispatcher(OutboxRepository outboxRepo, List<OutboxPublisher> publishers) {
		this.outboxRepo = outboxRepo;
		this.publishers = publishers;
	}

	// Run every 10 seconds (tune as needed)
	@Scheduled(fixedRate = 300000)
	@Transactional
	public void dispatchEvents() {
		List<OutboxEvent> events = outboxRepo.findTop10ByStatusOrderByCreatedAtAsc("PENDING");

		for (OutboxEvent ev : events) {
			try {
				publishers.stream().filter(p -> p.canHandle(ev.getAggregateType(), ev.getEventType())).findFirst()
						.ifPresentOrElse(p -> {
							try {
								p.publish(ev.getAggregateType(), ev.getAggregateId().toString(), ev.getEventType(),
										ev.getPayload());
								ev.setStatus("PUBLISHED");
								outboxRepo.save(ev);
							} catch (Exception ex) {
								handleFailure(ev, ex);
							}
						}, () -> handleFailure(ev,
								new RuntimeException("No publisher found for " + ev.getAggregateType())));
			} catch (Exception ex) {
				handleFailure(ev, ex);
			}
		}
	}

	private void handleFailure(OutboxEvent ev, Exception ex) {
		ev.setAttemptCount(ev.getAttemptCount() + 1);
		if (ev.getAttemptCount() >= 5) {
			ev.setStatus("DEAD_LETTER");
		} else {
			ev.setStatus("PENDING");
		}
		ev.setLastAttemptAt(LocalDateTime.now());
		outboxRepo.save(ev);
	}
}
