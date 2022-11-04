package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.TimestampNotificationDead;
import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.dcsa.uisupport.transferobjects.TimestampNotificationDeadTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = MessageRouteRuleMapper.class)
public interface TimestampNotificationDeadMapper {
  TimestampNotificationDeadTO toTO(TimestampNotificationDead timestampNotificationDead);
}
