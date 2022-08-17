package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.NegotiationCycle;
import org.dcsa.uisupport.transferobjects.NegotiationCycleTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NegotiationCycleMapper {

  NegotiationCycleTO toTO(NegotiationCycle negotiationCycle);
}
