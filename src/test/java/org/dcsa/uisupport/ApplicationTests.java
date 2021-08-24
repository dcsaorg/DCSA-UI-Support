package org.dcsa.uisupport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"db_hostname=0.0.0.0:5432/dcsa_openapi"})
class ApplicationTests {

  @Test
  void contextLoads() {}
}
