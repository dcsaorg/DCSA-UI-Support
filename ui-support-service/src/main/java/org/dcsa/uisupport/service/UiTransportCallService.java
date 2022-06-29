package org.dcsa.uisupport.service;

import lombok.RequiredArgsConstructor;
import org.dcsa.jit.mapping.EnumMappers;
import org.dcsa.jit.mapping.TransportCallMapper;
import org.dcsa.jit.persistence.entity.TransportCall;
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
import org.dcsa.uisupport.mapping.TransportCallWithTimestampsMapper;
import org.dcsa.uisupport.persistence.repository.TransportCallWithTimestampsRepository;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  public List<TransportCallWithTimestampsTO> findAll() {
    return transportCallWithTimestampsRepository.findAll().stream()
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
        .id(UUID.randomUUID().toString())
        .UNLocationCode(transportCallTO.UNLocationCode())
        .facility(findFacility(transportCallTO).orElse(null))
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

  private Optional<Facility> findFacility(TransportCallTO transportCallTO) {
    if (transportCallTO.facilityCodeListProvider() != null) {
      return switch (transportCallTO.facilityCodeListProvider()) {
        case SMDG ->
          facilityRepository.findByUNLocationCodeAndFacilitySMDGCode(transportCallTO.UNLocationCode(), transportCallTO.facilityCode());
        case BIC ->
          facilityRepository.findByUNLocationCodeAndFacilityBICCode(transportCallTO.UNLocationCode(), transportCallTO.facilityCode());
      };
    } else {
      return Optional.empty();
    }
  }
}
