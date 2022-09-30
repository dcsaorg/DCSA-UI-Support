package org.dcsa.uisupport.persistence.repository;

import org.dcsa.jit.persistence.entity.enums.PublisherRole;
import org.dcsa.uisupport.persistence.entity.PublisherRoleDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PublisherRoleRepository extends JpaRepository<PublisherRoleDetail, PublisherRole> {

  default List<PublisherRoleDetail> findAllPublisherRoles() {
    return findAllByPublisherRoleIn(Arrays.asList(PublisherRole.values()));
  }

  List<PublisherRoleDetail> findAllByPublisherRoleIn(List<PublisherRole> publisherRoles);
}
