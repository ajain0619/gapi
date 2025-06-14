package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BuyerDomainVerificationAuthLevelTest {

  @Test
  void testTypeNull_returnDefault() {
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_ALL,
        BuyerDomainVerificationAuthLevel.getFromValue(null),
        "null is invalid BuyerDomainVerificationAuthLevel");
  }

  @Test
  void testType_returnAllowAll() {
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_ALL,
        BuyerDomainVerificationAuthLevel.getFromValue(0),
        "Unexpected value returned! Expected Value: ALLOW_ALL");
  }

  @Test
  void testType_returnAllowAuthorized() {
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED,
        BuyerDomainVerificationAuthLevel.getFromValue(1),
        "Unexpected value returned! Expected Value: ALLOW_AUTHORIZED");
  }

  @Test
  void testType_returnAllowAuthorizedUncategorized() {
    assertEquals(
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_UNCATEGORIZED,
        BuyerDomainVerificationAuthLevel.getFromValue(2),
        "Unexpected value returned! Expected Value: ALLOW_AUTHORIZED_UNCATEGORIZED");
  }

  @Test
  void testAllowBasedOnBidderOrdinal() {
    assertEquals(
        0,
        BuyerDomainVerificationAuthLevel.ALLOW_ALL.ordinal(),
        "Unexpected value returned! Expected Value: 0");
  }

  @Test
  void testAllowAuthorizedOrdinal() {
    assertEquals(
        1,
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED.ordinal(),
        "Unexpected value returned! Expected Value: 1");
  }

  @Test
  void testAllowAuthorizedUncategorizedOrdinal() {
    assertEquals(
        2,
        BuyerDomainVerificationAuthLevel.ALLOW_AUTHORIZED_UNCATEGORIZED.ordinal(),
        "Unexpected value returned! Expected Value: 2");
  }
}
