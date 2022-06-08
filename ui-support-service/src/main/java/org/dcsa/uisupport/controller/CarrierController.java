package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.service.CarrierService;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "unofficial/carriers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CarrierController {
  private final CarrierService carrierService;

  @GetMapping
  public List<CarrierTO> findAll() {
    return carrierService.findAll();
  }
}
