package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.uisupport.mapping.PortCallPartMapper;
import org.dcsa.uisupport.persistence.repository.PortCallPartRepository;
import org.dcsa.uisupport.transferobjects.PortCallPartTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortCallPartService {
  private final PortCallPartRepository portCallPartRepository;
  private final PortCallPartMapper portCallPartMapper;

  public List<PortCallPartTO> findAll() {
    return portCallPartRepository.findAllByOrderByDisplayOrderAsc().stream()
      .map(portCallPartMapper::toTO)
      .toList();
  }
}
