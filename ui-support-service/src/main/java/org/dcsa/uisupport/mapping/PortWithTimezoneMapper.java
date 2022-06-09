package org.dcsa.uisupport.mapping;

import org.dcsa.uisupport.persistence.entity.PortWithTimezone;
import org.dcsa.uisupport.transferobjects.PortWithTimezoneTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortWithTimezoneMapper {
  PortWithTimezoneTO toTO(PortWithTimezone portWithTimezone);
}
