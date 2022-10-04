package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.PortCallPartService;
import org.dcsa.uisupport.transferobjects.PortCallPartTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/port-call-parts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PortCallPartController {

  private final PortCallPartService portCallPartService;


  @GetMapping
  public List<PortCallPartTO> getPublisherRoles() {
    return portCallPartService.findAll();
  }
}
