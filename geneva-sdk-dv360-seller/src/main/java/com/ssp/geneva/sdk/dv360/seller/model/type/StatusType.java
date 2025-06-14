package com.ssp.geneva.sdk.dv360.seller.model.type;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum StatusType {
  DISCOVERY_OBJECT_STATUS_UNSPECIFIED,
  DISCOVERY_OBJECT_STATUS_REMOVED,
  DISCOVERY_OBJECT_STATUS_PAUSED,
  DISCOVERY_OBJECT_STATUS_ACTIVE,
  DISCOVERY_OBJECT_STATUS_ARCHIVED;

  /**
   * Returns the all valid Auction Package valid status.
   *
   * @return {@link StatusType}
   */
  public static String getValues() {
    return Stream.of(StatusType.values()).map(Enum::name).collect(Collectors.joining(","));
  }

  /**
   * Return Valid StatusType
   *
   * @param status
   * @return
   */
  public static Optional<StatusType> getStatusType(String status) {
    return Stream.of(StatusType.values())
        .filter(statusType -> statusType.name().equalsIgnoreCase(status))
        .findFirst();
  }
}
