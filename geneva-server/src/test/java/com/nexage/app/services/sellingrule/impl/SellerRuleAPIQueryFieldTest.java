package com.nexage.app.services.sellingrule.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Most of the functionality of this class is the same as {@link SellerRuleQueryField} and has been
 * already tested in {@link SellerRuleQueryFieldTest} class. This class has only tests for features
 * specific to API endpoints.
 */
class SellerRuleAPIQueryFieldTest {

  @Test
  void typeFieldHasDefaultValue() {
    assertThat(SellerRuleAPIQueryField.TYPE.getDefaultValues().length, is(1));
  }

  @Test
  void typeFieldHasAllowedValue() {
    assertTrue(SellerRuleAPIQueryField.TYPE.hasAllowedValue("BRAND_PROTECTION"));
    assertTrue(SellerRuleAPIQueryField.TYPE.hasAllowedValuesDefined());
    assertThat(SellerRuleAPIQueryField.TYPE.getAllowedValues().length, is(1));
  }

  @Test
  void typeFieldIsOfTypeString() {
    checkValueType(SellerRuleAPIQueryField.TYPE, "TYPE1", "TYPE_2");
  }

  @Test
  void pidFieldIsOfTypeLong() {
    checkValueType(SellerRuleAPIQueryField.PID, "1", "22");
  }

  private void checkValueType(SellerRuleAPIQueryField field, String... inputValues) {
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
