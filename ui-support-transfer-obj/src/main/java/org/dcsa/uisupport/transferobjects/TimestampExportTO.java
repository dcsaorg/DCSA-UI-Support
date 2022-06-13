package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.OperationsEventTO;

import java.time.ZoneId;

public record TimestampExportTO(String negotiationSequenceID,
                                OperationsEventTO operationsEvent,
                                TimestampDefinitionTO timestampDefinition,
                                ZoneId timezone) {

  @Builder // workaround for intellij issue
  public TimestampExportTO { }
}
