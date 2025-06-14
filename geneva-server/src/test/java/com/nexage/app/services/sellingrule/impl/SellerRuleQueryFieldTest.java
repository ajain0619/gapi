package com.nexage.app.services.sellingrule.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SellerRuleQueryFieldTest {

  @Test
  void typeFieldHasAllowedOnlyValues() {
    assertTrue(SellerRuleQueryField.TYPE.hasAllowedValue("BRAND_PROTECTION"));

    assertFalse(SellerRuleQueryField.TYPE.hasAllowedValue("SOME_VALUE"));
  }

  @Test
  void nameFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SellerRuleQueryField.NAME.hasAllowedValuesDefined());
  }

  @Test
  void pidFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SellerRuleQueryField.PID.hasAllowedValuesDefined());
  }

  @Test
  void sellerDeployedFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SellerRuleQueryField.DF_SELLER.hasAllowedValuesDefined());
  }

  @Test
  void sitePidFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SellerRuleQueryField.DF_SITE.hasAllowedValuesDefined());
  }

  @Test
  void placementPidFieldDoesNotHaveAllowedOnlyValues() {
    assertFalse(SellerRuleQueryField.DF_PLACEMENT.hasAllowedValuesDefined());
  }

  @Test
  void pidFieldIsANumericField() {
    assertFalse(SellerRuleQueryField.PID.isValid("abc"));
    assertTrue(SellerRuleQueryField.PID.isValid("12"));
  }

  @Test
  void pidFieldIsOfTypeLong() {
    checkValueType(SellerRuleQueryField.PID, "1", "23");
  }

  @Test
  void nameFieldIsOfTypeString() {
    checkValueType(SellerRuleQueryField.NAME, "n_as", "adm");
  }

  @Test
  void typeFieldIsOfTypeString() {
    checkValueType(SellerRuleQueryField.TYPE, "T1");
  }

  @Test
  void sitePidFieldIsOfTypeLong() {
    checkValueType(SellerRuleQueryField.DF_SITE, "213", "3244234");
  }

  @Test
  void placementPidFieldIsOfTypeLong() {
    checkValueType(SellerRuleQueryField.DF_PLACEMENT, "213", "3244234");
  }

  @Test
  void sellerDeployedFieldIsOfTypeBoolean() {
    checkValueType(SellerRuleQueryField.DF_SELLER, "true", "faLSE");
  }

  @Test
  void deployForSitesFieldIsANumericField() {
    assertFalse(SellerRuleQueryField.DF_SITE.isValid("abc"));
    assertTrue(SellerRuleQueryField.DF_SITE.isValid("12"));
  }

  @Test
  void deployForPlacementsFieldIsANumericField() {
    assertFalse(SellerRuleQueryField.DF_PLACEMENT.isValid("abc"));
    assertTrue(SellerRuleQueryField.DF_PLACEMENT.isValid("12"));
  }

  @Test
  void deployForSellerFieldIsOfTypeBoolean() {
    assertFalse(SellerRuleQueryField.DF_SELLER.isValid("abc"));
    assertTrue(SellerRuleQueryField.DF_SELLER.isValid("true"));
    assertTrue(SellerRuleQueryField.DF_SELLER.isValid("FAlsE"));
  }

  @Test
  void fieldsThatDoesNotHaveAllowedOnlyValuesDefined() {
    assertFalse(SellerRuleQueryField.NAME.hasAllowedValuesDefined());
    assertFalse(SellerRuleQueryField.PID.hasAllowedValuesDefined());
    assertFalse(SellerRuleQueryField.DF_PLACEMENT.hasAllowedValuesDefined());
    assertFalse(SellerRuleQueryField.DF_SELLER.hasAllowedValuesDefined());
    assertFalse(SellerRuleQueryField.DF_SITE.hasAllowedValuesDefined());
  }

  @Test
  void fieldsThatHaveAllowedOnlyValuesDefined() {
    assertTrue(SellerRuleQueryField.TYPE.hasAllowedValuesDefined());
  }

  @Test
  void deployForSellerFieldHasTrueAsDefaultValue() {
    assertTrue((Boolean) SellerRuleQueryField.DF_SELLER.getDefaultValues()[0]);
  }

  @Test
  void defaultValuesShouldBeAppliedWhenFieldValuesContainsBlankData() {
    // i.e. SellerRuleQueryField.DF_SELLER is a field with default values
    // note that this case should not use empty list as a list of values for this field
    Object[] values =
        SellerRuleQueryField.DF_SELLER.values(
            prepareValueMap(SellerRuleQueryField.DF_SELLER.getName(), ""));
    assertTrue(Boolean.parseBoolean(values[0].toString()));

    // note that this case should use empty list as a list of values for this field
    values =
        SellerRuleQueryField.DF_SELLER.values(
            prepareValueMap(SellerRuleQueryField.DF_SELLER.getName()));
    assertTrue(Boolean.parseBoolean(values[0].toString()));
  }

  private void checkValueType(SellerRuleQueryField field, String... inputValues) {
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
