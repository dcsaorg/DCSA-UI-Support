package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TimestampDefinitionMapper {
  TimestampDefinitionTO toTO(TimestampDefinition timestampDefinition);
}
