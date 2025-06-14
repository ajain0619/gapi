package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.AdType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AdTypeMapperTest {

  @Test
  void shouldReturnNullWhenNullSet() {
    assertNull(AdTypeMapper.MAPPER.map((Set) null));
  }

  @Test
  void shouldReturnCsvStringWhenNonEmptySet() {
    var actual = AdTypeMapper.MAPPER.map(Set.of(AdType.IMAGE, AdType.SILENT_VIDEO)).split(",");
    assertEquals(2, actual.length);
    assertTrue(Arrays.stream(actual).anyMatch(adType -> adType.equals(AdType.IMAGE.getName())));
    assertTrue(
        Arrays.stream(actual).anyMatch(adType -> adType.equals(AdType.SILENT_VIDEO.getName())));
  }

  @Test
  void shouldReturnAdTypeSetWhenCsvString() {
    var stringValue =
        Stream.of(AdType.IMAGE, AdType.SILENT_VIDEO)
            .map(AdType::getName)
            .collect(Collectors.joining(","));

    assertEquals(2, AdTypeMapper.MAPPER.map(stringValue).size());
  }

  @Test
  void shouldReturnEmptySetWhenEmptyString() {
    assertTrue(AdTypeMapper.MAPPER.map("").isEmpty());
  }
}
