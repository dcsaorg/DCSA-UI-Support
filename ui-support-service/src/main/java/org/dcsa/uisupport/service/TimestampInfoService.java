package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.repository.TimestampInfoRepository;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Facility_;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.entity.Location_;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.dcsa.jit.transferobjects.enums.DeliveryStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
  private final EnumMappers enumMappers;

  public List<TimestampInfoTO> findAll(String portVisitID, String negotiationCycle, String facilitySMDGCode, String portCallPart) {

    return timestampInfoRepository
        .findAll(fetchSpec(portVisitID, negotiationCycle, facilitySMDGCode, portCallPart))
        .stream()
        .map(
            opsEventTimestampDefinition -> {
              DeliveryStatus deliveryStatus = DeliveryStatus.DELIVERY_FINISHED;
              if (opsEventTimestampDefinition.getEventSyncState() != null) {
                deliveryStatus = enumMappers.deliveryStatusToTO(opsEventTimestampDefinition.getEventSyncState().getDeliveryStatus());
              }
              OperationsEventTO operationsEventTO = operationsEventMapper.toTO(opsEventTimestampDefinition.getOperationsEvent());
              TimestampDefinitionTO timestampDefinitionTO = timestampDefinitionMapper.toTO(opsEventTimestampDefinition.getTimestampDefinition());
              return TimestampInfoTO.builder().operationsEventTO(operationsEventTO).timestampDefinitionTO(timestampDefinitionTO).eventDeliveryStatus(deliveryStatus).build();
            })
        .toList();
  }

  private static Specification<TimestampInfo> fetchSpec(
    String portVisitID, String negotiationCycle, String facilitySMDGCode, String portCallPart) {
    return (root, query, builder) -> {
      Join<TimestampInfo, OperationsEvent> operationsEventJoin = root.join(TimestampInfo_.OPERATIONS_EVENT);
      Join<TimestampInfo, TimestampDefinition> timestampDefinitionJoin = root.join(TimestampInfo_.TIMESTAMP_DEFINITION);
      Join<OperationsEvent, TransportCall> transportCallJoin = operationsEventJoin.join(OperationsEvent_.TRANSPORT_CALL);

      List<Predicate> predicates = new ArrayList<>();

      if (null != portVisitID) {
        Join<TransportCall, TransportCall> portVisitJoin = transportCallJoin.join(TransportCall_.PORT_VISIT);
        Predicate predicate = builder.equal(portVisitJoin.get(TransportCall_.ID), UUID.fromString(portVisitID));
        predicates.add(predicate);
      }

      if (null != negotiationCycle) {
        Join<TimestampDefinition, NegotiationCycle> negotiationCycleJoin = timestampDefinitionJoin.join(TimestampDefinition_.NEGOTIATION_CYCLE);
        Predicate predicate = builder.equal(negotiationCycleJoin.get(NegotiationCycle_.CYCLE_KEY), negotiationCycle);
        predicates.add(predicate);
      }

      if (null != portCallPart) {
        Predicate predicate = builder.equal(timestampDefinitionJoin.get(TimestampDefinition_.PORT_CALL_PART), portCallPart);
        predicates.add(predicate);
      }

      if (null != facilitySMDGCode) {
        Join<Location, Facility> facilityJoin = transportCallJoin.join(TransportCall_.LOCATION)
          .join(Location_.FACILITY, JoinType.LEFT);
        Predicate predicate;
        if (facilitySMDGCode.equals("NULL")) {
          predicate = builder.isNull(facilityJoin.get(Facility_.FACILITY_SM_DG_CODE));
        } else {
          predicate = builder.equal(facilityJoin.get(Facility_.FACILITY_SM_DG_CODE), facilitySMDGCode);
        }
        predicates.add(predicate);
      }

      query.orderBy(builder.desc(operationsEventJoin.get("eventCreatedDateTime")));

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
