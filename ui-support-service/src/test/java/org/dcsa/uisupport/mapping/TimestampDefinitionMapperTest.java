package org.dcsa.uisupport.mapping;

import org.dcsa.jit.persistence.entity.TimestampDefinition;
import org.dcsa.skernel.test.helpers.FieldValidator;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.junit.jupiter.api.Test;

public class TimestampDefinitionMapperTest {
  @Test
  public void testFieldsAreEqual() {
    FieldValidator.assertFieldsAreEqual(TimestampDefinition.class, TimestampDefinitionTO.class);
  }
}
