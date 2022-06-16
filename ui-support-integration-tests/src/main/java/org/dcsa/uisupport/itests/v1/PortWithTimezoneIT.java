package org.dcsa.uisupport.itests.v1;

import io.restassured.http.ContentType;
import org.dcsa.uisupport.itests.config.RestAssuredConfigurator;
import org.dcsa.uisupport.transferobjects.PortWithTimezoneTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PortWithTimezoneIT {
  @BeforeAll
  public static void initializeRestAssured() {
    RestAssuredConfigurator.initialize();
  }

  @Test
  public void testGetPorts() {
    List<PortWithTimezoneTO> ports = given()
      .contentType("application/json")
      .get("/v1/unofficial/ports")
      .then()
      .assertThat()
      .statusCode(200)
      .contentType(ContentType.JSON)
      .body("size()", greaterThanOrEqualTo(20))
      .extract()
      .body()
      .jsonPath().getList(".", PortWithTimezoneTO.class)
      ;

    assertTrue(ports.stream().noneMatch(this::codeOrNameIsNull));

    // Just test a single port
    PortWithTimezoneTO dublin = ports.stream().filter(port -> port.UNLocationCode().equals("IEORK")).findFirst().get();
    assertEquals("Cork", dublin.UNLocationName());
    assertEquals("Europe/Dublin", dublin.ianaTimezone());
  }

  private Boolean codeOrNameIsNull(PortWithTimezoneTO port) {
    return port.UNLocationCode() == null || port.UNLocationName() == null;
  }
}
