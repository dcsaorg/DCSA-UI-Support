package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TerminalsIT {

  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetTerminals() {
    List<TerminalTO> terminals = given()
      .contentType("application/json")
      .queryParam("UNLocationCode", "NLRTM")
      .get("/v1/unofficial/terminals")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(18))
      .extract()
      .body()
      .jsonPath().getList(".", TerminalTO.class)
      ;

    assertTrue(terminals.stream().noneMatch(terminal -> terminal.smdgCode() == null));

    // Just test a single carrier
    TerminalTO apmTerminal = terminals.stream().filter(terminal -> terminal.smdgCode().equals("APM")).findFirst().get();
    assertEquals("APM TERMINALS ROTTERDAM", apmTerminal.name());
  }
}
