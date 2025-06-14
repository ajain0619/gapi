package com.nexage.app.dto.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.PlacementCategory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PlacementDTOTest {

  private Validator validator;

  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  public static Object[][] data() {
    return new Object[][] {
      {"Test/(.)", true},
      {"Taru 1AS test nar - test", true},
      {"Test[", true},
      {"Test]", true},
      {"Test&", true},
      {"Test<", false},
      {"Test>", false},
      {"Test,", true},
      {"<script>alert('name');</script>", false},
      {"Test$#%^!@", false},
    };
  }

  @ParameterizedTest
  @MethodSource("data")
  void testPlacementMemoValidation(String memo, boolean positiveValidation) {
    PlacementDTO placementDTO = new PlacementDTO();
    placementDTO.setPlacementCategory(PlacementCategory.BANNER);
    placementDTO.setMemo(memo);
    validateResult(validator.validate(placementDTO), memo, positiveValidation);
  }

  private void validateResult(
      Set<ConstraintViolation<PlacementDTO>> validate, String memo, boolean positiveValidation) {
    if (positiveValidation) {
      assertTrue(validate.isEmpty());
    } else {
      assertFalse(validate.isEmpty());
      ConstraintViolation<PlacementDTO> constraint = validate.iterator().next();
      assertEquals("memo", constraint.getPropertyPath().toString(), "Unexpected result");
      assertEquals(memo, constraint.getInvalidValue().toString(), "Unexpected result");
      assertEquals(
          "must match \"^[\\w\\s\\[\\]\\-&,(./)]+$\"",
          constraint.getMessage(),
          "Unexpected result");
    }
  }
}
