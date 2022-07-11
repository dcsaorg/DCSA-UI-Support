package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
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
            .publisherRole(null)
            .primaryReceiver(null)
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
            .publisherPattern(
                Stream.of(
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "CA2TWG");
                            put("publisherRole", "CA");
                            put("primaryReceiver", "TWG");
                          }
                        },
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "TWG2CA");
                            put("publisherRole", "TWG");
                            put("primaryReceiver", "CA");
                          }
                        },
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "VSL2TWG");
                            put("publisherRole", "VSL");
                            put("primaryReceiver", "TWG");
                          }
                        },
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "AG2TWG");
                            put("publisherRole", "AG");
                            put("primaryReceiver", "TWG");
                          }
                        },
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "TWG2VSL");
                            put("publisherRole", "TWG");
                            ;
                            put("primaryReceiver", "VSL");
                          }
                        },
                        new LinkedHashMap<String, String>() {
                          {
                            put("id", "TWG2AG");
                            put("publisherRole", "TWG");
                            put("primaryReceiver", "AG");
                          }
                        })
                    .collect(Collectors.toCollection(HashSet::new)))
            .build();

    TimestampDefinitionTO actual =
        timestampDefinitions.stream().filter(td -> td.id().equals("TS197")).findFirst().get();
    assertEquals(expected, actual);
    assertEquals(expected, actual);
  }
}
