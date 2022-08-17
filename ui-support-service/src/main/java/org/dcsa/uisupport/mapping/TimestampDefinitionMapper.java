package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NegotiationCycleMapper.class)
public interface TimestampDefinitionMapper {
  TimestampDefinitionTO toTO(TimestampDefinition timestampDefinition);
}
