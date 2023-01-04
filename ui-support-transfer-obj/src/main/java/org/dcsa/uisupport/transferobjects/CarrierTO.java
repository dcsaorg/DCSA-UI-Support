package org.dcsa.uisupport.transferobjects;

import lombok.Builder;

import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CarrierTO(
  UUID id,

  @Size(max = 100)
  String carrierName,

  @Size(max = 3)
  String smdgCode,

  @Size(max = 4)
  String nmftaCode
) {
  @Builder // workaround for intellij issue
  public CarrierTO {}
}
