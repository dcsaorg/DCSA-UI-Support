package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.TimestampDefinitionService;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/timestamp-definitions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TimestampDefinitionController {
  private final TimestampDefinitionService timestampDefinitionService;

  @GetMapping
  public List<TimestampDefinitionTO> findAll() {
    return timestampDefinitionService.findAll();
  }
}
