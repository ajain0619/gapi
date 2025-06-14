package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DelimitedStringDecoderTest {

  @ParameterizedTest
  @MethodSource("getEmptyStringsToDecode")
  void shouldReturnEmptyDecodedList(String string) {
    List<Long> listOfLongs = DelimitedStringDecoder.decodeString(string);
    assertEquals(0, listOfLongs.size(), "List has the correct number of long values");
  }

  @Test
  void testDecodeWithSingleEntry() {
    List<Long> listOfLongs = DelimitedStringDecoder.decodeString("123");

    assertEquals(1, listOfLongs.size(), "List has the correct number of long values");
    assertEquals(123L, listOfLongs.get(0).longValue());
  }

  @Test
  void testDecodeWithMultipleEntries() {
    List<Long> listOfLongs = DelimitedStringDecoder.decodeString("123|344|222");

    assertEquals(3, listOfLongs.size(), "List has the correct number of long values");
    assertEquals(123L, listOfLongs.get(0).longValue());
    assertEquals(344L, listOfLongs.get(1).longValue());
    assertEquals(222L, listOfLongs.get(2).longValue());
  }

  @Test
  void testDecodeStringWithCharEntries() {
    assertThrows(
        IllegalArgumentException.class, () -> DelimitedStringDecoder.decodeString("a123|23"));
  }

  @Test
  void testDecodeStringWithStringEntries() {
    assertThrows(
        IllegalArgumentException.class, () -> DelimitedStringDecoder.decodeString("hello|23"));
  }

  private static Stream<String> getEmptyStringsToDecode() {
    return Stream.of(null, "", "|");
  }
}
