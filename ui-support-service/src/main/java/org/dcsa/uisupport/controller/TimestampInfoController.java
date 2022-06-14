package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.TerminalService;
import org.dcsa.uisupport.service.TimestampDefinitionService;
import org.dcsa.uisupport.service.TimestampInfoService;
import org.dcsa.uisupport.transferobjects.TerminalTO;
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
  public List<TerminalTO> getTerminals(
      @RequestParam(value = "transportCallID") String unLocationCode,
      @RequestParam(value = "negotiationCycle") String negotiationCycle) {
    return timestampInfoService.findAll(unLocationCode, negotiationCycle);
  }
}
