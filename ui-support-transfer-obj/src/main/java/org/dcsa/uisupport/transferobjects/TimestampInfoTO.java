package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.uisupport.transferobjects.enums.EventDeliveryStatus;

public record TimestampInfoTO (
  OperationsEventTO operationsEventTO,
  TimestampDefinitionTO timestampDefinitionTO,
  EventDeliveryStatus eventDeliveryStatus
) {
  @Builder // workaround for intellij issue
  public TimestampInfoTO {}
}

