package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.OperationsEventMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.persistence.repository.TimestampInfoRepository;
import org.dcsa.jit.transferobjects.OperationsEventTO;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.uisupport.mapping.TimestampDefinitionMapper;
import org.dcsa.uisupport.persistence.entity.PortWithTimezone;
import org.dcsa.uisupport.persistence.repository.PortWithTimezoneRepository;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampExportTO;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TimestampExportService {

  private final TimestampInfoRepository timestampInfoRepository;
  private final OperationsEventMapper operationsEventMapper;
  private final PortWithTimezoneRepository portWithTimezoneRepository;
  private final TimestampDefinitionMapper timestampDefinitionMapper;


  @Transactional
  public List<TimestampExportTO> getExportableTimestamps() {
    ZoneId utc = ZoneId.of("Etc/UTC");
    Map<String, ZoneId> unLocCode2TZID = portWithTimezoneRepository.findAllByIanaTimezoneIsNotNull()
      .stream()
      .collect(Collectors.toMap(PortWithTimezone::getUNLocationCode, port -> ZoneId.of(port.getIanaTimezone())));
    return timestampInfoRepository.findAll(Sort.by(Sort.Direction.ASC, "operationsEvent.eventCreatedDateTime"))
      .stream()
      .map(opsEventTimestampDefinition -> {
        OperationsEvent operationsEvent = opsEventTimestampDefinition.getOperationsEvent();
        OperationsEventTO operationsEventTO = operationsEventMapper.toTO(operationsEvent);
        TimestampDefinitionTO timestampDefinitionTO = timestampDefinitionMapper.toTO(opsEventTimestampDefinition.getTimestampDefinition());
        Location tcLocation = operationsEvent.getTransportCall().getLocation();
        return TimestampExportTO.builder()
          .operationsEvent(operationsEventTO)
          .timestampDefinition(timestampDefinitionTO)
          .timezone(unLocCode2TZID.getOrDefault(tcLocation != null ? tcLocation.getUNLocationCode() : null, utc))
          .negotiationSequenceID(
            computeNegotiationSequenceID(
              operationsEvent.getTransportCall(),
              timestampDefinitionTO
            )
          ).build();
      }).toList();
  }

  private static String computeNegotiationSequenceID(TransportCall transportCall, TimestampDefinitionTO timestampDefinition) {
    return String.valueOf(transportCall.getId()) + '-' + timestampDefinition.negotiationCycle();
  }


}
