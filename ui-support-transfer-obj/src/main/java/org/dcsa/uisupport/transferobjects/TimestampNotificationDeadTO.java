package org.dcsa.uisupport.transferobjects;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TimestampNotificationDeadTO(
  UUID id,
  MessageRoutingRuleTO messageRoutingRule,
  String payload,
  OffsetDateTime latestDeliveryAttemptedDatetime
) {

  @Builder // workaround for intellij issue
  public TimestampNotificationDeadTO { }
}
