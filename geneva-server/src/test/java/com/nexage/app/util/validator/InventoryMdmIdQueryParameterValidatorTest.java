package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.nexage.app.util.InventoryMdmIdQueryFieldParameter;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class InventoryMdmIdQueryParameterValidatorTest {

  @Mock private InventoryMdmIdQueryFieldParameterConstraint parameterConstraint;
  @Mock private ConstraintValidatorContext context;

  @InjectMocks private InventoryMdmIdQueryParameterValidator validator;

  @BeforeEach
  void setup() {
    initializeContext();
    ReflectionTestUtils.setField(validator, "annotation", parameterConstraint);
  }

  private void initializeContext() {
    ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }

  @Test
  void shouldReturnInvalidForNullQueryFields() {
    boolean isValid =
        validator.isValid(
            new InventoryMdmIdQueryFieldParams(null, SearchQueryOperator.OR), context);
    assertFalse(isValid);
  }

  @Test
  void shouldReturnInvalidForEmptyQuery() {
    boolean isValid =
        validator.isValid(prepareValidationMap(new String[0], new String[0][0]), context);
    assertFalse(isValid);
  }

  @Test
  void shouldReturnInvalidForUnknownField() {
    InventoryMdmIdQueryFieldParams queryFieldMap = prepareValidationMap("someName", "some_val");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertFalse(isValid);
  }

  @Test
  void shouldReturnValidIfFieldsCorrectlySpecified() {
    InventoryMdmIdQueryFieldParams queryFieldMap =
        prepareValidationMap(
            new String[] {
              InventoryMdmIdQueryFieldParameter.SELLER_PID.getName(),
              InventoryMdmIdQueryFieldParameter.DEAL_PID.getName()
            },
            new String[][] {{"742"}, {"17863"}});
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  private InventoryMdmIdQueryFieldParams prepareValidationMap(String[] keys, String[][] values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    for (int i = 0; i < keys.length; i++) {
      map.addAll(keys[i], Arrays.asList(values[i]));
    }
    return new InventoryMdmIdQueryFieldParams(map, SearchQueryOperator.AND);
  }

  private InventoryMdmIdQueryFieldParams prepareValidationMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new InventoryMdmIdQueryFieldParams(map, SearchQueryOperator.AND);
  }
}
