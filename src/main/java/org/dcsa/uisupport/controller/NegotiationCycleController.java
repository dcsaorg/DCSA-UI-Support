package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.NegotiationCycle;
import org.dcsa.uisupport.service.NegotiationCycleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "unofficial/negotiation-cycles",
        produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class NegotiationCycleController extends ExtendedBaseController<NegotiationCycleService, NegotiationCycle, String> {

    private final NegotiationCycleService negotiationCycleService;

    @Override
    public NegotiationCycleService getService() {
        return negotiationCycleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<NegotiationCycle> create(@Valid @RequestBody NegotiationCycle negotiationCycle) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @PutMapping(path = "{cycle_key}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<NegotiationCycle> update(@PathVariable String cycle_key, @Valid @RequestBody NegotiationCycle negotiationCycle) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody NegotiationCycle negotiationCycle) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping(path = "{cycle_key}")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable String cycle_key) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }


}
