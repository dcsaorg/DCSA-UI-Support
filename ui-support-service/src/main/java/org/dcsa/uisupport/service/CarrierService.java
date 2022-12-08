package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.skernel.domain.persistence.repository.CarrierRepository;
import org.dcsa.uisupport.mapping.CarrierMapper;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrierService {
  private final CarrierRepository carrierRepository;
  private final CarrierMapper carrierMapper;

  public List<CarrierTO> findAll() {
    return carrierRepository.findAll().stream()
      .map(carrierMapper::toTO)
      .toList();
  }
}
