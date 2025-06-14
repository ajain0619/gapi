package com.nexage.admin.core.enums;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserIdBidRequestType {
  UNKNOWN(null),
  AXID(2),
  MUID(4);

  private Integer value;

  UserIdBidRequestType(Integer value) {
    this.value = value;
  }

  public static UserIdBidRequestType valueOf(Integer value) {
    return Arrays.stream(values())
        .filter(
            userIdBidRequestType ->
                (userIdBidRequestType.getValue() != null
                    ? userIdBidRequestType.getValue().equals(value)
                    : value == null))
        .findFirst()
        .orElse(UserIdBidRequestType.UNKNOWN);
  }
}
