package org.dcsa.uisupport.mapping;

import org.dcsa.uisupport.persistence.entity.PortCallPart;
import org.dcsa.uisupport.transferobjects.PortCallPartTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortCallPartMapper {

  PortCallPartTO toTO(PortCallPart portCallPart);
}
