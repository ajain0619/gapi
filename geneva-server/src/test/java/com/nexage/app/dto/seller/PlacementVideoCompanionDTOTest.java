package com.nexage.app.dto.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.util.validator.ValidationMessages;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlacementVideoCompanionDTOTest {
  private Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @Test
  void testPidValidations() {
    PlacementVideoCompanionDTO companionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();

    // Create
    // Null
    companionDTO.setPid(null);
    Set<ConstraintViolation<PlacementVideoCompanionDTO>> constraintViolations =
        validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    companionDTO.setPid(1L);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_NOT_EMPTY, constraintViolations.iterator().next().getMessage());

    // Update
    companionDTO.setVersion(1);
    // Null
    companionDTO.setPid(null);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    companionDTO.setPid(1L);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testVersionValidations() {
    PlacementVideoCompanionDTO companionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();

    // Create
    // Null
    companionDTO.setVersion(null);
    Set<ConstraintViolation<PlacementVideoCompanionDTO>> constraintViolations =
        validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    companionDTO.setVersion(1);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_NOT_EMPTY, constraintViolations.iterator().next().getMessage());

    // Update
    companionDTO.setPid(1L);
    // Null
    companionDTO.setVersion(null);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Not Null
    companionDTO.setVersion(1);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());

    // < 0
    companionDTO.setVersion(-1);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());
  }

  @Test
  void testHeightValidations() {
    PlacementVideoCompanionDTO companionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();

    // Create
    // NULL
    companionDTO.setHeight(null);
    Set<ConstraintViolation<PlacementVideoCompanionDTO>> constraintViolations =
        validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());

    // < 1
    companionDTO.setHeight(0);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // > 9999
    companionDTO.setHeight(10000);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MAX, constraintViolations.iterator().next().getMessage());

    // Valid value
    companionDTO.setHeight(1000);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    companionDTO.setPid(1L);
    companionDTO.setVersion(1);
    companionDTO.setHeight(null);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());

    // Valid
    companionDTO.setHeight(1000);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  void testWidthValidations() {
    PlacementVideoCompanionDTO companionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();

    // Create
    // NULL
    companionDTO.setWidth(null);
    Set<ConstraintViolation<PlacementVideoCompanionDTO>> constraintViolations =
        validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());

    // < 1
    companionDTO.setWidth(0);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MIN, constraintViolations.iterator().next().getMessage());

    // > 9999
    companionDTO.setWidth(10000);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_NUMBER_MAX, constraintViolations.iterator().next().getMessage());

    // Valid value
    companionDTO.setWidth(1000);
    constraintViolations = validator.validate(companionDTO, Default.class, CreateGroup.class);
    assertEquals(0, constraintViolations.size());

    // Update
    companionDTO.setPid(1L);
    companionDTO.setVersion(1);
    companionDTO.setWidth(null);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(1, constraintViolations.size());
    assertEquals(
        ValidationMessages.WRONG_IS_EMPTY, constraintViolations.iterator().next().getMessage());

    // Valid
    companionDTO.setWidth(1000);
    constraintViolations = validator.validate(companionDTO, Default.class, UpdateGroup.class);
    assertEquals(0, constraintViolations.size());
  }
}
