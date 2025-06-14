package com.nexage.admin.core.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.enums.UserIdBidRequestType;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class UserIdBidRequestTypeConverterTest {

  @Test
  void testConvertToDatabaseColumn() {
    UserIdBidRequestTypeConverter converter = new UserIdBidRequestTypeConverter();
    Map<UserIdBidRequestType, Integer> map =
        Map.of(
            UserIdBidRequestType.AXID, 2,
            UserIdBidRequestType.MUID, 4);

    assertNull(converter.convertToDatabaseColumn(UserIdBidRequestType.UNKNOWN));
    assertEquals(
        map.keySet().stream().map(map::get).collect(Collectors.toUnmodifiableList()),
        map.keySet().stream()
            .map(converter::convertToDatabaseColumn)
            .collect(Collectors.toUnmodifiableList()));
  }

  @Test
  void testConvertToEntityAttribute() {
    UserIdBidRequestTypeConverter converter = new UserIdBidRequestTypeConverter();
    Map<Integer, UserIdBidRequestType> map =
        Map.of(
            2, UserIdBidRequestType.AXID,
            4, UserIdBidRequestType.MUID);

    assertEquals(UserIdBidRequestType.UNKNOWN, converter.convertToEntityAttribute(null));
    assertEquals(
        map.keySet().stream().map(map::get).collect(Collectors.toUnmodifiableList()),
        map.keySet().stream()
            .map(converter::convertToEntityAttribute)
            .collect(Collectors.toUnmodifiableList()));

    // Former AXID_A9 value. Should map to UNKNOWN.
    assertEquals(UserIdBidRequestType.UNKNOWN, converter.convertToEntityAttribute(5));
  }
}
