package org.dcsa.uisupport.transferobjects;

import jakarta.validation.constraints.Size;

public record PublisherPatternTO(
  String id,
  @Size(max = 3) String publisherRole,
  @Size(max = 3) String primaryReceiver
  ) {

}
