package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty("transportCallID")
  private UUID transportCallID;

  @JsonProperty("latestEventCreatedDateTime")
  private OffsetDateTime latestEventCreatedDateTime;

  @JsonProperty("etaBerthDateTime")
  private OffsetDateTime etaBerthDateTime;

  @JsonProperty("atdBerthDateTime")
  private OffsetDateTime atdBerthDateTime;
}
