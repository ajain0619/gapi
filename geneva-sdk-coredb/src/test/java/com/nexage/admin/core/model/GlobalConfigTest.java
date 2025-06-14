package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.util.GlobalConfigUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class GlobalConfigTest {

  @Test
  void shouldReturnNullOnInvalidPropertyName() {
    GlobalConfigProperty property = GlobalConfigProperty.fromPropertyName("");
    assertNull(property);
  }

  @Test
  void shouldReturnProperGlobalConfigProperty() {
    GlobalConfigProperty property = GlobalConfigProperty.fromPropertyName("crs.sso.endpoint");
    assertNotNull(property);
  }

  @Test
  void shouldReturnStringValue() {
    // given
    var value = "value";
    var config = new GlobalConfig();
    config.setValue(value);

    // when
    String returnedValue = config.getStringValue();

    // then
    assertEquals(value, returnedValue);
  }

  @Test
  void shouldReturnParsedLongValue() {
    // given
    var config = new GlobalConfig();
    var parsedValue = 123L;

    try (MockedStatic<GlobalConfigUtil> util = mockStatic(GlobalConfigUtil.class)) {
      util.when(() -> GlobalConfigUtil.getLongValue(config)).thenReturn(parsedValue);

      // when
      Long returnedValue = config.getLongValue();

      // then
      assertEquals(parsedValue, returnedValue);
    }
  }

  @Test
  void shouldReturnParsedIntegerValue() {
    // given
    var config = new GlobalConfig();
    var parsedValue = 123;

    try (MockedStatic<GlobalConfigUtil> util = mockStatic(GlobalConfigUtil.class)) {
      util.when(() -> GlobalConfigUtil.getIntegerValue(config)).thenReturn(parsedValue);

      // when
      Integer returnedValue = config.getIntegerValue();

      // then
      assertEquals(parsedValue, returnedValue);
    }
  }

  @Test
  void shouldReturnParsedBooleanValue() {
    // given
    var config = new GlobalConfig();
    var parsedValue = true;

    try (MockedStatic<GlobalConfigUtil> util = mockStatic(GlobalConfigUtil.class)) {
      util.when(() -> GlobalConfigUtil.getBooleanValue(config)).thenReturn(parsedValue);

      // when
      Boolean returnedValue = config.getBooleanValue();

      // then
      assertEquals(parsedValue, returnedValue);
    }
  }

  @Test
  void shouldReturnParsedCsvValue() {
    // given
    var config = new GlobalConfig();
    List<Long> parsedValue = List.of(123L, 234L, 345L);

    try (MockedStatic<GlobalConfigUtil> util = mockStatic(GlobalConfigUtil.class)) {
      util.when(() -> GlobalConfigUtil.getCsvValueAsLongList(config)).thenReturn(parsedValue);

      // when
      List<Long> returnedValue = config.getLongListValue();

      // then
      assertEquals(parsedValue, returnedValue);
    }
  }
}
