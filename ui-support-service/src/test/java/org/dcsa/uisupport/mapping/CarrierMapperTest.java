package org.dcsa.uisupport.mapping;

import org.dcsa.skernel.domain.persistence.entity.Carrier;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.dcsa.uisupport.transferobjects.CarrierTO;
import org.junit.jupiter.api.Test;

public class CarrierMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(Carrier.class, CarrierTO.class);
  }
}
