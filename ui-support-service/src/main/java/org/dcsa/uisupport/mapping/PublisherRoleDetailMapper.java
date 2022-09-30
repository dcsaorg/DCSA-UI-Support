package org.dcsa.uisupport.mapping;

import org.dcsa.uisupport.persistence.entity.PublisherRoleDetail;
import org.dcsa.uisupport.transferobjects.PublisherRoleDetailTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublisherRoleDetailMapper {
  PublisherRoleDetailTO toTO(PublisherRoleDetail publisherRoleDetail);
}
