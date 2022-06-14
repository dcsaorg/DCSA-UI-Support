package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.TimestampDefinitionRepository;
import org.dcsa.jit.persistence.repository.specification.OperationsEventSpecification;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimestampInfoService {
  private final TimestampDefinitionRepository timestampDefinitionRepository;
  private final OperationsEventRepository operationsEventRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;

  public List<TimestampDefinitionTO> findAll(String transportCallID, String negotiationCycle) {

    List<OperationsEvent> operationsEvents =
        operationsEventRepository.findAll(
            OperationsEventSpecification.withFilters(
                OperationsEventSpecification.OperationsEventFilters.builder()
                    .transportCallID(transportCallID)
                    .build()));

    return timestampDefinitionRepository.findAll().stream()
        .map(timestampDefinitionMapper::toTO)
        .toList();
  }
}
