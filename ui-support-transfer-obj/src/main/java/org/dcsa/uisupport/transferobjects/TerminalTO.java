package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Size;

public record TerminalTO(
  @Size(max = 100) @JsonProperty("facilityName")
  String name,
  @Size(max = 4) @JsonProperty("facilityBICCode")
  String bicCode,
  @Size(max = 6) @JsonProperty("facilitySMDGCode")
  String smdgCode,
  @Size(max = 5) @JsonProperty("UNLocationCode")
  String unLocationCode) {

  @Builder
  public TerminalTO {}
}
