package org.dcsa.uisupport.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.repository.FacilityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UiFacilityRepository extends FacilityRepository {
  List<Facility> findFacilitiesByUNLocationCodeAndFacilitySMDGCodeIsNotNull(String unLocationCode);
}
