package com.nexage.admin.core.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.model.Position;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionPlacementCategoryValidatorTest {

  @Mock private PositionPlacementCategoryValueConstraint positionPlacementCategoryValueConstraint;
  @Mock private ConstraintValidatorContext context;
  @InjectMocks private PositionPlacementCategoryValueValidator validator;

  private static Stream<PlacementCategory> providePlacementCategoryValues() {
    return Stream.of(
        PlacementCategory.BANNER,
        PlacementCategory.INTERSTITIAL,
        PlacementCategory.MEDIUM_RECTANGLE,
        PlacementCategory.NATIVE_V2,
        PlacementCategory.INSTREAM_VIDEO,
        PlacementCategory.REWARDED_VIDEO,
        PlacementCategory.IN_ARTICLE,
        PlacementCategory.IN_FEED);
  }

  @BeforeEach
  void setUp() {
    initializeContext();
    initializeConstraint();
  }

  @Test
  void shouldFailWhenPlacementCategoryIsNative() {
    Position position = new Position();
    position.setPlacementCategory(PlacementCategory.NATIVE);
    assertFalse(validator.isValid(position, context));
  }

  @ParameterizedTest
  @MethodSource("providePlacementCategoryValues")
  void shouldPassWhenPlacementCategoryIsNotNative(PlacementCategory placementCategory) {
    Position position = new Position();
    position.setPlacementCategory(placementCategory);
    assertTrue(validator.isValid(position, context));
  }

  private void initializeContext() {
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }

  private void initializeConstraint() {
    lenient()
        .when(positionPlacementCategoryValueConstraint.field())
        .thenReturn("placementCategory");
  }
}
