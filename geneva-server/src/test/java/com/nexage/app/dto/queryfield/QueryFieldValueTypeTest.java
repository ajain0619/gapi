package com.nexage.app.dto.queryfield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class QueryFieldValueTypeTest {

  @Test
  void shouldReturnTrueForValidStringValue() {
    assertThat(QueryFieldValueType.STRING.valid("name"), is(true));
  }

  @Test
  void shouldReturnTrueForTrueBooleanValue() {
    assertThat(QueryFieldValueType.BOOL.valid("true"), is(true));
  }

  @Test
  void shouldReturnTrueForFalseBooleanValue() {
    assertThat(QueryFieldValueType.BOOL.valid("false"), is(true));
  }

  @Test
  void shouldReturnFalseForInvalidBooleanValue() {
    assertThat(QueryFieldValueType.BOOL.valid("1_"), is(false));
  }

  @Test
  void shouldReturnTrueForNegativeLongValue() {
    assertThat(QueryFieldValueType.LONG.valid("-1"), is(true));
  }

  @Test
  void shouldReturnTrueForPositiveLongValue() {
    assertThat(QueryFieldValueType.LONG.valid("1"), is(true));
  }

  @Test
  void shouldReturnFalseForInvalidLongValue() {
    assertThat(QueryFieldValueType.LONG.valid("1_"), is(false));
  }

  @Test
  void shouldReturnTrueForNegativeIntegerValue() {
    assertThat(QueryFieldValueType.INTEGER.valid("-1"), is(true));
  }

  @Test
  void shouldReturnTrueForPositiveIntegerValue() {
    assertThat(QueryFieldValueType.INTEGER.valid("1"), is(true));
  }

  @Test
  void shouldReturnFalseForInvalidIntegerValue() {
    assertThat(QueryFieldValueType.INTEGER.valid("1_"), is(false));
  }
}
