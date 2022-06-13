package org.dcsa.uisupport.persistence.repository;

import org.dcsa.jit.persistence.repository.FacilityRepository;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UiFacilityRepository extends FacilityRepository {
  List<Facility> findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(String unLocationCode);
}
