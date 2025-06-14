package com.nexage.app.queue.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class EnrichPlacementResultMessageTest {

  @Test
  void testIsValid() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    EnrichPlacementResultMessage msg = new EnrichPlacementResultMessage();
    Set<ConstraintViolation<EnrichPlacementResultMessage>> violations = validator.validate(msg);
    assertFalse(violations.isEmpty());

    msg = validMsg();
    violations = validator.validate(msg);
    assertTrue(violations.isEmpty());

    msg.setCompanyPid("1f");
    violations = validator.validate(msg);
    assertFalse(violations.isEmpty());

    msg = validMsg();
    msg.setPlacementPid("3k");
    violations = validator.validate(msg);
    assertFalse(violations.isEmpty());

    msg = validMsg();
    msg.setSectionPid("");
    violations = validator.validate(msg);
    assertFalse(violations.isEmpty());
  }

  private EnrichPlacementResultMessage validMsg() {
    EnrichPlacementResultMessage msg = new EnrichPlacementResultMessage();
    msg.setPlacementPid("10");
    msg.setCompanyPid("11");
    msg.setSitePid("12");
    msg.setSectionPid("sectionPid");
    return msg;
  }
}
