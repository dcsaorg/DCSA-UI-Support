package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.uisupport.model.EventDelivery;
import org.dcsa.uisupport.service.EventDeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
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

    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<EventDelivery> create(@Valid @RequestBody EventDelivery eventDelivery) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @PutMapping(path = "{eventID}")
    public Mono<EventDelivery> update(@PathVariable UUID eventID, @Valid @RequestBody EventDelivery eventDelivery) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody EventDelivery eventDelivery) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping(path = "{eventID}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable UUID eventID) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }
}
