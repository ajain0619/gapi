package com.nexage.app.services.sellingrule.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.RuleType;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SellerRuleQueryFieldParameterTest {

  @Test
  void nameFieldValuesCreatedFromAnInputMustBeTheSameAsInputData() {
    String testValue = "val1";
    MultiValueQueryParams inputParams =
        createInputMap(SellerRuleQueryField.NAME.getName(), testValue, "tst1_22");
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getRuleName().isPresent());
    parameter.getRuleName().ifPresent(v -> assertThat(v, is(testValue)));
  }

  @Test
  void singleValueFieldShouldReturnTheFirstValueIfMultiValuesAreGiven() {
    // name is one of single value field
    String[] testValues = {"val1", "tst1_22"};
    MultiValueQueryParams inputParams =
        createInputMap(SellerRuleQueryField.NAME.getName(), testValues);
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getRuleName().isPresent());
    parameter.getRuleName().ifPresent(v -> assertEquals(v, testValues[0]));
  }

  @Test
  void singleValueFieldShouldBeEmptyIfMissingInInputData() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.NAME.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertFalse(parameter.getRuleName().isPresent());
  }

  @Test
  void pidFieldValuesCreatedFromAnInputMustBeTheSameAsInputData() {
    MultiValueQueryParams inputParams =
        createInputMap(SellerRuleQueryField.PID.getName(), "12", "3300");
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getRulePids().isPresent());
    parameter.getRulePids().ifPresent(values -> assertThat(values, hasItems(12L, 3300L)));
  }

  @Test
  void multiValueFieldShouldBeEmptyIfMissingInInputData() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.PID.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertFalse(parameter.getRulePids().isPresent());
  }

  @Test
  void deployedForSellerFieldShouldBeTrueIfSpecifiedWithoutValue() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.DF_SELLER.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getOnlyRulesDeployedForSeller().isPresent());
    parameter.getOnlyRulesDeployedForSeller().ifPresent(Assertions::assertTrue);
  }

  @Test
  void fieldsShouldBeEmptyIfNotSpecifiedInQueryField() {
    MultiValueQueryParams inputParams = createInputMap("some_random_key");
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getTypes().isEmpty());
    assertTrue(parameter.getRuleName().isEmpty());
    assertTrue(parameter.getPlacementPids().isEmpty());
    assertTrue(parameter.getRulePids().isEmpty());
    assertTrue(parameter.getOnlyRulesDeployedForSeller().isEmpty());
    assertTrue(parameter.getSitePids().isEmpty());
  }

  private MultiValueQueryParams createInputMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new MultiValueQueryParams(map, SearchQueryOperator.AND);
  }

  @Test
  void typeFieldShouldBeEmptyIfMissingInInputData() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.TYPE.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertFalse(parameter.getTypes().isPresent());
  }

  @Test
  void typeFieldShouldBeProperlyConvertedFromStringInInputData() {
    MultiValueQueryParams inputParams =
        createInputMap(SellerRuleQueryField.TYPE.getName(), "BRAND_PROTECTION");
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    assertTrue(parameter.getTypes().isPresent());
    parameter.getTypes().ifPresent(t -> assertThat(t, hasItem(RuleType.BRAND_PROTECTION)));
  }

  @Test
  void whenTypeIsMissingInParameterMapForciblySetValuesShouldBeApplied() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.TYPE.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());
    parameter.whenAbsentForceFieldValues(
        SellerRuleQueryField.TYPE, RuleType.BRAND_PROTECTION.name());

    assertTrue(parameter.getTypes().isPresent());
    parameter.getTypes().ifPresent(t -> assertThat(t, hasItem(RuleType.BRAND_PROTECTION)));
  }

  @Test
  void whenRulePidsIsMissingInParameterMapForciblySetValuesShouldBeApplied() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.PID.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());
    parameter.whenAbsentForceFieldValues(SellerRuleQueryField.PID, "21");

    assertTrue(parameter.getRulePids().isPresent());
    parameter.getRulePids().ifPresent(t -> assertThat(t, hasItem(21L)));
  }

  @Test
  void whenRuleNameIsMissingInParameterMapForciblySetValuesShouldBeApplied() {
    MultiValueQueryParams inputParams = createInputMap(SellerRuleQueryField.NAME.getName());
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());
    parameter.whenAbsentForceFieldValues(SellerRuleQueryField.NAME, "some_name");

    assertTrue(parameter.getRuleName().isPresent());
    parameter.getRuleName().ifPresent(t -> assertThat(t, is("some_name")));
  }
}
