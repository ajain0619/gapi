package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RuleSearchRequestParamValidatorTest {

  @Test
  void isValidIfFieldAndTermDoNotExist() {
    boolean result = RuleSearchRequestParamValidator.isValid(null, null);
    assertTrue(result);
  }

  @Test
  void isValidIfFieldAndTermEmpty() {
    boolean result = RuleSearchRequestParamValidator.isValid(Collections.emptySet(), "");
    assertTrue(result);
  }

  @Test
  void isValidIfFieldExitTermDoesNotExist() {
    Set<String> fields = new HashSet<>();
    boolean result = RuleSearchRequestParamValidator.isValid(fields, null);
    assertTrue(result);
  }

  @Test
  void isValidIfFieldDoesNotExitTermExist() {
    String term = "";
    boolean result = RuleSearchRequestParamValidator.isValid(null, term);
    assertTrue(result);
  }

  @Test
  void isValidIfFieldValueCorrect() {
    Set<String> fields = new HashSet<>();
    fields.add("pid");
    fields.add("name");
    String term = "abc";
    boolean result = RuleSearchRequestParamValidator.isValid(fields, term);
    assertTrue(result);
  }

  @Test
  void isValidIfFieldValueIncorrect() {
    Set<String> fields = new HashSet<>();
    fields.add("abc");
    fields.add("name");
    String term = "abc";
    boolean result = RuleSearchRequestParamValidator.isValid(fields, term);
    assertFalse(result);
  }

  @Test
  void isValidIfFieldAndTermTypeDoesNotMatch() {
    Set<String> fields = new HashSet<>();
    fields.add("pid");
    String term = "abc";
    boolean result = RuleSearchRequestParamValidator.isValid(fields, term);
    assertFalse(result);
  }

  @Test
  void isValidIfFieldAndTermTypeMatches() {
    Set<String> fields = new HashSet<>();
    fields.add("pid");
    String term = "123";
    boolean result = RuleSearchRequestParamValidator.isValid(fields, term);
    assertTrue(result);
  }

  @Test
  void whenSearchingForPid_andValueIsNegativeNumeric_thenValidationIsFailed() {
    Set<String> fields = new HashSet<>();
    fields.add("pid");
    String term = "-123";
    boolean result = RuleSearchRequestParamValidator.isValid(fields, term);
    assertFalse(result);
  }
}
