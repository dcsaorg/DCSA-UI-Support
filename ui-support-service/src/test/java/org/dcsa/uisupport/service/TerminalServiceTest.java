package org.dcsa.uisupport.service;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.uisupport.mapping.FacilityMapper;
import org.dcsa.uisupport.persistence.repository.UiFacilityRepository;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for the terminal service")
class TerminalServiceTest {

  @Mock private UiFacilityRepository uiFacilityRepository;
  @InjectMocks TerminalService terminalService;
  @Spy FacilityMapper facilityMapper = Mappers.getMapper(FacilityMapper.class);

  @Test
  @DisplayName(
      "Retrieving a Facility should result in a list of terminals containing one terminalTO.")
  void testTerminalService() {
    given(uiFacilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(
            List.of(
                Facility.builder()
                    .UNLocationCode("NLRTM")
                    .facilityBICCode("MBBA")
                    .facilitySMDGCode("NANS")
                    .facilityName("UNIPORT WAALHAVEN TERMINAL")
                    .build()));

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(uiFacilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(1);
    assertThat(terminals.get(0)).isInstanceOf(TerminalTO.class);
    assertThat(terminals.get(0).UNLocationCode()).isEqualTo("NLRTM");
    assertThat(terminals.get(0).facilityBICCode()).isEqualTo("MBBA");
  }

  @Test
  @DisplayName(
      "Retrieving multiple facilities should return in a list of terminals containing multiple terminalTO's.")
  void testTerminalServiceMultipleFacilities() {
    given(uiFacilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(
            List.of(
                Facility.builder()
                    .UNLocationCode("NLRTM")
                    .facilityBICCode("MBBA")
                    .facilitySMDGCode("NANS")
                    .facilityName("UNIPORT WAALHAVEN TERMINAL")
                    .build(),
                Facility.builder()
                    .UNLocationCode("DEHAM")
                    .facilitySMDGCode("SWT")
                    .facilityName("SUD-WEST TERMINAL")
                    .build()));

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(uiFacilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(2);
    assertThat(terminals.get(0)).isInstanceOf(TerminalTO.class);
    assertThat(terminals.get(1).UNLocationCode()).isEqualTo("DEHAM");
    assertThat(terminals.get(1).facilitySMDGCode()).isEqualTo("SWT");
  }

  @Test
  @DisplayName("No Facilities found should result in an empty list")
  void testNoFacilitiesFound() {
    given(uiFacilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(Collections.emptyList());

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(uiFacilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(0);
  }
}
