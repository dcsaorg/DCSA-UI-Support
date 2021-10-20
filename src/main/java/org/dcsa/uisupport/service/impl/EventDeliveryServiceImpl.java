package org.dcsa.uisupport.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.uisupport.model.EventDelivery;
import org.dcsa.uisupport.model.PendingEvent;
import org.dcsa.uisupport.model.enums.EventDeliveryStatus;
import org.dcsa.uisupport.repository.EventDeliveryRepository;
import org.dcsa.uisupport.service.EventDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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

}
