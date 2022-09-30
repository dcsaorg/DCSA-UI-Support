package org.dcsa.uisupport.transferobjects;

import lombok.Builder;
import org.dcsa.jit.transferobjects.enums.PublisherRole;

public record PublisherRoleDetailTO(
  PublisherRole publisherRole,
  String publisherRoleName
) {

  @Builder // workaround for intellij issue
  public PublisherRoleDetailTO { }
}
