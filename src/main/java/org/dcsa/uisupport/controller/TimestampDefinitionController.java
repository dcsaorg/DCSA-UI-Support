package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.uisupport.service.TimestampDefinitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "unofficial/timestamp-definitions",
        produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class TimestampDefinitionController extends ExtendedBaseController<TimestampDefinitionService, TimestampDefinition, String> {

    private final TimestampDefinitionService timestampDefinitionService;

    @Override
    public TimestampDefinitionService getService() {
        return timestampDefinitionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<TimestampDefinition> create(@Valid @RequestBody TimestampDefinition timestampDefinition) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<TimestampDefinition> update(@PathVariable String id, @Valid @RequestBody TimestampDefinition timestampDefinition) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody TimestampDefinition timestampDefinition) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable String id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }


}
