package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Size;

public record PortWithTimezoneTO(
  @Size(max = 5) @JsonProperty("unLocationCode")
  String unLocationCode,

  @Size(max = 100) @JsonProperty("unLocationName")
  String unLocationName,

  @JsonProperty("ianaTimezone")
  String ianaTimezone
) {
  @Builder // workaround for intellij issue
  public PortWithTimezoneTO {}
}
