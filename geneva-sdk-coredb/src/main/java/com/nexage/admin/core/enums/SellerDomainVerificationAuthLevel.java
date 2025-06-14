package com.nexage.admin.core.enums;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SellerDomainVerificationAuthLevel {
  ALLOW_BASED_ON_BIDDER(0),
  ALLOW_AUTHORIZED_AND_UNCATEGORIZED(1),
  ALLOW_ONLY_AUTHORIZED(2);

  @Getter private final int value;

  public static SellerDomainVerificationAuthLevel getFromValue(Integer level) {
    return (level == null || fromIntMap.get(level) == null) ? defaultType() : fromIntMap.get(level);
  }

  public static SellerDomainVerificationAuthLevel defaultType() {
    return ALLOW_BASED_ON_BIDDER;
  }

  private static final Map<Integer, SellerDomainVerificationAuthLevel> fromIntMap =
      Stream.of(values())
          .collect(
              Collectors.toMap(SellerDomainVerificationAuthLevel::getValue, Function.identity()));
}
