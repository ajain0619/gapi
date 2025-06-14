package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SellerDomainVerificationAuthLevelTest {

  @Test
  void testTypeNull_returnDefault() {
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER,
        SellerDomainVerificationAuthLevel.getFromValue(null),
        "null is invalid SellerDomainVerificationAuthLevel");
  }

  @Test
  void testType_returnAllowBasedOnBidder() {
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER,
        SellerDomainVerificationAuthLevel.getFromValue(0),
        "Unexpected value returned! Expected Value: ALLOW_BASED_ON_BIDDER");
  }

  @Test
  void testType_returnAllowAuthorizedAndUncategorized() {
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED,
        SellerDomainVerificationAuthLevel.getFromValue(1),
        "Unexpected value returned! Expected Value: ALLOW_AUTHORIZED_AND_UNCATEGORIZED");
  }

  @Test
  void testType_returnAllowOnlyAuthorized() {
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_ONLY_AUTHORIZED,
        SellerDomainVerificationAuthLevel.getFromValue(2),
        "Unexpected value returned! Expected Value: ALLOW_ONLY_AUTHORIZED");
  }

  @Test
  void testAllowBasedOnBidderOrdinal() {
    assertEquals(
        0,
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER.ordinal(),
        "Unexpected value returned! Expected Value: 0");
  }

  @Test
  void testAllowAuthorizedAndUncategorizedOrdinal() {
    assertEquals(
        1,
        SellerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_AND_UNCATEGORIZED.ordinal(),
        "Unexpected value returned! Expected Value: 1");
  }

  @Test
  void testAllowOnlyAuthorizedOrdinal() {
    assertEquals(
        2,
        SellerDomainVerificationAuthLevel.ALLOW_ONLY_AUTHORIZED.ordinal(),
        "Unexpected value returned! Expected Value: 2");
  }
}
