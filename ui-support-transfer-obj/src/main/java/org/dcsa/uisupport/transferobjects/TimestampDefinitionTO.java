package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import javax.validation.constraints.Size;
import java.util.Set;

public record TimestampDefinitionTO(
  String id,

  String timestampTypeName,

  @Size(max = 3)
  String eventClassifierCode,

  @Size(max = 4)
  String operationsEventTypeCode,

  @Size(max = 4)
  String portCallPhaseTypeCode,

  @Size(max = 4)
  String portCallServiceTypeCode,

  @Size(max = 4)
  String facilityTypeCode,

  @Size(max = 100)
  String portCallPart,

  Boolean isBerthLocationNeeded,

  Boolean isPBPLocationNeeded,

  Boolean isAnchorageLocationNeeded,

  Boolean isTerminalNeeded,

  Boolean isVesselPositionNeeded,
  String negotiationCycle,

  String providedInStandard,

  String acceptTimestampDefinition,

  String rejectTimestampDefinition,

  Set<PublisherPatternTO> publisherPattern
) {
  @Builder // workaround for intellij issue
  public TimestampDefinitionTO {}
}
