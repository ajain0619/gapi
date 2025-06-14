package com.ssp.geneva.sdk.onecentral.enums;

import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.ADMIN;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.API;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.API_IIQ;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.BUYER;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.DEAL;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.MANAGER;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.NEXAGE;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.SEAT_HOLDER;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.SELLER;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.SELLER_SEAT;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.USER;
import static com.ssp.geneva.sdk.onecentral.enums.OneCentralEntitlement.YIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class OneCentralEntitlementTest {

  @ParameterizedTest
  @MethodSource("getEntitlementArgs")
  void shouldReturnAdScreeningValue(String value, OneCentralEntitlement entitlement) {
    assertEquals(value, entitlement.getValue());
  }

  private static Stream<Arguments> getEntitlementArgs() {
    return Stream.of(
        Arguments.of("admin", ADMIN),
        Arguments.of("api", API),
        Arguments.of("apiiiq", API_IIQ),
        Arguments.of("buyer", BUYER),
        Arguments.of("deal", DEAL),
        Arguments.of("manager", MANAGER),
        Arguments.of("nexage", NEXAGE),
        Arguments.of("user", USER),
        Arguments.of("seatholder", SEAT_HOLDER),
        Arguments.of("seller", SELLER),
        Arguments.of("sellerseat", SELLER_SEAT),
        Arguments.of("yield", YIELD));
  }
}
