package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.PortWithTimezoneService;
import org.dcsa.uisupport.transferobjects.PortWithTimezoneTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/ports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PortWithTimezoneController {
  private final PortWithTimezoneService portWithTimezoneService;

  @GetMapping
  public List<PortWithTimezoneTO> findAll() {
    return portWithTimezoneService.findAll();
  }
}
