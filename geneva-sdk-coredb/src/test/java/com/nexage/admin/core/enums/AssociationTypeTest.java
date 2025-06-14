package com.nexage.admin.core.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AssociationTypeTest {

  @Test
  void testTypeNull_returnDefaultType() {
    assertEquals(
        AssociationType.NON_DEFAULT, AssociationType.getFromValue(null), "Invalid AssociationTyoe");
  }

  @Test
  void testType_returnDefaultType() {
    assertEquals(
        AssociationType.DEFAULT, AssociationType.getFromValue(1), "Invalid AssociationTyoe");
  }

  @Test
  void testType_returnNonDefaultType() {
    assertEquals(
        AssociationType.NON_DEFAULT, AssociationType.getFromValue(0), "Invalid AssociationType");
  }

  @Test
  void shouldCorrectlyMapBannerDefaultType() {
    assertEquals(
        AssociationType.DEFAULT_BANNER, AssociationType.getFromValue(2), "Invalid AssociationType");
  }

  @Test
  void shouldCorrectlyMapVideoDefaultType() {
    assertEquals(
        AssociationType.DEFAULT_VIDEO, AssociationType.getFromValue(3), "Invalid AssociationType");
  }

  @Test
  void testTypeForUnknownValue_returnNonDefaultType() {
    assertEquals(
        AssociationType.NON_DEFAULT, AssociationType.getFromValue(4), "Invalid AssociationType");
  }
}
