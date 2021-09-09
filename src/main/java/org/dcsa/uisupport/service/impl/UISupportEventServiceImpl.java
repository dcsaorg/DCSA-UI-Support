package org.dcsa.uisupport.service.impl;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.repository.PendingEventRepository;
import org.dcsa.core.events.service.EquipmentEventService;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.ShipmentEventService;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.events.service.impl.GenericEventServiceImpl;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.uisupport.service.UISupportEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class UISupportEventServiceImpl extends GenericEventServiceImpl implements UISupportEventService {

    private final Set<EventType> SUPPORTED_EVENT_TYPES = Set.of(EventType.OPERATIONS);

    public UISupportEventServiceImpl(TransportEventService transportEventService, EquipmentEventService equipmentEventService, ShipmentEventService shipmentEventService, OperationsEventService operationsEventService, EventRepository eventRepository, PendingEventRepository pendingEventRepository) {
        super(shipmentEventService, transportEventService, equipmentEventService,operationsEventService, eventRepository, pendingEventRepository);
    }

    public Mono<Integer> countAllExtended(ExtendedRequest<Event> extendedRequest) {
        return getRepository().countAllExtended(extendedRequest);
    }


    protected Set<EventType> getSupportedEvents() {
        return SUPPORTED_EVENT_TYPES;
    }
}
