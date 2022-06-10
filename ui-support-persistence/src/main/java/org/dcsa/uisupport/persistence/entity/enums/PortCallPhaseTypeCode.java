package org.dcsa.uisupport.persistence.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PortCallPhaseTypeCode {
  INBD("Inbound"),
  ALGS("Alongside"),
  SHIF("Shifting"),
  OUTB("Outbound")
  ;

  @Getter
  private final String name;
}
