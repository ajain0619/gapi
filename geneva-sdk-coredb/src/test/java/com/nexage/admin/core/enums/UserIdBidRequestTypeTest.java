package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class UserIdBidRequestTypeTest {
  @Test
  void testEnumPresent() {
    assertEquals(
        Set.of(UserIdBidRequestType.UNKNOWN, UserIdBidRequestType.AXID, UserIdBidRequestType.MUID),
        Set.of(UserIdBidRequestType.values()));
  }

  @Test
  void testValueToEnumMap() {
    Map<Integer, UserIdBidRequestType> map =
        Map.of(
            2, UserIdBidRequestType.AXID,
            4, UserIdBidRequestType.MUID);

    assertEquals(UserIdBidRequestType.UNKNOWN, UserIdBidRequestType.valueOf((Integer) null));
    assertEquals(
        map.keySet().stream().map(map::get).collect(Collectors.toUnmodifiableList()),
        map.keySet().stream()
            .map(UserIdBidRequestType::valueOf)
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void testEnumToValueMap() {
    Map<UserIdBidRequestType, Integer> map =
        Map.of(
            UserIdBidRequestType.AXID, 2,
            UserIdBidRequestType.MUID, 4);

    assertNull(UserIdBidRequestType.UNKNOWN.getValue());
    assertEquals(
        map.keySet().stream().map(map::get).collect(Collectors.toUnmodifiableList()),
        map.keySet().stream()
            .map(UserIdBidRequestType::getValue)
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void testValueToEnumOutOfBounds() {
    assertTrue(
        IntStream.of(-4, -3, -2, 6, 7)
            .mapToObj(UserIdBidRequestType::valueOf)
            .allMatch(i -> i == UserIdBidRequestType.UNKNOWN));
  }
}
