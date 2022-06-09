package org.dcsa.uisupport.persistence.repository;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {
  @Query("""
    select facility from Facility facility
    where (:unLocationCode is null or facility.unLocationCode = :unLocationCode)
    and facility.smdgCode is not null
    """)
  List<Facility> findFacilities(String unLocationCode);

  List<Facility> findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(String unLocationCode);
}
