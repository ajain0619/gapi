package com.nexage.admin.core.enums;

import java.util.stream.Stream;

public enum PublisherDataProtectionRole {
  THIRD_PARTY_TCF(0),
  FIRST_PARTY(1),
  THIRD_PARTY_NO_TCF(2),
  THIRD_PARTY_NO_GDPR(3);

  private final Integer value;

  PublisherDataProtectionRole(Integer value) {
    this.value = value;
  }

  public Integer getExternalValue() {
    return value;
  }

  /**
   * This method will take in an {@link Integer} value from the database and test it against the
   * values of {@link PublisherDataProtectionRole} or return the defined default value,
   * THIRD_PARTY_TCF
   *
   * @param value
   * @return the PublisherDataProtectionParty corresponding to the value, or the default otherwise
   */
  public static PublisherDataProtectionRole from(Integer value) {
    PublisherDataProtectionRole defaultValue = PublisherDataProtectionRole.THIRD_PARTY_TCF;
    return Stream.of(values())
        .filter(source -> source.value.equals(value))
        .findFirst()
        .orElse(defaultValue);
  }
}
