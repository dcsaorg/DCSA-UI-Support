package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.uisupport.service.UiTransportCallService;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/transport-calls", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TransportCallController {
  private final UiTransportCallService uiTransportCallService;

  @GetMapping
  public List<TransportCallWithTimestampsTO> findAll(
    @RequestParam(value = "UNLocationCode", required = false) String unLocationCode,
    @RequestParam(value = "vesselIMONumber", required = false) String vesselIMONumber
  ) {
    return uiTransportCallService.findAll(unLocationCode, vesselIMONumber);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransportCallWithTimestampsTO create(@RequestBody TransportCallTO transportCallTO) {
    return uiTransportCallService.create(transportCallTO);
  }
}
