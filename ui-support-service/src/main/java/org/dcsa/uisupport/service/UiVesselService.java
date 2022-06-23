package org.dcsa.uisupport.service;

import lombok.AllArgsConstructor;
import org.dcsa.jit.mapping.VesselMapper;
import org.dcsa.jit.persistence.entity.Vessel;
import org.dcsa.jit.persistence.repository.VesselRepository;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.jit.transferobjects.enums.CarrierCodeListProvider;
import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.skernel.errors.exceptions.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.infrastructure.pagination.Cursor;
import org.dcsa.skernel.infrastructure.pagination.PagedResult;
import org.dcsa.uisupport.persistence.repository.CarrierRepository;
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

  public PagedResult<VesselTO> findAll(Cursor cursor) {
    return new PagedResult<>(
      vesselRepository.findAll(cursor.toPageRequest()),
      vesselMapper::toTO
    );
  }

  public VesselTO fetchVessel(final UUID id) {
    Optional<Vessel> foundVessel = vesselRepository.findById(id);
    return vesselMapper.toTO(
        foundVessel.orElseThrow(
            () -> ConcreteRequestErrorMessageException.notFound("Vessel not found.")));
  }

  @Transactional
  public VesselTO createVessel(final VesselTO request) {

    Carrier carrierForRequestedVessel = findCarrierForVesselRequest(request);

    Vessel vessel =
        vesselMapper.toEntity(request).toBuilder()
            .vesselOperatorCarrier(carrierForRequestedVessel)
            .build();

    Vessel saveVessel = vesselRepository.save(vessel);

    return setCarrierDetailsOnVesselTOIfPresent(request, carrierForRequestedVessel, saveVessel);
  }

  private Carrier findCarrierForVesselRequest(final VesselTO request) {
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

  private VesselTO setCarrierDetailsOnVesselTOIfPresent(
      VesselTO request, Carrier carrierForVesselRequest, Vessel vessel) {
    if (null != carrierForVesselRequest) {
      if (CarrierCodeListProvider.SMDG.equals(request.vesselOperatorCarrierCodeListProvider())) {
        return vesselMapper.toTO(vessel).toBuilder()
            .vesselOperatorCarrierCode(carrierForVesselRequest.getSmdgCode())
            .vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.SMDG)
            .build();
      } else
        return vesselMapper.toTO(vessel).toBuilder()
            .vesselOperatorCarrierCode(carrierForVesselRequest.getNmftaCode())
            .vesselOperatorCarrierCodeListProvider(CarrierCodeListProvider.NMFTA)
            .build();
    } else {
      return vesselMapper.toTO(vessel);
    }
  }

  @Transactional
  public VesselTO updateVessel(final UUID id, final VesselTO request) {

    this.fetchVessel(id); // this will throw an error if vessel does not exist
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
