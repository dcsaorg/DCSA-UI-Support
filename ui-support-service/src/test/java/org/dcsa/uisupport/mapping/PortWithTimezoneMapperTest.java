package org.dcsa.uisupport.mapping;

import org.dcsa.skernel.test.helpers.FieldValidator;
import org.dcsa.uisupport.persistence.entity.PortWithTimezone;
import org.dcsa.uisupport.transferobjects.PortWithTimezoneTO;
import org.junit.jupiter.api.Test;

public class PortWithTimezoneMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(PortWithTimezone.class, PortWithTimezoneTO.class);
  }
}
