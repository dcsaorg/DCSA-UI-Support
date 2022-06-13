package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimestampDefinitionService {
  private final TimestampDefinitionRepository timestampDefinitionRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;

  public List<TimestampDefinitionTO> findAll() {
    return timestampDefinitionRepository.findAll().stream()
      .map(timestampDefinitionMapper::toTO)
      .toList();
  }
}
