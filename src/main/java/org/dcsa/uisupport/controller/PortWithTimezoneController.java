package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.uisupport.model.PortWithTimezone;
import org.dcsa.uisupport.service.PortWithTimezoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping(
    value = "unofficial/ports",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class PortWithTimezoneController extends ExtendedBaseController<PortWithTimezoneService, PortWithTimezone, String> {

  private final PortWithTimezoneService portWithTimezoneService;

  @Override
  public PortWithTimezoneService getService() {
    return portWithTimezoneService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<PortWithTimezone> create(@Valid @RequestBody PortWithTimezone port) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @PutMapping(path = "{unLocationCode}")
  public Mono<PortWithTimezone> update(@PathVariable String unLocationCode, @Valid @RequestBody PortWithTimezone port) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> delete(@RequestBody PortWithTimezone port) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping(path = "{unLocationCode}")
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> deleteById(@PathVariable String unLocationCode) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }
}
