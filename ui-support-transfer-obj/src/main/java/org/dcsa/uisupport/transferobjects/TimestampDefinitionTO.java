package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import javax.validation.constraints.Size;

public record TimestampDefinitionTO(
  String id,

  String timestampTypeName,

  @Size(max = 3)
  String publisherRole,

  @Size(max = 3)
  String primaryReceiver,

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

  Boolean isBerthLocationNeeded,

  Boolean isPBPLocationNeeded,

  Boolean isTerminalNeeded,

  Boolean isVesselPositionNeeded,

  String negotiationCycle,

  String providedInStandard,

  String acceptTimestampDefinition,

  String rejectTimestampDefinition
) {
  @Builder // workaround for intellij issue
  public TimestampDefinitionTO {}
}
