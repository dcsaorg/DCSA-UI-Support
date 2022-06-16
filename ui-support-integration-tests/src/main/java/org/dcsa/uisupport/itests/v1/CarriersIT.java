package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CarriersIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetCarriers() {
    List<CarrierTO> carriers = given()
      .contentType("application/json")
      .get("/v1/unofficial/carriers")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(9))
      .extract()
      .body()
      .jsonPath().getList(".", CarrierTO.class)
    ;

    assertTrue(carriers.stream().noneMatch(carrier -> carrier.id() == null));

    // Just test a single carrier
    CarrierTO everGreen = carriers.stream().filter(carrier -> carrier.smdgCode().equals("EMC")).findFirst().get();
    assertEquals("Evergreen Marine Corporation", everGreen.carrierName());
  }
}
