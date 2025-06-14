package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class RuleTargetDataValidationHelperTest {

  @Test
  void shouldReturnListWhenValidData() {
    String data = "12,13,123,500";

    List<Long> result = RuleTargetDataValidationHelper.convertToList(data, ",");

    assertEquals(4, result.size());
    assertTrue(result.containsAll(List.of(12L, 13L, 123L, 500L)));
  }

  @Test
  void shouldReturnListWhenValidDataWithEmptySpaces() {
    String data = "12, 13,  123,  500";

    List<Long> result = RuleTargetDataValidationHelper.convertToList(data, ",");

    assertEquals(4, result.size());
    assertTrue(result.containsAll(List.of(12L, 13L, 123L, 500L)));
  }

  @Test
  void shouldReturnEmptyListWhenDataIsEmpty() {
    List<Long> result = RuleTargetDataValidationHelper.convertToList("   ", ",");
    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenDataNotValid() {
    String data = "12,13,abc,test";

    List<Long> result = RuleTargetDataValidationHelper.convertToList(data, ",");

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenWrongDelimiter() {
    String data = "12,13,123,500";

    List<Long> result = RuleTargetDataValidationHelper.convertToList(data, ";");

    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnTrueWhenListHasUniqueElements() {
    List<Long> list = List.of(12L, 13L, 123L, 500L);
    assertTrue(RuleTargetDataValidationHelper.hasUniqueElements(list));
  }

  @Test
  void shouldReturnFalseWhenListHasDuplicateElements() {
    List<Long> list = List.of(12L, 13L, 123L, 13L);
    assertFalse(RuleTargetDataValidationHelper.hasUniqueElements(list));
  }
}
