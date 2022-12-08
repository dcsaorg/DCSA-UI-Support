package org.dcsa.uisupport.service;

import lombok.AllArgsConstructor;
import org.dcsa.jit.mapping.VesselMapper;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.repository.VesselRepository;
import org.dcsa.jit.transferobjects.TransportCallVesselTO;
import org.dcsa.jit.transferobjects.UISupportVesselTO;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.skernel.domain.persistence.repository.CarrierRepository;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UiVesselService {

  private final VesselRepository vesselRepository;
  private final CarrierRepository carrierRepository;
  private final VesselMapper vesselMapper;

  public PagedResult<UISupportVesselTO> findAllRealVessels(Cursor cursor) {
    return new PagedResult<>(
      vesselRepository.findAllByIsDummyIsFalse(cursor.toPageRequest()),
      vesselMapper::toUISupportVesselTO
    );
  }

  public UISupportVesselTO fetchVesselByIMONumber(final String vesselIMONumber) {
    Optional<Vessel> foundVessel = vesselRepository.findByVesselIMONumber(vesselIMONumber);
    return vesselMapper.toUISupportVesselTO(
        foundVessel.orElseThrow(
            () -> ConcreteRequestErrorMessageException.notFound("Vessel not found.")));
  }

  @Transactional
  public UISupportVesselTO createVessel(final UISupportVesselTO request) {

    Carrier carrierForRequestedVessel = findCarrierForVesselRequest(request);

    Vessel vessel =
        vesselMapper.toEntity(request).toBuilder()
            .vesselOperatorCarrier(carrierForRequestedVessel)
            .build();

    Vessel saveVessel = vesselRepository.save(vessel);

    return setCarrierDetailsOnVesselTOIfPresent(request, carrierForRequestedVessel, saveVessel);
  }

  private Carrier findCarrierForVesselRequest(final UISupportVesselTO request) {
    Carrier carrier = null;
    if (null != request.vesselOperatorCarrierCode()) {
      // if vessel operator carrier code is present , we need carrier code list provider
      if (null == request.vesselOperatorCarrierCodeListProvider()) {
        throw ConcreteRequestErrorMessageException.invalidInput(
            "vesselOperatorCarrierCodeListProvider is required if vesselOperatorCarrierCode is provided.");
      } else {
        if (CarrierCodeListProvider.NMFTA.equals(request.vesselOperatorCarrierCodeListProvider())) {
          carrier = carrierRepository.findByNmftaCode(request.vesselOperatorCarrierCode());
        } else {
          carrier = carrierRepository.findBySmdgCode(request.vesselOperatorCarrierCode());
        }
        // if carrier is not present throw an error
        if (null == carrier) {
          throw ConcreteRequestErrorMessageException.notFound(
              "Cannot find any vessel operator with carrier code: "
                  + request.vesselOperatorCarrierCodeListProvider());
        }
      }
    }
    return carrier;
  }

  private UISupportVesselTO setCarrierDetailsOnVesselTOIfPresent(
    UISupportVesselTO request, Carrier carrierForVesselRequest, Vessel vessel) {
    if (null != carrierForVesselRequest) {
      if (CarrierCodeListProvider.SMDG.equals(request.vesselOperatorCarrierCodeListProvider())) {
        return vesselMapper.toUISupportVesselTO(vessel).toBuilder()
            .vesselOperatorCarrierCode(carrierForVesselRequest.getSmdgCode())
            .vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG)
            .build();
      } else
        return vesselMapper.toUISupportVesselTO(vessel).toBuilder()
            .vesselOperatorCarrierCode(carrierForVesselRequest.getNmftaCode())
            .vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.NMFTA)
            .build();
    } else {
      return vesselMapper.toUISupportVesselTO(vessel);
    }
  }

  @Transactional
  public UISupportVesselTO updateVessel(final String vesselIMONumber, final UISupportVesselTO request) {

    Optional<Vessel> foundVessel = vesselRepository.findByVesselIMONumber(vesselIMONumber);
    UUID id =
      foundVessel.orElseThrow(
        () -> ConcreteRequestErrorMessageException.notFound("Vessel not found.")).getId();
    Carrier carrierForRequestedVessel = this.findCarrierForVesselRequest(request);
    Vessel vessel =
        vesselMapper.toEntity(request).toBuilder()
            .id(id)
            .vesselOperatorCarrier(carrierForRequestedVessel)
            .build();

    Vessel updateVessel = vesselRepository.save(vessel);

    return this.setCarrierDetailsOnVesselTOIfPresent(
        request, carrierForRequestedVessel, updateVessel);
  }
}
