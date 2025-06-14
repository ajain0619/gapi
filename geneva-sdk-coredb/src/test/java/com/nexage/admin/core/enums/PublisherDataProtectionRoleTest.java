package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PublisherDataProtectionRoleTest {
  @Test
  void publisherDataProtectionRoleFromValueReturn() {
    assertEquals(
        PublisherDataProtectionRole.THIRD_PARTY_TCF, PublisherDataProtectionRole.from(200));

    assertEquals(PublisherDataProtectionRole.THIRD_PARTY_TCF, PublisherDataProtectionRole.from(0));

    assertEquals(PublisherDataProtectionRole.FIRST_PARTY, PublisherDataProtectionRole.from(1));

    assertEquals(
        PublisherDataProtectionRole.THIRD_PARTY_NO_TCF, PublisherDataProtectionRole.from(2));

    assertEquals(
        PublisherDataProtectionRole.THIRD_PARTY_NO_GDPR, PublisherDataProtectionRole.from(3));
  }
}
