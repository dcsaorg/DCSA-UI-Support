package org.dcsa.uisupport.transferobjects;

import lombok.Builder;

import jakarta.validation.constraints.Size;

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
