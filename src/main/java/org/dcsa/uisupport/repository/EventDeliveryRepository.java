package org.dcsa.uisupport.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.uisupport.model.EventDelivery;
import org.dcsa.uisupport.model.PendingEvent;
import org.dcsa.uisupport.model.UnmappedEvent;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventDeliveryRepository extends ExtendedRepository<EventDelivery, UUID> {
    @Query("SELECT unmapped_event_queue.* from unmapped_event_queue"
            + " WHERE unmapped_event_queue.event_id = :eventId")
    Mono<UnmappedEvent> findUnmappedEventByEventId(UUID eventId);

    @Query("SELECT pending_event_queue.* from pending_event_queue"
            + " WHERE pending_event_queue.event_id = :eventId"
            + " ORDER BY pending_event_queue.retry_count DESC"
            + " LIMIT 1")
    Mono<PendingEvent> findPendingEventByEventId(UUID eventId);
}
