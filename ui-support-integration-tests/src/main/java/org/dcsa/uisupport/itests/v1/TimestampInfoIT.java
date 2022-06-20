package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TimestampInfoIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetTimestampDefinitions() {
    List<TimestampInfoTO> timestampDefinitions =
        given()
            .contentType("application/json")
            .get("/v1/unofficial/timestamp-info")
            .then()
            .assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", greaterThanOrEqualTo(1))
            .body("operationsEventTO", notNullValue())
            .body("operationsEventTO.publisher", notNullValue())
            .body("operationsEventTO.transportCall", notNullValue())
            .body("operationsEventTO.eventLocation", notNullValue())
            .body("timestampDefinitionTO", notNullValue())
            .body("eventDeliveryStatus", notNullValue())
            .extract()
            .body()
            .jsonPath()
            .getList(".", TimestampInfoTO.class);
  }

  @Test
  public void testGetTimestampWithQueryParamTransportCallID() {
    given()
        .contentType("application/json")
        .queryParam("transportCallID", "b785317a-2340-4db7-8fb3-c8dfb1edfa60")
        .get("/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body(
            "operationsEventTO.transportCall.transportCallReference",
            everyItem(equalTo("TC-REF-08_03-B")))
        .extract()
        .body()
        .jsonPath()
        .getList(".", TimestampInfoTO.class);
  }

  @Test
  public void testGetTimestampWithQueryParamNegotiationCycle() {
    given()
        .contentType("application/json")
        .queryParam("negotiationCycle", "TA-Berth")
        .get("/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("timestampDefinitionTO.negotiationCycle", everyItem(equalTo("TA-Berth")))
        .extract()
        .body()
        .jsonPath()
        .getList(".", TimestampInfoTO.class);

    given()
        .contentType("application/json")
        .queryParam("negotiationCycle", "T-Pilotage-Arrival")
        .get("/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("timestampDefinitionTO.negotiationCycle", everyItem(equalTo("T-Pilotage-Arrival")))
        .extract()
        .body()
        .jsonPath()
        .getList(".", TimestampInfoTO.class);
  }
}
