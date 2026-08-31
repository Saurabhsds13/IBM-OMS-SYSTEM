package com.dmart.oms.order.event;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Maintains the set of connected admin-UI SSE clients and fans order status
 * events out to all of them. Dead emitters are pruned on send failure.
 */
@Component
public class OrderEventBroadcaster {

	private static final Logger log = LoggerFactory.getLogger(OrderEventBroadcaster.class);
	private static final long TIMEOUT_MS = 30L * 60L * 1000L; // 30 minutes

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SseEmitter register() {
		SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError(e -> emitters.remove(emitter));
		emitters.add(emitter);

		try {
			// Initial handshake so the client knows the stream is live.
			emitter.send(SseEmitter.event().name("connected").data("ok"));
		} catch (IOException e) {
			emitters.remove(emitter);
		}
		return emitter;
	}

	public void broadcast(OrderStatusEvent event) {
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("order-status").data(event));
			} catch (Exception ex) {
				// Client disconnected or slow; drop it.
				emitters.remove(emitter);
				log.debug("Removed dead SSE emitter: {}", ex.getMessage());
			}
		}
	}

	public int clientCount() {
		return emitters.size();
	}
}
