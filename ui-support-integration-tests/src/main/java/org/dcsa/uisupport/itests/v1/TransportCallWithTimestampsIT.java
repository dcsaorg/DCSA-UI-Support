package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.jit.transferobjects.VesselTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransportCallWithTimestampsIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetTransportCalls() {
    List<TransportCallWithTimestampsTO> calls = given()
      .contentType("application/json")
      .get("/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(3))
      .extract()
      .body()
      .jsonPath().getList(".", TransportCallWithTimestampsTO.class)
      ;
  }

  @Test
  public void testPostTransportCalls() {
    TransportCallTO newTransportCall = TransportCallTO.builder()
      .exportVoyageNumber("2107E")
      .importVoyageNumber("2106W")
      .carrierServiceCode("TNT1")
      .modeOfTransport(ModeOfTransport.VESSEL)
      .UNLocationCode("DEHAM")
      .facilityCode("DPWJA")
      .facilityCodeListProvider(FacilityCodeListProvider.SMDG)
      .vessel(VesselTO.builder().vesselIMONumber("1234567").build())
      .build();

    TransportCallWithTimestampsTO saved = given()
      .contentType("application/json")
      .body(newTransportCall)
      .post("/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(201)
      .contentType(ContentType.JSON)
      .extract()
      .body()
      .jsonPath().getObject(".", TransportCallWithTimestampsTO.class)
      ;

    List<TransportCallWithTimestampsTO> calls = given()
      .contentType("application/json")
      .get("/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(3))
      .extract()
      .body()
      .jsonPath().getList(".", TransportCallWithTimestampsTO.class)
      ;

    assertTrue(calls.stream().anyMatch(tc -> tc.getTransportCallID().equals(saved.getTransportCallID())));
  }
}
