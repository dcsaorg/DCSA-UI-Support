package org.dcsa.uisupport.controller;

import org.dcsa.uisupport.service.TerminalService;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Test for terminal endpoint")
@WebMvcTest(controllers = TerminalController.class)
class FacilityControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean TerminalService terminalService;

  @Test
  @DisplayName("GET terminal for a unLocationCode should return 200 for given basic valid call")
  void testGetTerminalWithUnLocationCode() throws Exception {
    when(terminalService.findFacilitiesForUnLocationCode(any()))
        .thenReturn(
            List.of(
                TerminalTO.builder()
                    .unLocationCode("NLRTM")
                    .name("UNIPORT WAALHAVEN TERMINAL")
                    .smdgCode("UMTR")
                    .build()));

    mockMvc
        .perform(
            get("/unofficial/terminals")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("UNLocationCode", "NLRTM"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].facilitySMDGCode").value("UMTR"));
  }

  @Test
  @DisplayName("Get terminal without a unLocationCode should return a 400.")
  void testGetTerminalWithoutUnLocationCode() throws Exception {
    mockMvc
        .perform(get("/unofficial/terminals").accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Get terminal without results should return an empty list.")
  void testGetTerminalNoResultsFound() throws Exception {
    when(terminalService.findFacilitiesForUnLocationCode(any()))
        .thenReturn(Collections.emptyList());
    mockMvc
        .perform(
            get("/unofficial/terminals")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("UNLocationCode", "DEHAM"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }
}
