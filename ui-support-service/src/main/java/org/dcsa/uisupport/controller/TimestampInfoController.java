package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.TimestampInfoService;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/timestamp-info", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TimestampInfoController {
  private final TimestampInfoService timestampInfoService;

  @GetMapping
  public List<TimestampInfoTO> getTimestampInfos(
      @RequestParam(value = "transportCallID", required = false) String transportCallID,
      @RequestParam(value = "negotiationCycle", required = false) String negotiationCycle,
      @RequestParam(value = "portCallPart", required = false) String portCallPart) {
    return timestampInfoService.findAll(transportCallID, negotiationCycle, portCallPart);
  }
}
