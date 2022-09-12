package org.dcsa.uisupport.mapping;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.TransportCallMapper;
import org.dcsa.uisupport.persistence.entity.JITPortVisitUIContext;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransportCallWithTimestampsMapper {
  private final TransportCallMapper transportCallMapper;

  public TransportCallWithTimestampsTO toTO(JITPortVisitUIContext src) {
    return TransportCallWithTimestampsTO.builder()
      .transportCallID(src.getPortVisitID())
      .transportCallTO(transportCallMapper.toTO(src.getJitPortVisit()))
      .atdBerthDateTime(src.getAtdBerthDateTime())
      .etaBerthDateTime(src.getEtaBerthDateTime())
      .omitCreatedDateTime(src.getOmitCreatedDateTime())
      .latestEventCreatedDateTime(src.getLatestEventCreatedDateTime())
      .vesselDraft(src.getVesselDraft())
      .milesToDestinationPort(src.getMilesToDestinationPort())
      .build();
  }
}
