package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.GlobalConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GlobalConfigUtilTest {

  @Test
  void shouldParseLongValueConfigWhenCorrectLongValueIsGiven() {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn("9876543210");

    // when
    Long parsedValue = GlobalConfigUtil.getLongValue(config);

    // then
    assertEquals(9876543210L, parsedValue);
  }

  @Test
  void shouldParseIntegerValueConfigWhenCorrectIntegerValueIsGiven() {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn("123");

    // when
    Integer parsedValue = GlobalConfigUtil.getIntegerValue(config);

    // then
    assertEquals(123, parsedValue);
  }

  @Test
  void shouldParseBooleanValueConfigWhenCorrectBooleanValueIsGiven() {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn("true");

    // when
    Boolean parsedValue = GlobalConfigUtil.getBooleanValue(config);

    // then
    assertTrue(parsedValue);
  }

  @Test
  void shouldParseCsvValueAsLongListWhenCorrectCsvValueIsGiven() {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn("123,234,345");

    // when
    List<Long> parsedValue = GlobalConfigUtil.getCsvValueAsLongList(config);

    // then
    assertEquals(List.of(123L, 234L, 345L), parsedValue);
  }

  @ParameterizedTest
  @ValueSource(strings = {"123L", "nonLongValue", "a25", "--123", " ", ""})
  void shouldReturnNullOnParsingNonLongValueAsLong(String value) {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn(value);

    // when
    Long parsedValue = GlobalConfigUtil.getLongValue(config);

    // then
    assertNull(parsedValue);
  }

  @ParameterizedTest
  @ValueSource(strings = {"9876543210", "nonIntValue", "a25", "--123", " ", ""})
  void shouldReturnNullOnParsingNonIntegerValueAsInteger(String value) {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn(value);

    // when
    Integer parsedValue = GlobalConfigUtil.getIntegerValue(config);

    // then
    assertNull(parsedValue);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "nonBoolValue", "0", ""})
  void shouldReturnFalseOnParsingNonBooleanValueAsBoolean(String value) {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn(value);

    // when
    Boolean parsedValue = GlobalConfigUtil.getBooleanValue(config);

    // then
    assertFalse(parsedValue);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "", ",", ",,", "5.6,3,5", "56,abba,67"})
  void shouldReturnEmptyListOnParsingIncorrectCsvValueAsLongList(String value) {
    // given
    GlobalConfig config = mock(GlobalConfig.class);
    when(config.getValue()).thenReturn(value);

    // when
    List<Long> parsedValue = GlobalConfigUtil.getCsvValueAsLongList(config);

    // then
    assertTrue(parsedValue.isEmpty());
  }
}
