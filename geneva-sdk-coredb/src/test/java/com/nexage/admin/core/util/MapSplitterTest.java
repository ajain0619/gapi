package com.nexage.admin.core.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapSplitterTest {

  private MapSplitter splitter;

  @BeforeEach
  public void setup() {
    System.out.println("before each");
    splitter = MapSplitter.separator(",").withKeyValueSeparator("=");
  }

  @Test
  void testSplit() {
    var input = "foo=bar,x=baz";
    var out = splitter.split(input);

    assertAll(
        "split test",
        () -> assertNotNull(out),
        () -> assertTrue(out.containsKey("foo")),
        () -> assertEquals("bar", out.get("foo")),
        () -> assertTrue(out.containsKey("x")),
        () -> assertEquals("baz", out.get("x")));
  }

  @Test
  void mapWithLongKey() {
    var input = "1=bar,2=baz";
    var out = splitter.splitAsLong(input);

    assertAll(
        "long key check",
        () -> assertNotNull(out),
        () -> assertTrue(out.containsKey(1L)),
        () -> assertEquals("bar", out.get(1L)),
        () -> assertTrue(out.containsKey(2L)),
        () -> assertEquals("baz", out.get(2L)));
  }
}
