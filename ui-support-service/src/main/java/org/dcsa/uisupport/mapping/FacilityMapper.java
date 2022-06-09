package org.dcsa.uisupport.mapping;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
  TerminalTO facilityToTerminalTO(Facility facility);
}
