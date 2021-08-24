package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.ExtendedBaseController;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.uisupport.model.transferobjects.TransportTO;
import org.dcsa.uisupport.service.TransportTOService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(
    value = "unofficial-transports",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class TransportTOController
    extends ExtendedBaseController<TransportTOService, TransportTO, UUID> {

  private final TransportTOService transportTOService;

  @Override
  public String getType() {
    return "Transport";
  }

  @Override
  public TransportTOService getService() {
    return transportTOService;
  }

  @Override
  protected ExtendedRequest<TransportTO> newExtendedRequest() {
    return transportTOService.newExtendedRequest();
  }
}
