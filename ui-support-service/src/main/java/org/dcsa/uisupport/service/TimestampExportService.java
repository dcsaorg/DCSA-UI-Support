package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.OperationsEvent;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.persistence.entity.OpsEventTimestampDefinition;
import org.dcsa.uisupport.persistence.entity.PortWithTimezone;
import org.dcsa.uisupport.persistence.repository.OpsEventTimestampDefinitionRepository;
import org.dcsa.uisupport.persistence.repository.PortWithTimezoneRepository;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampExportTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TimestampExportService {

  private final OpsEventTimestampDefinitionRepository opsEventTimestampDefinitionRepository;
  private final OperationsEventMapper operationsEventMapper;
  private final PortWithTimezoneRepository portWithTimezoneRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;


  @Transactional
  public List<TimestampExportTO> getExportableTimestamps() {
    ZoneId utc = ZoneId.of("Etc/UTC");
    Map<String, ZoneId> unLocCode2TZID = portWithTimezoneRepository.findAllByIanaTimezoneIsNotNull()
      .stream()
      .collect(Collectors.toMap(PortWithTimezone::getUnLocationCode, port -> ZoneId.of(port.getIanaTimezone())));
    return opsEventTimestampDefinitionRepository.findAll(fetchSpec(), Sort.by(Sort.Direction.ASC, "operationsEvent.createdDateTime"))
      .stream()
      .map(opsEventTimestampDefinition -> {
        OperationsEvent operationsEvent = opsEventTimestampDefinition.getOperationsEvent();
        OperationsEventTO operationsEventTO = operationsEventMapper.toTO(operationsEvent);
        TimestampDefinitionTO timestampDefinitionTO = timestampDefinitionMapper.toTO(opsEventTimestampDefinition.getTimestampDefinition());
        return TimestampExportTO.builder()
          .operationsEvent(operationsEventTO)
          .timestampDefinition(timestampDefinitionTO)
          .timezone(unLocCode2TZID.getOrDefault(operationsEvent.getTransportCall().getLocation().getUnLocationCode(), utc))
          .negotiationSequenceID(
            computeNegotiationSequenceID(
              operationsEvent.getTransportCall(),
              timestampDefinitionTO
            )
          ).build();
      }).collect(Collectors.toList());
  }

  private static String computeNegotiationSequenceID(TransportCall transportCall, TimestampDefinitionTO timestampDefinition) {
    return String.valueOf(transportCall.getId()) + '-' + timestampDefinition.negotiationCycle();
  }

  private static Specification<OpsEventTimestampDefinition> fetchSpec() {
    return (root, query, criteriaBuilder) -> {
      // Eager load *all the entities* - being a "dump every timestamp" query the laziness hurts a lot.
      Join<OpsEventTimestampDefinition, OperationsEvent> opsEventJoin = root.join("operationsEvent");
      Join<OperationsEvent, TransportCall> opsEvent2TCJoin = opsEventJoin.join("transportCall");
      Join<TransportCall, Location> locationJoin = opsEvent2TCJoin.join("location");
      locationJoin.join("address");
      locationJoin.join("facility");
      opsEventJoin.join("publisher").join("address");
      return criteriaBuilder.and();
    };
  }

}
