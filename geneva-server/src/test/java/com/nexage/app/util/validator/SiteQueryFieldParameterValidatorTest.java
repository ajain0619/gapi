package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.nexage.app.services.site.SiteQueryField;
import com.nexage.app.util.validator.site.SiteQueryFieldParameterConstraint;
import com.nexage.app.util.validator.site.SiteQueryFieldParameterValidator;
import com.nexage.app.util.validator.site.SiteQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;
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
class SiteQueryFieldParameterValidatorTest {

  @Mock private SiteQueryFieldParameterConstraint parameterConstraint;
  @Mock private ConstraintValidatorContext context;

  @InjectMocks
  private static SiteQueryFieldParameterValidator validator =
      new SiteQueryFieldParameterValidator();

  @BeforeEach
  public void setup() {
    initializeContext();
    ReflectionTestUtils.setField(validator, "annotation", parameterConstraint);
  }

  @Test
  void shouldReturnFalseWhenQueryFieldMapIsNull() {
    // when
    boolean isValid =
        validator.isValid(new SiteQueryParams(null, SearchQueryOperator.AND), context);

    // then
    assertFalse(isValid);
  }

  @Test
  void shouldReturnTrueWhenQueryFieldMapIsMissingOrEmpty() {
    // when
    boolean isValid =
        validator.isValid(prepareValidationMap(new String[0], new String[0][0]), context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnTrueWhenUnknownFieldValidation() {
    // given
    SiteQueryParams queryFieldMap = prepareValidationMap("fakeName", "some_val");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnTrueWhenNameFieldHasCorrectValues() {

    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(
            SiteQueryField.NAME.getName(), "some_name", "some_name2", "123", "x23SD_");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnTrueWhenGlobalAliasNameFieldValidationHasCorrectValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(
            SiteQueryField.GLOBAL_ALIAS_NAME.getName(),
            "some_globalAliasName",
            "some_globalAliasName2",
            "123",
            "x23SD_");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnTrueWhenCompanyNameFieldValidationHasCorrectValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(
            SiteQueryField.COMPANY_NAME.getName(),
            "some_companyName",
            "some_companyName2",
            "123",
            "x23SD_");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnTrueWhenPidFieldHasNumericValues() {
    // given
    SiteQueryParams queryFieldMap = prepareValidationMap(SiteQueryField.PID.getName(), "123", "32");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenPidFieldHasAlphanumericValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(SiteQueryField.PID.getName(), "123", "x23SD_");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertFalse(isValid);
  }

  @Test
  void shouldReturnTrueWhenStatusFieldHasOnlyNumericValues() {
    // given
    SiteQueryParams queryFieldMap = prepareValidationMap(SiteQueryField.STATUS.getName(), "1", "0");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenStatusFieldHasAlphanumericValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(SiteQueryField.STATUS.getName(), "1a", "0a");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertFalse(isValid);
  }

  @Test
  void shouldReturnTrueWhenCompanyPidFieldHasOnlyNumericValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(SiteQueryField.COMPANY_PID.getName(), "123", "32");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertTrue(isValid);
  }

  @Test
  void shouldReturnFalseWhenCompanyPidFieldHasAlphanumericValues() {
    // given
    SiteQueryParams queryFieldMap =
        prepareValidationMap(SiteQueryField.COMPANY_PID.getName(), "123", "x23SD_");

    // when
    boolean isValid = validator.isValid(queryFieldMap, context);

    // then
    assertFalse(isValid);
  }

  private SiteQueryParams prepareValidationMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new SiteQueryParams(map, SearchQueryOperator.AND);
  }

  private SiteQueryParams prepareValidationMap(String[] keys, String[][] values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    for (int i = 0; i < keys.length; i++) {
      map.addAll(keys[i], Arrays.asList(values[i]));
    }
    return new SiteQueryParams(map, SearchQueryOperator.AND);
  }

  private void initializeContext() {
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    lenient()
        .when(context.buildConstraintViolationWithTemplate(any()))
        .thenReturn(constraintViolationBuilder);
  }
}
