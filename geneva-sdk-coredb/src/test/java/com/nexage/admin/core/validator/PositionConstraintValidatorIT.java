package com.nexage.admin.core.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.model.Position;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class PositionConstraintValidatorIT extends CoreDbSdkIntegrationTestBase {

  @Autowired LocalValidatorFactoryBean validator;

  private List<String> expectedViolations =
      Arrays.asList(
          "Placement Inventory Attribute(s) are invalid",
          "Must be any of [BANNER, INTERSTITIAL, MEDIUM_RECTANGLE, NATIVE_V2, INSTREAM_VIDEO, REWARDED_VIDEO, IN_ARTICLE, IN_FEED]");

  @Test
  void shouldFailOnPositionConstraints() {
    Set<ConstraintViolation<Position>> violations = validator.validate(new Position());
    assertEquals(2, violations.size(), "Just 5 constraint being violated.");
  }

  @Test
  void shouldFailOnPositionConstraintsOnCreateGroup() {
    Position position = new Position();
    position.setPlacementCategory(PlacementCategory.NATIVE);
    Set<ConstraintViolation<Position>> violations = validator.validate(position, CreateGroup.class);
    assertEquals(1, violations.size(), "Just 1 constraint being violated.");

    assertTrue(
        expectedViolations.contains(
            ((ConstraintViolationImpl) violations.toArray()[0]).getMessage()));
  }

  @Test
  void shouldFailOnPositionConstraintsOnUpdateGroup() {
    Position position = new Position();
    position.setPlacementCategory(PlacementCategory.NATIVE);
    Set<ConstraintViolation<Position>> violations = validator.validate(position, UpdateGroup.class);
    assertEquals(1, violations.size(), "Just 1 constraint being violated.");
    assertTrue(
        expectedViolations.contains(
            ((ConstraintViolationImpl) violations.toArray()[0]).getMessage()));
  }
}
