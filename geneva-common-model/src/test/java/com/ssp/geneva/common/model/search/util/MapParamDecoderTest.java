package com.ssp.geneva.common.model.search.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class MapParamDecoderTest {

  @Test
  void testDecodeStringCommonCase() {
    Map<String, String> map = MapParamDecoder.decodeString("{key=val}");
    assertEquals(1, map.size(), "Map generated from {key=val} should contain one entry");
    assertEquals("val", map.get("key"));
  }

  @Test
  void testDecodeStringWithMultipleEntries() {
    Map<String, String> map = MapParamDecoder.decodeString("key1=val, key2 = val");
    assertEquals(
        2, map.size(), "Map generated from two key-value pairs should contain two entries");
    assertEquals("val", map.get("key1"));
    assertEquals("val", map.get("key2"));
  }

  @Test
  void testDecodeStringWithEmptyKeyAndValue() {
    Map<String, String> map = MapParamDecoder.decodeString("=");
    assertEquals(1, map.size(), "Map generated from empty key-value pair should have one entry");
  }

  @Test
  void testDecodeStringWithNoEntries() {
    Map<String, String> map = MapParamDecoder.decodeString(",,");
    assertEquals(0, map.size(), "Map generated from invalid string should be empty");
  }

  @Test
  void testDecodeStringWithEmptyMap() {
    Map<String, String> map = MapParamDecoder.decodeString("{}");
    assertEquals(0, map.size(), "Map generated from empty map should be empty");
  }

  @Test
  void testDecodeQueryParamKeyValueSeparator() {
    MultiValueMap<String, String>
        map = MapParamDecoder.decodeQueryParam("{key1=val1,key2 = val2,key3 =val3, key4= val4}"),
        expected = new LinkedMultiValueMap<>();
    expected.add("key1", "val1");
    expected.add("key2", "val2");
    expected.add("key3", "val3");
    expected.add("key4", "val4");
    Assertions.assertEquals(expected, map);
  }

  @Test
  void testDecodeQueryParamKeysSeparator() {
    MultiValueMap<String, String>
        map = MapParamDecoder.decodeQueryParam("{ key1=val1 , key2=val2, key3=val3 ,key4=val4 }"),
        expected = new LinkedMultiValueMap<>();
    expected.add("key1", "val1");
    expected.add("key2", "val2");
    expected.add("key3", "val3");
    expected.add("key4", "val4");
    Assertions.assertEquals(expected, map);
  }

  @Test
  void testDecodeQueryParamValuesSeparator() {
    MultiValueMap<String, String>
        map =
            MapParamDecoder.decodeQueryParam(
                "{key1=val1|val2,key2=val3 |val4,key3=val5| val6,key4=val7 | val8}"),
        expected = new LinkedMultiValueMap<>();
    expected.addAll("key1", Arrays.asList("val1", "val2"));
    expected.addAll("key2", Arrays.asList("val3", "val4"));
    expected.addAll("key3", Arrays.asList("val5", "val6"));
    expected.addAll("key4", Arrays.asList("val7", "val8"));
    Assertions.assertEquals(expected, map);
  }

  @ParameterizedTest
  @CsvSource({
    "'{key1=val 1|val 2,key2=val 3}','val 1','val 2'", // Param value with white spaces
    "'{key1=v{}al1|v[]al2, ,,, key2=va()l 3}','val1', 'val2'", // Param emprty values and braces
    "'{key1=val 1|val 2,key2=val 3','val 1', 'val 2'" // Malformed but parsable
  })
  void shouldDecodeQueryParams(String queryParam, String value1, String value2) {
    MultiValueMap<String, String> map = MapParamDecoder.decodeQueryParam(queryParam),
        expected = new LinkedMultiValueMap<>();
    expected.addAll("key1", Arrays.asList(value1, value2));
    expected.add("key2", "val 3");
    Assertions.assertEquals(expected, map);
  }

  @Test
  void testDecodeQueryParamWithNull() {
    MultiValueMap<String, String> map = MapParamDecoder.decodeQueryParam(null);
    assertNotNull(map);
    Assertions.assertTrue(map.isEmpty());
  }

  @Test
  void testDecodeMalformedQuery() {
    MultiValueMap<String, String> map =
        MapParamDecoder.decodeQueryParam("{someTekstWithoutEqualsSign}");
    assertNotNull(map);
    Assertions.assertTrue(map.isEmpty());
  }

  @Test
  void testDecodeMultiValueMapCommonCase() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll("site", Arrays.asList("234", "456"));
    map.add("key2", "val2");
    Map<String, String> singleMap = MapParamDecoder.decodeMap(map), expected = new HashMap<>();
    expected.put("site", "234,456");
    expected.put("key2", "val2");
    assertEquals(expected, singleMap);
  }

  @Test
  void testDecodeMultiValueMapNullCase() {
    Map<String, String> map = MapParamDecoder.decodeMap(null);
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }
}
