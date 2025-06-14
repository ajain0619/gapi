package com.nexage.app.util.validator;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Site;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SearchRequestParamValidatorTest {

  @ParameterizedTest
  @ValueSource(strings = {"name", "status", "rtbEnabled"})
  void shouldReturnTrueForValidParams(String params) {
    Class<?> typeClass = Company.class;
    boolean result = SearchRequestParamValidator.isValid(singleton(params), typeClass);
    assertTrue(result);
  }

  @Test
  void isValidIfFieldsExist() {
    Class<?> typeClass = Company.class;
    Set<String> params = new HashSet<>();
    params.add("name");
    params.add("description");
    boolean result = SearchRequestParamValidator.isValid(params, typeClass);
    assertTrue(result);
  }

  @Test
  void isInvalidIfFieldDoesNotExist() {
    Class<?> typeClass = Company.class;
    boolean result = SearchRequestParamValidator.isValid(singleton("whatever"), typeClass);
    assertFalse(result);
  }

  @Test
  void isValidIfNotParams() {
    Class<?> typeClass = Company.class;
    boolean result = SearchRequestParamValidator.isValid(Collections.emptySet(), typeClass);
    assertTrue(result);
  }

  @Test
  void isInvalidIfOneFieldDoesNotExist() {
    Class<?> typeClass = Company.class;
    Set<String> params = new HashSet<>();
    params.add("name");
    params.add("description");
    params.add("whatever");
    boolean result = SearchRequestParamValidator.isValid(params, typeClass);
    assertFalse(result);
  }

  @Test
  void testValidSearchWithLists() {
    List<String> qf = Arrays.asList("name", "age");
    List<String> qt = Arrays.asList("Joe", "35");
    boolean result = SearchRequestParamValidator.isValid(qf, qt);
    assertTrue(result);
  }

  @Test
  void testValidSearchWithLists_NullOrEmptyLists() {
    List<String> qf = null;
    List<String> qt = new ArrayList<>();
    boolean result = SearchRequestParamValidator.isValid(qf, qt);
    assertTrue(result);
  }

  @Test
  void testInvalidSearchWithLists_UnequalListSize() {
    List<String> qf = Arrays.asList("name", "age");
    List<String> qt = Arrays.asList("Joe");
    boolean result = SearchRequestParamValidator.isValid(qf, qt);
    assertFalse(result);
  }

  @Test
  void testInvalidSearchWithLists_NullTermValue() {
    List<String> qf = Arrays.asList("name", "age");
    List<String> qt = Arrays.asList("Joe", null);
    boolean result = SearchRequestParamValidator.isValid(qf, qt);
    assertFalse(result);
  }

  @Test
  void testInvalidSearchWithLists_OneListNullOrEmpty() {
    List<String> qf = Arrays.asList("name", "age");
    List<String> qt = null;
    boolean result = SearchRequestParamValidator.isValid(qf, qt);
    assertFalse(result);
  }

  @Test
  void testWrapperClassVariablesSearch() {
    Class<?> typeClass = Site.class;
    boolean result = SearchRequestParamValidator.isValid(singleton("companyPid"), typeClass);
    assertTrue(result);
  }

  @Test
  void testWithInvalidParameters() {
    Class<?> typeClass = Company.class;
    boolean result = SearchRequestParamValidator.isValid(singleton(null), typeClass);
    assertFalse(result);
    result = SearchRequestParamValidator.isValid(singleton(StringUtils.EMPTY), typeClass);
    assertFalse(result);
  }
}
