package org.dcsa.uisupport.controller;

import lombok.AllArgsConstructor;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.uisupport.service.UiVesselService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "/unofficial/vessels", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class VesselController {

  private final UiVesselService uiVesselService;

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public VesselTO get(@PathVariable UUID id) {
    return uiVesselService.fetchVessel(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public VesselTO post(@Valid @RequestBody VesselTO request) {
    return uiVesselService.createVessel(request);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public VesselTO update(@PathVariable UUID id, @Valid @RequestBody VesselTO request) {
    return uiVesselService.updateVessel(id, request);
  }
}
