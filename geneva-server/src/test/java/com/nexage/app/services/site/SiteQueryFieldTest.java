package com.nexage.app.services.site;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SiteQueryFieldTest {

  @Test
  void shouldReturnTrueWhenPidFieldIsANumericField() {
    assertFalse(SiteQueryField.PID.isValid("abc"));
    assertTrue(SiteQueryField.PID.isValid("12"));
  }

  @Test
  void shouldReturnTrueWhenCompanyPidFieldIsANumericField() {
    assertFalse(SiteQueryField.COMPANY_PID.isValid("abc"));
    assertTrue(SiteQueryField.COMPANY_PID.isValid("12"));
  }

  @Test
  void shouldReturnTrueWhenPidFieldIsOfTypeLong() {
    checkValueType(SiteQueryField.PID, "1", "23");
  }

  @Test
  void shouldReturnTrueWhenCompanyPidFieldIsOfTypeLong() {
    checkValueType(SiteQueryField.COMPANY_PID, "1", "23");
  }

  @Test
  void shouldReturnTrueWhenNameFieldIsOfTypeString() {
    checkValueType(SiteQueryField.NAME, "n_as", "adm");
  }

  @Test
  void shouldReturnTrueWhenGlobalAliasNameFieldIsOfTypeString() {
    checkValueType(SiteQueryField.GLOBAL_ALIAS_NAME, "n_as", "adm");
  }

  @Test
  void shouldReturnTrueWhenCompanyNameFieldIsOfTypeString() {
    checkValueType(SiteQueryField.COMPANY_NAME, "n_as", "adm");
  }

  @Test
  void shouldReturnTrueWhenStatusFieldIsOfTypeString() {
    checkValueType(SiteQueryField.STATUS, "0", "1");
  }

  @Test
  void shouldReturnTrueWhenStatusFieldIsANumericField() {
    assertFalse(SiteQueryField.STATUS.isValid("1a"));
    assertTrue(SiteQueryField.STATUS.isValid("1"));
  }

  @Test
  void statusFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SiteQueryField.STATUS.hasAllowedValuesDefined());
  }

  @Test
  void shouldReturnFalseWhenFieldsAllowedValuesDefined() {
    assertFalse(SiteQueryField.NAME.hasAllowedValuesDefined());
    assertFalse(SiteQueryField.PID.hasAllowedValuesDefined());
    assertFalse(SiteQueryField.GLOBAL_ALIAS_NAME.hasAllowedValuesDefined());
    assertFalse(SiteQueryField.COMPANY_NAME.hasAllowedValuesDefined());
    assertFalse(SiteQueryField.COMPANY_PID.hasAllowedValuesDefined());
    assertFalse(SiteQueryField.STATUS.hasAllowedValuesDefined());
  }

  private void checkValueType(SiteQueryField field, String... inputValues) {
    Object[] values = field.values(prepareValueMap(field.getName(), inputValues));
    boolean allValuesAreOk =
        Stream.of(values).anyMatch(v -> Objects.equals(v.getClass(), field.typeClass()));
    String msg = "One of '%s' field values is of wrong type - '%s'";
    assertTrue(
        allValuesAreOk, String.format(msg, field.getName(), field.typeClass().getCanonicalName()));
  }

  private MultiValueMap<String, String> prepareValueMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return map;
  }
}
