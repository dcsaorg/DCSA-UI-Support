package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.TerminalService;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/terminals", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TerminalController {
  private final TerminalService terminalService;

  @GetMapping
  public List<TerminalTO> getTerminals(@RequestParam(value = "UNLocationCode") String unLocationCode) {
    return terminalService.findFacilitiesForUnLocationCode(unLocationCode);
  }
}
