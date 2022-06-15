package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Size;

public record PortWithTimezoneTO(
  @Size(max = 5)
  String UNLocationCode,

  @Size(max = 100)
  String UNLocationName,

  @Size(max = 3)
  String locationCode,

  @Size(max = 2)
  String countryCode,

  @JsonProperty("timezone")
  String ianaTimezone
) {
  @Builder // workaround for intellij issue
  public PortWithTimezoneTO {}
}
