package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UtilsTest {

  @Test
  void shouldEncodeArray() {
    byte[] data = "hello world".getBytes();
    String encodedValue = Utils.encode(data);
    assertNotNull(encodedValue);
    assertEquals("aGVsbG8gd29ybGQ.", encodedValue);
  }

  @Test
  void shouldSafeEncodeArray() {
    byte[] data = "hello world".getBytes();
    String safeEncodedValue = Utils.safeEncode(data);
    assertNotNull(safeEncodedValue);
    assertEquals("aGVsbG8gd29ybGQ", safeEncodedValue);
  }

  @Test
  void shouldConvertArrayToString() {
    String[] data = {"hello", "world"};
    String safeEncodedValue = Utils.convertArrayToString(data, ",");
    assertNotNull(safeEncodedValue);
    assertEquals("hello,world,", safeEncodedValue);
  }
}
