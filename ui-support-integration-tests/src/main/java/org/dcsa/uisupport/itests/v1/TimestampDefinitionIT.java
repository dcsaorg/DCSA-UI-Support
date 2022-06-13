package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimestampDefinitionIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetTimestampDefinitions() {
    List<TimestampDefinitionTO> timestampDefinitions = given()
      .contentType("application/json")
      .get("/v1/unofficial/timestamp-definitions")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(9))
      .extract()
      .body()
      .jsonPath().getList(".", TimestampDefinitionTO.class)
      ;

    assertTrue(timestampDefinitions.stream().noneMatch(td -> td.id() == null));

    // Just test a single TimestampDefinition
    TimestampDefinitionTO expected = TimestampDefinitionTO.builder()
      .id("UC48-OUTB")
      .timestampTypeName("ATS-Towage (Outbound)")
      .publisherRole("TWG")
      .primaryReceiver("ATH")
      .eventClassifierCode("ACT")
      .operationsEventTypeCode("STRT")
      .portCallPhaseTypeCode("OUTB")
      .portCallServiceTypeCode("TOWG")
      .isBerthLocationNeeded(false)
      .isPBPLocationNeeded(false)
      .isTerminalNeeded(true)
      .isVesselPositionNeeded(true)
      .negotiationCycle("T-Towage-Outbound")
      .providedInStandard("jit1_1")
      .build();
    TimestampDefinitionTO actual = timestampDefinitions.stream().filter(td -> td.id().equals("UC48-OUTB")).findFirst().get();
    assertEquals(expected, actual);
  }
}
