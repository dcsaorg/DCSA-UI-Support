package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.repository.OperationsEventRepository;
import org.dcsa.jit.persistence.repository.UnmappedEventRepository;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.persistence.entity.PendingEvent;
import org.dcsa.uisupport.persistence.repository.OpsEventTimestampDefinitionRepositoryForUI;
import org.dcsa.uisupport.persistence.repository.PendingEventRepository;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.dcsa.uisupport.transferobjects.enums.EventDeliveryStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimestampInfoService {

  private final OperationsEventRepository operationsEventRepository;
  private final UnmappedEventRepository unmappedEventRepository;
  private final PendingEventRepository pendingEventRepository;
  private final OpsEventTimestampDefinitionRepositoryForUI opsEventTimestampDefinitionRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;
  private final OperationsEventMapper operationsEventMapper;

  public List<TimestampInfoTO> findAll(String transportCallID, String negotiationCycle) {
    return opsEventTimestampDefinitionRepository
        .findAll(fetchSpec(transportCallID, negotiationCycle))
        .stream()
        .map(
            opsEventTimestampDefinition -> {
              Optional<UnmappedEvent> unmapped = unmappedEventRepository.findById(opsEventTimestampDefinition.getEventID());
              Optional<PendingEvent> pending = pendingEventRepository.findById(opsEventTimestampDefinition.getEventID());

              EventDeliveryStatus eventDeliveryStatus;
              if (pending.isPresent()) {
                eventDeliveryStatus = EventDeliveryStatus.PENDING_DELIVERY;
              } else if (unmapped.isPresent()) {
                eventDeliveryStatus = EventDeliveryStatus.ATTEMPTED_DELIVERY;
              } else {
                eventDeliveryStatus = EventDeliveryStatus.DELIVERY_FINISHED;
              }

              OperationsEventTO operationsEventTO = operationsEventMapper.toTO(opsEventTimestampDefinition.getOperationsEvent());
              TimestampDefinitionTO timestampDefinitionTO = timestampDefinitionMapper.toTO(opsEventTimestampDefinition.getTimestampDefinition());
              return TimestampInfoTO.builder().operationsEventTO(operationsEventTO).timestampDefinitionTO(timestampDefinitionTO).eventDeliveryStatus(eventDeliveryStatus).build();
            })
        .collect(Collectors.toList());
  }

  private static Specification<OpsEventTimestampDefinition> fetchSpec(
      String transportCallID, String negotiationCycle) {
    return (root, query, builder) -> {
      // Eager load *all the entities* -
      // being a "dump every timestamp" query the laziness hurts a lot.
      Join<OpsEventTimestampDefinition, OperationsEvent> opsEventJoin = root.join("operationsEvent");
      Join<OperationsEvent, TransportCall> operationsEventTransportCallJoin = opsEventJoin.join("transportCall");
      Join<OpsEventTimestampDefinition, TimestampDefinition> timestampDefinitionJoin = root.join("timestampDefinition");

      List<Predicate> predicates = new ArrayList<>();

      if (null != transportCallID) {
        Predicate predicate = builder.equal(operationsEventTransportCallJoin.get("id"), UUID.fromString(transportCallID));
        predicates.add(predicate);
      }

      if (null != negotiationCycle) {
        Predicate predicate = builder.equal(timestampDefinitionJoin.get("negotiationCycle"), negotiationCycle);
        predicates.add(predicate);
      }

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
