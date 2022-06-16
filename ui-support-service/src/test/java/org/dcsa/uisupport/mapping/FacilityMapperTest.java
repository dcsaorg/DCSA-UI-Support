package org.dcsa.uisupport.mapping;

import org.dcsa.skernel.domain.persistence.entity.Facility;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.dcsa.uisupport.transferobjects.TerminalTO;
import org.junit.jupiter.api.Test;

public class FacilityMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(Facility.class, TerminalTO.class,
      "id", "location");
  }
}
