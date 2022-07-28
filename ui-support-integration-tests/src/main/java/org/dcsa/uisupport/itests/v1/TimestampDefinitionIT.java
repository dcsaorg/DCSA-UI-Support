package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.PublisherPatternTO;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
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

    // Just test a single TimestampDefinition
    TimestampDefinitionTO expected =
        TimestampDefinitionTO.builder()
            .id("TS197")
            .timestampTypeName("ATS-Towage (Outbound)")
            .eventClassifierCode("ACT")
            .operationsEventTypeCode("STRT")
            .portCallPhaseTypeCode("OUTB")
            .portCallServiceTypeCode("TOWG")
            .portCallPart("Port Departure Execution")
            .isBerthLocationNeeded(true)
            .isPBPLocationNeeded(false)
            .isAnchorageLocationNeeded(false)
            .isTerminalNeeded(true)
            .isVesselPositionNeeded(false)
            .providedInStandard("jit1_1")
            .facilityTypeCode("BRTH")
            .negotiationCycle("T-Towage-Outbound")
            .publisherPattern(
                Stream.of(
                        new PublisherPatternTO("CA2TWG", "CA", "TWG"),
                        new PublisherPatternTO("TWG2CA", "TWG", "CA"),
                        new PublisherPatternTO("VSL2TWG", "VSL", "TWG"),
                        new PublisherPatternTO("AG2TWG", "AG", "TWG"),
                        new PublisherPatternTO("TWG2VSL", "TWG", "VSL"),
                        new PublisherPatternTO("TWG2AG", "TWG", "AG"))
                    .collect(Collectors.toCollection(HashSet::new)))
            .build();

    TimestampDefinitionTO actual =
        timestampDefinitions.stream().filter(td -> td.id().equals("TS197")).findFirst().get();
    assertEquals(expected, actual);
    assertEquals(expected, actual);
  }
}
