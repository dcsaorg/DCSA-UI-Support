package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import org.dcsa.uisupport.transferobjects.enums.LocationRequirement;

import jakarta.validation.constraints.Size;
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

  LocationRequirement eventLocationRequirement,

  Boolean isTerminalNeeded,

  Boolean isVesselDraftRelevant,

  LocationRequirement vesselPositionRequirement,

  NegotiationCycleTO negotiationCycle,

  Boolean isMilesToDestinationRelevant,

  String providedInStandard,

  String acceptTimestampDefinition,

  String rejectTimestampDefinition,

  String implicitVariantOf,

  Set<PublisherPatternTO> publisherPattern
) {
  @Builder // workaround for intellij issue
  public TimestampDefinitionTO {}
}
