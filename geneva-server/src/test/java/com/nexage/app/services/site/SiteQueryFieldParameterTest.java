package com.nexage.app.services.site;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SiteQueryFieldParameterTest {

  @Test
  void shouldCreateNameFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    String testValue = "val1";
    MultiValueQueryParams inputParams =
        createInputMap(SiteQueryField.NAME.getName(), testValue, "tst1_22");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getName().isPresent());
    parameter.getName().ifPresent(v -> assertThat(v, is(testValue)));
  }

  @Test
  void shouldCreateGlobalAliasNameFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    String testValue = "val1";
    MultiValueQueryParams inputParams =
        createInputMap(SiteQueryField.GLOBAL_ALIAS_NAME.getName(), testValue, "tst1_22");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getGlobalAliasName().isPresent());
    parameter.getGlobalAliasName().ifPresent(v -> assertThat(v, is(testValue)));
  }

  @Test
  void shouldCreateCompanyNameFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    String testValue = "val1";
    MultiValueQueryParams inputParams =
        createInputMap(SiteQueryField.COMPANY_NAME.getName(), testValue, "tst1_22");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getCompanyName().isPresent());
    parameter.getCompanyName().ifPresent(v -> assertThat(v, is(testValue)));
  }

  @Test
  void shouldReturnTheFirstValueIfMultiValuesAreGiven() {
    // given
    String[] testValues = {"val1", "tst1_22"};
    MultiValueQueryParams inputParams = createInputMap(SiteQueryField.NAME.getName(), testValues);

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getName().isPresent());
    parameter.getName().ifPresent(v -> assertEquals(v, testValues[0]));
  }

  @Test
  void shouldBeEmptyIfMissingInInputData() {
    // given
    MultiValueQueryParams inputParams = createInputMap(SiteQueryField.NAME.getName());

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertFalse(parameter.getName().isPresent());
  }

  @Test
  void shouldCreatePidFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    MultiValueQueryParams inputParams = createInputMap(SiteQueryField.PID.getName(), "12", "3300");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getPids().isPresent());
    parameter.getPids().ifPresent(values -> assertThat(values, hasItems(12L, 3300L)));
  }

  @Test
  void shouldCreateCompanyPidFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    MultiValueQueryParams inputParams =
        createInputMap(SiteQueryField.COMPANY_PID.getName(), "12", "3300");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getCompanyPids().isPresent());
    parameter.getCompanyPids().ifPresent(values -> assertThat(values, hasItems(12L, 3300L)));
  }

  @Test
  void shouldCreateStatusFieldValuesFromAnInputMustBeTheSameAsInputData() {
    // given
    MultiValueQueryParams inputParams = createInputMap(SiteQueryField.STATUS.getName(), "0", "1");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getStatus().isPresent());
    parameter.getStatus().ifPresent(values -> assertThat(values, hasItems(0, 1)));
  }

  @Test
  void shouldBeEmptyIfMissingInputDataForMultiValueField() {
    // given
    MultiValueQueryParams inputParams = createInputMap(SiteQueryField.PID.getName());

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertFalse(parameter.getPids().isPresent());
  }

  @Test
  void shouldBeEmptyIfNotSpecifiedInQueryField() {
    // given
    MultiValueQueryParams inputParams = createInputMap("some_random_key");

    // when
    SiteQueryFieldParameter parameter =
        SiteQueryFieldParameter.createFrom(inputParams, SiteQueryField.values());

    // then
    assertTrue(parameter.getName().isEmpty());
    assertTrue(parameter.getPids().isEmpty());
    assertTrue(parameter.getCompanyPids().isEmpty());
    assertTrue(parameter.getGlobalAliasName().isEmpty());
  }

  private MultiValueQueryParams createInputMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new MultiValueQueryParams(map, SearchQueryOperator.AND);
  }
}
