package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.uisupport.model.TransportCallWithTimestampsTO;
import org.dcsa.uisupport.service.TransportCallWithTimestampsTOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(
    value = "unofficial/transport-calls",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class TransportCallController
    extends ExtendedBaseController<TransportCallWithTimestampsTOService, TransportCallWithTimestampsTO, String> {

  private final TransportCallWithTimestampsTOService transportCallWithTimestampsTOService;
  private final TransportCallTOService transportCallTOService;

  @Override
  public TransportCallWithTimestampsTOService getService() {
    return transportCallWithTimestampsTOService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<TransportCallWithTimestampsTO> create(@RequestBody TransportCallWithTimestampsTO transportCallTO) {
    return transportCallTOService.create(transportCallTO)
            .map(tc -> MappingUtils.instanceFrom(tc, TransportCallWithTimestampsTO::new, TransportCallTO.class));
  }

  @Override
  @PutMapping(path = "{transportCallID}")
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<TransportCallWithTimestampsTO> update(
      @PathVariable String transportCallID, @RequestBody TransportCallWithTimestampsTO transportCallTO) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @Override
  @DeleteMapping
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> delete(@RequestBody TransportCallWithTimestampsTO transportCallTO) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping(path = "{transportCallID}")
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Mono<Void> deleteById(@PathVariable String transportCallID) {
    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
  }
}
