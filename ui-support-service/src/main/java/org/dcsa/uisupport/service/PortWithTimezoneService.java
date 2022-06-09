package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.mapping.PortWithTimezoneMapper;
import org.dcsa.uisupport.persistence.repository.PortWithTimezoneRepository;
import org.dcsa.uisupport.transferobjects.PortWithTimezoneTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortWithTimezoneService {
  private final PortWithTimezoneRepository portWithTimezoneRepository;
  private final PortWithTimezoneMapper portWithTimezoneMapper;

  public List<PortWithTimezoneTO> findAll() {
    return portWithTimezoneRepository.findAll().stream()
      .map(portWithTimezoneMapper::toTO)
      .toList();
  }
}
