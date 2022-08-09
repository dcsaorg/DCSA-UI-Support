package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.PublisherPatternTO;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.enums.LocationRequirement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    List<TimestampDefinitionTO> timestampDefinitions =
        given()
            .contentType("application/json")
            .get("/v1/unofficial/timestamp-definitions")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", greaterThanOrEqualTo(9))
            .extract()
            .body()
            .jsonPath()
            .getList(".", TimestampDefinitionTO.class);

    assertTrue(timestampDefinitions.stream().noneMatch(td -> td.id() == null));

    final String timestampTypeName = "ATS-Towage (Outbound)";

    // Just test a single TimestampDefinition
    TimestampDefinitionTO expected =
        TimestampDefinitionTO.builder()
            .id(timestampTypeName)
            .timestampTypeName(timestampTypeName)
            .eventClassifierCode("ACT")
            .operationsEventTypeCode("STRT")
            .portCallPhaseTypeCode("OUTB")
            .portCallServiceTypeCode("TOWG")
            .portCallPart("Port Departure Execution")
            .eventLocationRequirement(LocationRequirement.REQUIRED)
            .isTerminalNeeded(false)
            .isMilesToDestinationRelevant(false)
            .providedInStandard("jit1_1")
            .facilityTypeCode("BRTH")
            .negotiationCycle("T-Towage (Outbound)")
            .publisherPattern(
                Set.of(
                        new PublisherPatternTO("TWG2ATH", "TWG", "ATH"),
                        new PublisherPatternTO("ATH2TWG", "ATH", "TWG")
                    )
            ).build();

    TimestampDefinitionTO actual =
        timestampDefinitions.stream().filter(td -> td.timestampTypeName().equals(timestampTypeName)).findFirst().get();
    assertEquals(expected, actual);
    assertEquals(expected, actual);
  }
}
