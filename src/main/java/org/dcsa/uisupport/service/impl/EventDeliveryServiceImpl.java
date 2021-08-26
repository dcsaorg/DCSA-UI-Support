package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.model.AttemptedEventDelivery;
import org.dcsa.uisupport.model.EventDelivery;
import org.dcsa.uisupport.model.PendingEvent;
import org.dcsa.uisupport.model.enums.EventDeliveryStatus;
import org.dcsa.uisupport.repository.EventDeliveryRepository;
import org.dcsa.uisupport.service.EventDeliveryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EventDeliveryServiceImpl extends ExtendedBaseServiceImpl<EventDeliveryRepository, EventDelivery, UUID>
        implements EventDeliveryService {
    private final EventDeliveryRepository eventDeliveryRepository;

    @Override
    public EventDeliveryRepository getRepository() {
        return eventDeliveryRepository;
    }

    @Override
    public Mono<EventDelivery> findById(UUID eventID) {
        Mono<EventDelivery> unmappedEventDelivery = eventDeliveryRepository
                .findUnmappedEventByEventId(eventID)
                .map(event -> pendingDelivery());

        Mono<EventDelivery> pendingEventDelivery = eventDeliveryRepository
                .findPendingEventByEventId(eventID)
                .map(event -> {
                    if (event.getLastErrorMessage() == null) {
                        return pendingDelivery();
                    }
                    return attemptedEventDelivery(event);
                });

        return Flux.merge(pendingEventDelivery, unmappedEventDelivery)
                .switchIfEmpty(finishedDelivery())
                .next();
    }

    private AttemptedEventDelivery attemptedEventDelivery(PendingEvent pendingEvent) {
        AttemptedEventDelivery eventDelivery = new AttemptedEventDelivery();
        eventDelivery.setEventDeliveryStatus(EventDeliveryStatus.ATTEMPTED_DELIVERY);
        eventDelivery.setEqueuedAtDateTime(pendingEvent.getEqueuedAtDateTime());
        eventDelivery.setLastAttemptDateTime(pendingEvent.getLastAttemptDateTime());
        eventDelivery.setLastErrorMessage(pendingEvent.getLastErrorMessage());
        eventDelivery.setRetryCount(pendingEvent.getRetryCount());
        return eventDelivery;
    }

    private EventDelivery pendingDelivery() {
        EventDelivery eventDelivery = new EventDelivery();
        eventDelivery.setEventDeliveryStatus(EventDeliveryStatus.PENDING_DELIVERY);
        return eventDelivery;
    }

    private Flux<EventDelivery> finishedDelivery() {
        EventDelivery eventDelivery = new EventDelivery();
        eventDelivery.setEventDeliveryStatus(EventDeliveryStatus.DELIVERY_FINISHED);
        return Flux.just(eventDelivery);
    }
}
