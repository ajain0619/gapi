package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.nexage.app.services.sellingrule.impl.SellerRuleQueryField;
import com.nexage.app.util.validator.rule.queryfield.SellerRuleQueryFieldParameterConstraint;
import com.nexage.app.util.validator.rule.queryfield.SellerRuleQueryFieldParameterValidator;
import com.nexage.app.util.validator.rule.queryfield.SellerRuleQueryParams;
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
class SellerRuleQueryFieldParameterValidatorTest {

  @Mock private SellerRuleQueryFieldParameterConstraint parameterConstraint;
  @Mock private ConstraintValidatorContext context;

  @InjectMocks
  private static SellerRuleQueryFieldParameterValidator validator =
      new SellerRuleQueryFieldParameterValidator();

  @BeforeEach
  public void setup() {
    initializeContext();
    ReflectionTestUtils.setField(validator, "annotation", parameterConstraint);
  }

  @Test
  void nullQueryFieldMapIsInvalid() {
    boolean isValid =
        validator.isValid(new SellerRuleQueryParams(null, SearchQueryOperator.AND), context);
    assertFalse(isValid);
  }

  @Test
  void missingOrEmptyQueryFieldMapIsValid() {
    boolean isValid =
        validator.isValid(prepareValidationMap(new String[0], new String[0][0]), context);
    assertTrue(isValid);
  }

  @Test
  void unknownFieldValidationShouldFail() {
    SellerRuleQueryParams queryFieldMap = prepareValidationMap("fakeName", "some_val");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void nameFieldValidationShouldAcceptCorrectValues() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.NAME.getName(), "some_name", "some_name2");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void nameFieldDoesNotHaveAllowedOnlyValuesDefinedSoAnyValueIsAnAllowedOne() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.NAME.getName(), "123", "x23SD_");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void pidFieldShouldAcceptOnlyNumericValues() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.PID.getName(), "123", "32");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void pidFieldShouldNotAcceptAlphanumericValues() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.PID.getName(), "123", "x23SD_");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertFalse(isValid);
  }

  @Test
  void typeFieldShouldNotAcceptNotAllowedOnlyValues() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.TYPE.getName(), "someValue");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertFalse(isValid);
  }

  @Test
  void typeFieldShouldAcceptAllowedValues() {
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.TYPE.getName(), "BRAND_PROTECTION");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void multiFieldValidationShouldFailWhenOneOfTheFieldIsInvalid() {
    String[] keys = {
      SellerRuleQueryField.DF_SITE.getName(), SellerRuleQueryField.DF_SELLER.getName()
    };
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(keys, new String[][] {{"11"}, {"1", "3"}});
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertFalse(isValid);
  }

  @Test
  void multiFieldValidationShouldPassWhenAllFieldsAreValid() {
    String[] keys = {
      SellerRuleQueryField.DF_SITE.getName(), SellerRuleQueryField.DF_PLACEMENT.getName()
    };
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(keys, new String[][] {{"11"}, {"3"}});
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  @Test
  void defaultFieldValuesShouldBeAppliedBeforeFieldValidation() {
    // i.e. DF_SELLER (deployedForSeller) is a field with default values specified/defined
    SellerRuleQueryParams queryFieldMap =
        prepareValidationMap(SellerRuleQueryField.DF_SELLER.getName(), "");
    boolean isValid = validator.isValid(queryFieldMap, context);
    assertTrue(isValid);
  }

  private SellerRuleQueryParams prepareValidationMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new SellerRuleQueryParams(map, SearchQueryOperator.AND);
  }

  private SellerRuleQueryParams prepareValidationMap(String[] keys, String[][] values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    for (int i = 0; i < keys.length; i++) {
      map.addAll(keys[i], Arrays.asList(values[i]));
    }
    return new SellerRuleQueryParams(map, SearchQueryOperator.AND);
  }

  private void initializeContext() {
    ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }
}
