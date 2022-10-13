package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.jit.transferobjects.enums.DeliveryStatus;

public record TimestampInfoTO (
  OperationsEventTO operationsEventTO,
  TimestampDefinitionTO timestampDefinitionTO,
  DeliveryStatus eventDeliveryStatus
) {
  @Builder // workaround for intellij issue
  public TimestampInfoTO {}
}

