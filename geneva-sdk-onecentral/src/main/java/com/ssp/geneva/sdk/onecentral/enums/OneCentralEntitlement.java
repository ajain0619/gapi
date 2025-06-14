package com.ssp.geneva.sdk.onecentral.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enumeration representing OneCentral entitlements. */
@AllArgsConstructor
public enum OneCentralEntitlement {
  ADMIN("admin"),
  API("api"),
  API_IIQ("apiiiq"),
  BUYER("buyer"),
  DEAL("deal"),
  MANAGER("manager"),
  NEXAGE("nexage"),
  USER("user"),
  SEAT_HOLDER("seatholder"),
  SELLER("seller"),
  SELLER_SEAT("sellerseat"),
  SMARTEX("smartex"),
  YIELD("yield"),
  AD_SCREENING("adscreening");

  @Getter private final String value;
}
