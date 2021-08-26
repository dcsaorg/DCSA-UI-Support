package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.uisupport.model.EventDelivery;
import org.dcsa.uisupport.service.EventDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(
        value = "unofficial-event-delivery-status",
        produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EventDeliveryController extends ExtendedBaseController<EventDeliveryService, EventDelivery, UUID> {
    private final EventDeliveryService eventDeliveryService;

    @Override
    public EventDeliveryService getService() {
        return eventDeliveryService;
    }

    }
}
