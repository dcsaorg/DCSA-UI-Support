package org.dcsa.uisupport.mapping;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.TransportCallMapper;
import org.dcsa.uisupport.persistence.entity.TransportCallWithTimestamps;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransportCallWithTimestampsMapper {
  private final TransportCallMapper transportCallMapper;

  public TransportCallWithTimestampsTO toTO(TransportCallWithTimestamps src) {
    return TransportCallWithTimestampsTO.builder()
      .transportCallID(src.getId())
      .transportCallTO(transportCallMapper.toTO(src))
      .atdBerthDateTime(src.getAtdBerthDateTime())
      .etaBerthDateTime(src.getEtaBerthDateTime())
      .latestEventCreatedDateTime(src.getLatestEventCreatedDateTime())
      .vesselDraft(src.getVesselDraft())
      .milesToDestinationPort(src.getMilesRemainingToDestination())
      .build();
  }
}
