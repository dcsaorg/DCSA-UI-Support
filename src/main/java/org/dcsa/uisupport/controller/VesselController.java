package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.validator.ValidVesselIMONumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(
    value = "unofficial/vessels",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class VesselController extends ExtendedBaseController<VesselService, Vessel, UUID> {

  private final VesselService vesselService;

  @Override
  public VesselService getService() {
    return vesselService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Vessel> create(@Valid @RequestBody Vessel vessel) {
    // Override subclass as we *want* an ID (default is that the ID must be absent)
    if (vessel.getVesselIMONumber() == null) {
      throw new CreateException("Missing vessel IMO number");
    }
    return vesselService.create(vessel);
  }

  @PutMapping(path = "{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Vessel> update(
          @PathVariable UUID id, @Valid @RequestBody Vessel vessel) {
    if (!id.equals(vesselService.getIdOfEntity(vessel))) {
      return updateMonoError();
    }
    return vesselService.update(vessel);
  }

  @Override
  @DeleteMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> delete(@RequestBody Vessel vessel) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }
}
