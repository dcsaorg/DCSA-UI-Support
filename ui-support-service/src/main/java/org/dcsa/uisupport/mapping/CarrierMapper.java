package org.dcsa.uisupport.mapping;

import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierMapper {
  CarrierTO toTO(Carrier carrier);
}
