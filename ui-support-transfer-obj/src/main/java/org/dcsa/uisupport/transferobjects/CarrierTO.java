package org.dcsa.uisupport.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Size;
import java.util.UUID;

public record CarrierTO(
  @JsonProperty("id")
  UUID id,

  @Size(max = 100) @JsonProperty("carrierName")
  String name,

  @Size(max = 3) @JsonProperty("smdgCode")
  String smdgCode,

  @Size(max = 4) @JsonProperty("nmftaCode")
  String nmftaCode
) {
  @Builder // workaround for intellij issue
  public CarrierTO {}
}
