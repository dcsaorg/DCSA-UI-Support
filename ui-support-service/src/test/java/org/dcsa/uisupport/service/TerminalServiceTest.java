package org.dcsa.uisupport.service;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.uisupport.mapping.FacilityMapper;
import org.dcsa.uisupport.persistence.repository.FacilityRepository;
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

  @Mock private FacilityRepository facilityRepository;
  @InjectMocks TerminalService terminalService;
  @Spy FacilityMapper facilityMapper = Mappers.getMapper(FacilityMapper.class);

  @Test
  @DisplayName(
      "Retrieving a Facility should result in a list of terminals containing one terminalTO.")
  void testTerminalService() {
    given(facilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(
            List.of(
                Facility.builder()
                    .unLocationCode("NLRTM")
                    .bicCode("MBBA")
                    .smdgCode("NANS")
                    .name("UNIPORT WAALHAVEN TERMINAL")
                    .build()));

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(facilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(1);
    assertThat(terminals.get(0)).isInstanceOf(TerminalTO.class);
    assertThat(terminals.get(0).unLocationCode()).isEqualTo("NLRTM");
    assertThat(terminals.get(0).bicCode()).isEqualTo("MBBA");
  }

  @Test
  @DisplayName(
      "Retrieving multiple facilities should return in a list of terminals containing multiple terminalTO's.")
  void testTerminalServiceMultipleFacilities() {
    given(facilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(
            List.of(
                Facility.builder()
                    .unLocationCode("NLRTM")
                    .bicCode("MBBA")
                    .smdgCode("NANS")
                    .name("UNIPORT WAALHAVEN TERMINAL")
                    .build(),
                Facility.builder()
                    .unLocationCode("DEHAM")
                    .smdgCode("SWT")
                    .name("SUD-WEST TERMINAL")
                    .build()));

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(facilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(2);
    assertThat(terminals.get(0)).isInstanceOf(TerminalTO.class);
    assertThat(terminals.get(1).unLocationCode()).isEqualTo("DEHAM");
    assertThat(terminals.get(1).smdgCode()).isEqualTo("SWT");
  }

  @Test
  @DisplayName("No Facilities found should result in an empty list")
  void testNoFacilitiesFound() {
    given(facilityRepository.findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any()))
        .willReturn(Collections.emptyList());

    List<TerminalTO> terminals = terminalService.findFacilitiesForUnLocationCode("NLRTM");

    verify(facilityRepository, times(1)).findFacilitiesByUnLocationCodeAndSmdgCodeIsNotNull(any());
    assertThat(terminals.size()).isEqualTo(0);
  }
}
