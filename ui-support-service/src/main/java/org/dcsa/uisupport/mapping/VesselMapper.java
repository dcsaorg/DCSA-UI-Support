package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  VesselTO toTO(Vessel vessel);

  @Mapping(target = "isDummy", source = "isDummy", defaultValue = "false")
  Vessel toEntity(VesselTO vesselTO);
}
