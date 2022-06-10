package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import javax.validation.constraints.Size;

public record TimestampDefinitionTO(
  @JsonProperty("id")
  String id,

  @JsonProperty("timestampTypeName")
  String typeName,

  @Size(max = 3) @JsonProperty("publisherRole")
  String publisherRole,

  @Size(max = 3) @JsonProperty("primaryReceiver")
  String primaryReceiver,

  @Size(max = 3) @JsonProperty("eventClassifierCode")
  String eventClassifierCode,

  @Size(max = 4) @JsonProperty("operationsEventTypeCode")
  String operationsEventTypeCode,

  @Size(max = 4) @JsonProperty("portCallPhaseTypeCode")
  String portCallPhaseTypeCode,

  @Size(max = 4) @JsonProperty("portCallServiceTypeCode")
  String portCallServiceTypeCode,

  @Size(max = 4) @JsonProperty("facilityTypeCode")
  String facilityTypeCode,

  @JsonProperty("isBerthLocationNeeded")
  Boolean isBerthLocationNeeded,

  @JsonProperty("isPBPLocationNeeded")
  Boolean isPbpLocationNeeded,

  @JsonProperty("isTerminalNeeded")
  Boolean isTerminalNeeded,

  @JsonProperty("isVesselPositionNeeded")
  Boolean isVesselPositionNeeded,

  @JsonProperty("negotiationCycle")
  String negotiationCycle,

  @JsonProperty("providedInStandard")
  String providedInStandard,

  @JsonProperty("acceptTimestampDefinition")
  String acceptTimestampDefinition,

  @JsonProperty("rejectTimestampDefinition")
  String rejectTimestampDefinition
) {
  @Builder // workaround for intellij issue
  public TimestampDefinitionTO {}
}
