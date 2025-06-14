package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SetMapperTest {

  @Test
  void shouldReturnNullWhenSetIsNull() {
    Set set = null;
    assertNull(SetMapper.MAPPER.map(set));
  }

  @Test
  void shouldReturnStringWhenSetIsNotEmpty() {
    var actual = SetMapper.MAPPER.map(Set.of("123", "abc")).split(",");
    assertEquals(2, actual.length);
    assertTrue(Arrays.stream(actual).anyMatch(string -> "123".equals(string)));
    assertTrue(Arrays.stream(actual).anyMatch(string -> "abc".equals(string)));
  }

  @Test
  void shouldMapStringToSet() {
    var strings = SetMapper.MAPPER.map("test,this,string");
    assertEquals(3, strings.size());
  }

  @Test
  void shouldMapEmptyStringToEmptySet() {
    assertTrue(SetMapper.MAPPER.map("").isEmpty());
  }
}
