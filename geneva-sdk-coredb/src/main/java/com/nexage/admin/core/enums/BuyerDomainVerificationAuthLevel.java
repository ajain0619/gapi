package com.nexage.admin.core.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public enum BuyerDomainVerificationAuthLevel {
  ALLOW_ALL(0),
  ALLOW_AUTHORIZED(1),
  ALLOW_AUTHORIZED_UNCATEGORIZED(2);

  @Getter private final int value;

  public static BuyerDomainVerificationAuthLevel getFromValue(Integer level) {
    return (level == null || fromIntMap.get(level) == null) ? defaultType() : fromIntMap.get(level);
  }

  public static BuyerDomainVerificationAuthLevel defaultType() {
    return ALLOW_ALL;
  }

  private static final Map<Integer, BuyerDomainVerificationAuthLevel> fromIntMap =
      Stream.of(values())
          .collect(
              Collectors.toMap(BuyerDomainVerificationAuthLevel::getValue, Function.identity()));
}
