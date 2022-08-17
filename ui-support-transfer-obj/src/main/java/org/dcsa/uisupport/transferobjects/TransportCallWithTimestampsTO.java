package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.jit.transferobjects.TransportCallTO;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Normal Data class due to deserilization issues with @JsonUnwrapped.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCallWithTimestampsTO {
  @JsonUnwrapped
  private TransportCallTO transportCallTO;

  private UUID transportCallID;

  private OffsetDateTime latestEventCreatedDateTime;

  private OffsetDateTime etaBerthDateTime;

  private OffsetDateTime atdBerthDateTime;

  private Float vesselDraft;
  private Float milesToDestinationPort;
}
