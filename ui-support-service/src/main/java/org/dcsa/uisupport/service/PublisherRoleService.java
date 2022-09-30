package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.mapping.PublisherRoleDetailMapper;
import org.dcsa.uisupport.persistence.repository.PublisherRoleRepository;
import org.dcsa.uisupport.transferobjects.PublisherRoleDetailTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherRoleService {

  private final PublisherRoleRepository publisherRoleRepository;
  private final PublisherRoleDetailMapper publisherRoleDetailMapper;

  public List<PublisherRoleDetailTO> getPublisherRoleDetails() {
    return publisherRoleRepository.findAllPublisherRoles()
      .stream()
      .map(publisherRoleDetailMapper::toTO)
      .toList();
  }
}
