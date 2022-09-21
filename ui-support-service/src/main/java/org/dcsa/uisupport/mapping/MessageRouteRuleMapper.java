package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.MessageRoutingRule;
import org.dcsa.uisupport.transferobjects.MessageRoutingRuleTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageRouteRuleMapper {

  MessageRoutingRuleTO toTO(MessageRoutingRule messageRoutingRule);
  MessageRoutingRule toDAO(MessageRoutingRuleTO messageRoutingRuleTO);
}
