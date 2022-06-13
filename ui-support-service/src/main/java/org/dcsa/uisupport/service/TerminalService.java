package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.mapping.FacilityMapper;
import org.dcsa.uisupport.persistence.repository.UiFacilityRepository;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TerminalService {

  private final UiFacilityRepository uiFacilityRepository;
  private final FacilityMapper facilityMapper;

  public List<TerminalTO> findFacilitiesForUnLocationCode(String unLocationCode) {

    return uiFacilityRepository
        .findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(unLocationCode)
        .stream()
        .map(facilityMapper::facilityToTerminalTO)
        .toList();
  }
}
