package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.TransportCallMapper;
import org.dcsa.jit.persistence.entity.*;
import org.dcsa.jit.service.ServiceService;
import org.dcsa.jit.service.TransportCallService;
import org.dcsa.jit.service.VesselService;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.domain.persistence.entity.Location_;
import org.dcsa.skernel.domain.persistence.repository.LocationRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.uisupport.mapping.TransportCallWithTimestampsMapper;
import org.dcsa.uisupport.persistence.entity.JITPortVisitUIContext;
import org.dcsa.uisupport.persistence.entity.JITPortVisitUIContext_;
import org.dcsa.uisupport.persistence.repository.JITPortVisitUIContextRepository;
import org.dcsa.uisupport.persistence.repository.UiFacilityRepository;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UiTransportCallService {
  private final TransportCallMapper transportCallMapper;
  private final TransportCallWithTimestampsMapper transportCallWithTimestampsMapper;
  private final EnumMappers enumMappers;
  private final ServiceService serviceService;
  private final VesselService vesselService;
  private final TransportCallService transportCallService;
  private final LocationRepository locationRepository;
  private final UiFacilityRepository facilityRepository;
  private final JITPortVisitUIContextRepository jitPortVisitUIContextRepository;

  @Transactional
  public List<TransportCallWithTimestampsTO> findAll(String unLocationCode, String vesselIMONumber) {
    // This is a bit messy - the UI really wants a port visit, but there is no well-defined
    // Entity for it.  We approximate it with one variant of the transport call to reduce
    // the amount of changes required for the UI (short term win, for long term pain).
    // But at least "It works(tm)!" ...
    return jitPortVisitUIContextRepository.findAll(fetchSpec(unLocationCode, vesselIMONumber)).stream()
      .map(transportCallWithTimestampsMapper::toTO)
      .toList();
  }

  @Transactional
  public TransportCallWithTimestampsTO create(TransportCallTO transportCallTO) {

    if (transportCallTO.exportVoyageNumber() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where export voyage number is missing");
    } else if (transportCallTO.importVoyageNumber() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where export voyage number is missing");
    } else if (transportCallTO.carrierServiceCode() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where service code is missing");
    } else if (transportCallTO.modeOfTransport() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where modeOfTransport is missing");
    } else if (transportCallTO.UNLocationCode() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where UNLocationCode is missing");
    } else if (transportCallTO.vessel() == null || transportCallTO.vessel().vesselIMONumber() == null) {
      throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where vessel.vesselIMONumber is missing");
    } else if (transportCallTO.facilityCode() == null ^ transportCallTO.facilityCodeListProvider() == null) {
      if (transportCallTO.facilityCode() == null) {
        throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where facility code list provider is present but facility code is missing");
      } else {
        throw ConcreteRequestErrorMessageException.invalidInput("Cannot create transport call where facility code is present but facility code list provider is missing");
      }
    }

    Location location = locationRepository.save(
      Location.builder()
        .UNLocationCode(transportCallTO.UNLocationCode())
        .facility(findFacility(transportCallTO))
        .build()
    );
    org.dcsa.jit.persistence.entity.Service service =
      serviceService.ensureServiceExistsByCarrierServiceCode(transportCallTO.carrierServiceCode());

    TransportCall entityToSave = TransportCall.builder()
      .transportCallReference(UUID.randomUUID().toString())
      .transportCallSequenceNumber(Objects.requireNonNullElse(transportCallTO.transportCallSequenceNumber(), 1))
      .facilityTypeCode(enumMappers.facilityTypeCodeToDao(transportCallTO.facilityTypeCode()))
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(transportCallTO.modeOfTransport()).getCode().toString())
      .vessel(vesselService.ensureVesselExistsByImoNumber(transportCallTO.vessel().vesselIMONumber()))
      .importVoyage(Voyage.builder().carrierVoyageNumber(transportCallTO.importVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(transportCallTO.exportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .build();

    TransportCall saved = transportCallService.create(entityToSave);

    return TransportCallWithTimestampsTO.builder()
      .transportCallID(saved.getId()) // Just adding the id for easier testing
      .transportCallTO(transportCallMapper.toTO(saved))
      .build();
  }

  private Facility findFacility(TransportCallTO transportCallTO) {
    if (transportCallTO.facilityCodeListProvider() != null) {
      return (switch (transportCallTO.facilityCodeListProvider()) {
        case SMDG ->
          facilityRepository.findByUNLocationCodeAndFacilitySMDGCode(transportCallTO.UNLocationCode(), transportCallTO.facilityCode());
        case BIC ->
          facilityRepository.findByUNLocationCodeAndFacilityBICCode(transportCallTO.UNLocationCode(), transportCallTO.facilityCode());
      }).orElseThrow(() ->
        ConcreteRequestErrorMessageException.invalidInput(
          "No facility found with " + transportCallTO.facilityCodeListProvider() + " code = '" + transportCallTO.facilityCode()
            + "' and UNLocationCode = '" + transportCallTO.UNLocationCode() + "'"));
    } else {
      return null;
    }
  }

  private static Specification<JITPortVisitUIContext> fetchSpec(String unLocationCode, String vesselIMONumber) {
    return (root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();
      Join<JITPortVisitUIContext, TransportCall> portVisitJoin = root.join(JITPortVisitUIContext_.JIT_PORT_VISIT);

      if (null != unLocationCode) {
        Join<TransportCall, Location> locationJoin = portVisitJoin.join(TransportCall_.LOCATION);
        Predicate predicate = builder.equal(locationJoin.get(Location_.U_NLOCATION_CODE), unLocationCode);
        predicates.add(predicate);
      }

      if (null != vesselIMONumber) {
        Join<TransportCall, Vessel> vesselJoin = portVisitJoin.join(TransportCall_.VESSEL);
        Predicate predicate = builder.equal(vesselJoin.get(Vessel_.VESSEL_IM_ONUMBER), vesselIMONumber);
        predicates.add(predicate);
      }

      query.orderBy(builder.desc(root.get(JITPortVisitUIContext_.LATEST_EVENT_CREATED_DATE_TIME)));

      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
