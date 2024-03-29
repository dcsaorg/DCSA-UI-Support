package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.TimestampInfoTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TimestampInfoIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetTimestampDefinitions() {

    given()
        .contentType("application/json")
        .get("/ui-support/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("operationsEventTO", notNullValue())
        .body("operationsEventTO.publisherPattern", notNullValue())
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
  public void testGetTimestampWithQueryParamNegotiationCycle() {
    given()
        .contentType("application/json")
        .queryParam("negotiationCycle", "TA-Berth")
        .get("/ui-support/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("timestampDefinitionTO.negotiationCycle.cycleKey", everyItem(equalTo("TA-Berth")))
        .extract()
        .body()
        .jsonPath()
        .getList(".", TimestampInfoTO.class);

    given()
        .contentType("application/json")
        .queryParam("negotiationCycle", "T-Pilotage (Inbound)")
        .get("/ui-support/v1/unofficial/timestamp-info")
        .then()
        .assertThat()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("size()", greaterThanOrEqualTo(1))
        .body("timestampDefinitionTO.negotiationCycle.cycleKey", everyItem(equalTo("T-Pilotage (Inbound)")))
        .extract()
        .body()
        .jsonPath()
        .getList(".", TimestampInfoTO.class);
  }
}
