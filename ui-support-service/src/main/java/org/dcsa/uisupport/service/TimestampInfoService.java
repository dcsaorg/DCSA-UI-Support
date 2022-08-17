package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.repository.TimestampInfoRepository;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.dcsa.uisupport.transferobjects.enums.EventDeliveryStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimestampInfoService {
  private final TimestampInfoRepository timestampInfoRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;
  private final OperationsEventMapper operationsEventMapper;
  public List<TimestampInfoTO> findAll(String portVisitID, String negotiationCycle, String portCallPart) {

    return timestampInfoRepository
        .findAll(fetchSpec(portVisitID, negotiationCycle, portCallPart))
        .stream()
        .map(
            opsEventTimestampDefinition -> {
              EventDeliveryStatus eventDeliveryStatus;
              if (opsEventTimestampDefinition.getPendingEvents() != null && !opsEventTimestampDefinition.getPendingEvents().isEmpty()) {
                boolean deliveryAttempted = opsEventTimestampDefinition.getPendingEvents().stream()
                  .anyMatch(e -> e.getLastAttemptDateTime() != null);
                eventDeliveryStatus =  deliveryAttempted ? EventDeliveryStatus.ATTEMPTED_DELIVERY : EventDeliveryStatus.PENDING_DELIVERY;
              } else if (opsEventTimestampDefinition.getUnmappedEvent() != null) {
                eventDeliveryStatus = EventDeliveryStatus.PENDING_DELIVERY;
              } else {
                eventDeliveryStatus = EventDeliveryStatus.DELIVERY_FINISHED;
              }

              OperationsEventTO operationsEventTO = operationsEventMapper.toTO(opsEventTimestampDefinition.getOperationsEvent());
              TimestampDefinitionTO timestampDefinitionTO = timestampDefinitionMapper.toTO(opsEventTimestampDefinition.getTimestampDefinition());
              return TimestampInfoTO.builder().operationsEventTO(operationsEventTO).timestampDefinitionTO(timestampDefinitionTO).eventDeliveryStatus(eventDeliveryStatus).build();
            })
        .toList();
  }

  private static Specification<TimestampInfo> fetchSpec(
    String portVisitID, String negotiationCycle, String portCallPart) {
    return (root, query, builder) -> {
      Join<TimestampInfo, OperationsEvent> operationsEventJoin = root.join(TimestampInfo_.OPERATIONS_EVENT);
      Join<TimestampInfo, TimestampDefinition> timestampDefinitionJoin = root.join(TimestampInfo_.TIMESTAMP_DEFINITION);

      List<Predicate> predicates = new ArrayList<>();

      if (null != portVisitID) {
        Join<OperationsEvent, TransportCall> portVisitJoin = operationsEventJoin
          .join(OperationsEvent_.TRANSPORT_CALL)
          .join(TransportCall_.PORT_VISIT);
        Predicate predicate = builder.equal(portVisitJoin.get("id"), UUID.fromString(portVisitID));
        predicates.add(predicate);
      }

      if (null != negotiationCycle) {
        Join<TimestampDefinition, NegotiationCycle> negotiationCycleJoin = timestampDefinitionJoin.join(TimestampDefinition_.NEGOTIATION_CYCLE);
        Predicate predicate = builder.equal(negotiationCycleJoin.get(NegotiationCycle_.CYCLE_KEY), negotiationCycle);
        predicates.add(predicate);
      }

      if (null != portCallPart) {
        Predicate predicate = builder.equal(timestampDefinitionJoin.get("portCallPart"), portCallPart);
        predicates.add(predicate);
      }

      query.orderBy(builder.desc(operationsEventJoin.get("eventCreatedDateTime")));

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
