package org.dcsa.uisupport.persistence.repository;

import org.dcsa.uisupport.persistence.entity.PortWithTimezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortWithTimezoneRepository extends JpaRepository<PortWithTimezone, String> {

  List<PortWithTimezone> findAllByIanaTimezoneIsNotNull();
}
