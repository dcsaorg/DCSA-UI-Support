package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Size;

public record TerminalTO(
  @Size(max = 100)
  String facilityName,

  @Size(max = 4)
  String facilityBICCode,

  @Size(max = 6)
  String facilitySMDGCode,

  @Size(max = 5)
  String UNLocationCode) {

  @Builder
  public TerminalTO {}
}
