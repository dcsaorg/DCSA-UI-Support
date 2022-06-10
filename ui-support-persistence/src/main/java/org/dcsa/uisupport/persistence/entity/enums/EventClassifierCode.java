package org.dcsa.uisupport.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EventClassifierCode {
  ACT("Actual"),
  PLN("Planned"),
  EST("Estimated"),
  REQ("Requested")
  ;

  @Getter
  private final String value;
}
