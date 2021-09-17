package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.service.FacilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(
    value = "unofficial/terminals",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class FacilityController extends ExtendedBaseController<FacilityService, Facility, UUID> {

  private final FacilityService facilityService;

  @Override
  public FacilityService getService() {
    return facilityService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Facility> create(@Valid @RequestBody Facility facility) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @PutMapping(path = "{facilityID}")
  public Mono<Facility> update(@PathVariable UUID facilityID, @Valid @RequestBody Facility facility) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> delete(@RequestBody Facility facility) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping(path = "{facilityID}")
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> deleteById(@PathVariable UUID facilityID) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }
}
