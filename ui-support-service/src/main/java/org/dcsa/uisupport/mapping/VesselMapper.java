package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.transferobjects.VesselTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VesselMapper {
  VesselTO toTO(Vessel vessel);

  Vessel toEntity(VesselTO vesselTO);
}
