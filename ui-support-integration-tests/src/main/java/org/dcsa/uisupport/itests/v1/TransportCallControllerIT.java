package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.dcsa.jit.transferobjects.TransportCallTO;
import org.dcsa.jit.transferobjects.TransportCallVesselTO;
import org.dcsa.jit.transferobjects.enums.FacilityCodeListProvider;
import org.dcsa.jit.transferobjects.enums.ModeOfTransport;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.TransportCallWithTimestampsTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransportCallControllerIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();

  }

  static boolean once = false;

  @BeforeEach
  public void setup() {
    synchronized (TransportCallControllerIT.class) {
      if (!once) {
        createTransportCall("1234567", "DEHAM", "CTA");
        createTransportCall("9811000", "USMIA", "SML");
        once = true;
      }
    }
  }

  @Test
  public void testGetTransportCalls() {
    given()
      .contentType("application/json")
      .get("/ui-support/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(HttpStatus.SC_OK)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(2))
      .extract()
      .body()
      .jsonPath()
      .getList(".", TransportCallWithTimestampsTO.class);
  }
  @Test
  public void testGetTransportCallsWithVesselIMONumberQueryParameter() {
    given()
      .contentType(ContentType.JSON)
      .queryParam("vesselIMONumber", "1234567")
      .get("/ui-support/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(HttpStatus.SC_OK)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(1))
      .body("transportCall.vessel.vesselIMONumber", everyItem(equalTo("1234567")))
      .extract()
      .body()
      .jsonPath()
      .getList(".", TransportCallWithTimestampsTO.class);
  }

  @Test
  public void testGetTransportCallsWithUNLocationCodeQueryParameter() {
    given()
      .contentType(ContentType.JSON)
      .queryParam("UNLocationCode", "USMIA")
      .get("/ui-support/v1/unofficial/transport-calls")
      .then()
      .assertThat()
      .statusCode(HttpStatus.SC_OK)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(1))
      .body("transportCall.UNLocationCode", everyItem(equalTo("USMIA")))
      .extract()
      .body()
      .jsonPath()
      .getList(".", TransportCallWithTimestampsTO.class);
  }

  @Test
  public void testPostTransportCalls() {
    TransportCallWithTimestampsTO saved = createTransportCall("1234567", "AEJEA", "DPWJA");
    assertNotNull(saved.getTransportCallID());
  }

  private TransportCallWithTimestampsTO createTransportCall(String vesselIMONumber, String unLocationCode, String facilityCode) {
    TransportCallTO newTransportCall =
      TransportCallTO.builder()
        .exportVoyageNumber("2107E")
        .importVoyageNumber("2106W")
        .carrierServiceCode("TNT1")
        .modeOfTransport(ModeOfTransport.VESSEL)
        .UNLocationCode(unLocationCode)
        .facilityCode(facilityCode)
        .facilityCodeListProvider(facilityCode != null ? FacilityCodeListProvider.SMDG : null)
        .vessel(TransportCallVesselTO.builder().vesselIMONumber(vesselIMONumber).build())
        .build();

    return given()
        .contentType("application/json")
        .body(newTransportCall)
        .post("/ui-support/v1/unofficial/transport-calls")
        .then()
        .assertThat()
        .statusCode(201)
        .contentType(ContentType.JSON)
        .extract()
        .body()
        .jsonPath()
        .getObject(".", TransportCallWithTimestampsTO.class);
  }
}
