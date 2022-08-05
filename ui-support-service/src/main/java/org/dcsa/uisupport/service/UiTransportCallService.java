package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.TransportCallMapper;
import org.dcsa.jit.persistence.entity.TransportCall;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.entity.Voyage;
import org.dcsa.jit.persistence.repository.FacilityRepository;
import org.dcsa.jit.persistence.repository.LocationRepository;
import org.dcsa.jit.persistence.repository.TransportCallRepository;
import org.dcsa.jit.service.ServiceService;
import org.dcsa.jit.service.VesselService;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.domain.persistence.entity.Location;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.uisupport.mapping.TransportCallWithTimestampsMapper;
import org.dcsa.uisupport.persistence.entity.TransportCallWithTimestamps;
import org.dcsa.uisupport.persistence.repository.TransportCallWithTimestampsRepository;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UiTransportCallService {
  private final TransportCallWithTimestampsRepository transportCallWithTimestampsRepository;
  private final TransportCallWithTimestampsMapper transportCallWithTimestampsMapper;
  private final TransportCallMapper transportCallMapper;
  private final EnumMappers enumMappers;
  private final ServiceService serviceService;
  private final VesselService vesselService;
  private final TransportCallRepository transportCallRepository;
  private final LocationRepository locationRepository;
  private final FacilityRepository facilityRepository;

  @Transactional
  public  PagedResult<TransportCallWithTimestampsTO> findAll(
      String vesselIMONumber, String unLocationCode, String facilitySMDGCode, Cursor cursor) {

    return new PagedResult<>(
      transportCallWithTimestampsRepository
        .findAll(fetchSpec(vesselIMONumber, unLocationCode, facilitySMDGCode),
        cursor.toPageRequest()),
      transportCallWithTimestampsMapper::toTO);
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
      .facility(null) // Go through location to find facility,
      .facilityTypeCode(enumMappers.facilityTypeCodeToDao(transportCallTO.facilityTypeCode()))
      .location(location)
      .modeOfTransportCode(enumMappers.modeOfTransportToDao(transportCallTO.modeOfTransport()).getCode().toString())
      .vessel(vesselService.ensureVesselExistsByImoNumber(transportCallTO.vessel().vesselIMONumber()))
      .importVoyage(Voyage.builder().carrierVoyageNumber(transportCallTO.importVoyageNumber()).service(service).build())
      .exportVoyage(Voyage.builder().carrierVoyageNumber(transportCallTO.exportVoyageNumber()).service(service).build())
      .portCallStatusCode(null)
      .build();

    TransportCall saved = transportCallRepository.save(entityToSave);

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

  public static Specification<TransportCallWithTimestamps> fetchSpec(String vesselIMONumber, String unLocationCode, String facilitySMDGCode ) {
    return (root, query, builder) -> {
      Join<TransportCallWithTimestamps, Vessel> transportCallVesselJoin = root.join("vessel", JoinType.LEFT);
      Join<TransportCallWithTimestamps, Location> transportCallLocationJoin = root.join("location", JoinType.LEFT);
      Join<Location, Facility> locationFacilityJoin = transportCallLocationJoin.join("facility", JoinType.LEFT);
      List<Predicate> predicates = new ArrayList<>();

      if (null != vesselIMONumber) {
        Predicate predicate =
          builder.equal(transportCallVesselJoin.get("vesselIMONumber"), vesselIMONumber);
        predicates.add(predicate);
      }

      if (null != unLocationCode) {
        Predicate predicate =
          builder.equal(transportCallLocationJoin.get("UNLocationCode"), unLocationCode);
        predicates.add(predicate);
      }

      if (null != facilitySMDGCode) {
        Predicate predicate;
        if (facilitySMDGCode.equalsIgnoreCase("null")) {
          predicate = builder.isNull(locationFacilityJoin.get("facilitySMDGCode"));
        } else {
          predicate = builder.equal(locationFacilityJoin.get("facilitySMDGCode"), facilitySMDGCode);
        }
        predicates.add(predicate);
      }
      return builder.and(predicates.toArray(Predicate[]::new));
    };
  }
}
