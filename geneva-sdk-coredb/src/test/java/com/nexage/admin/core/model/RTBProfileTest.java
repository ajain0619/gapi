package com.nexage.admin.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.sparta.jpa.model.RTBProfileBidder;
import com.nexage.admin.core.sparta.jpa.model.RTBProfileLibraryAssociation;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RTBProfileTest {

  @Test
  void shouldReturnNullWhenBiddersFilterWhitelistNotSet() {
    RTBProfile rtbProfile = new RTBProfile();
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterAllowlistNotSet() {
    RTBProfile rtbProfile = new RTBProfile();
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterWhitelistSetTrue() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(true);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterWhitelistSetFalse() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(false);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetTrue() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterAllowlistSetFalse() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetTrueAndWhitelistSetFalse() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    rtbProfile.setBiddersFilterWhitelist(false);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterWhitelistSetTrueAndAllowlistSetFalse() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(true);
    rtbProfile.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterWhitelistSetNull() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(null);
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterAllowlistSetNull() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(null);
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterWhitelistSetAndAllowlistNull() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(true);
    rtbProfile.setBiddersFilterAllowlist(null);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetAndWhitelistNull() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    rtbProfile.setBiddersFilterWhitelist(null);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterAllowlistAndWhitelistSetTrue() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterAllowlist(true);
    rtbProfile.setBiddersFilterWhitelist(true);
    assertTrue(rtbProfile.getBiddersFilterWhitelist());
    assertTrue(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterWhitelistAndAllowlistSetFalse() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(false);
    rtbProfile.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfile.getBiddersFilterWhitelist());
    assertFalse(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterWhitelistAndAllowlistSetNull() {
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setBiddersFilterWhitelist(null);
    rtbProfile.setBiddersFilterAllowlist(null);
    assertNull(rtbProfile.getBiddersFilterWhitelist());
    assertNull(rtbProfile.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnCorrectIntegerValueForScreeningLevel() {
    // Given
    RTBProfile rtbProfile = new RTBProfile();

    // When
    rtbProfile.setScreeningLevel(RTBProfile.ScreeningLevel.AllowAll);

    // Then
    assertEquals(0, rtbProfile.getScreeningLevelValue(), "ScreeningLevel int value does not match");
  }

  @Test
  void shouldCorrectlyResetLibraries() {
    // Given
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setLibraries(Set.of(new RTBProfileLibraryAssociation()));

    // When
    rtbProfile.resetRtbProfileLibraries();

    // Then
    assertEquals(
        0,
        rtbProfile.getLibraries().size(),
        "Libraries were not reset to an empty set. Size: " + rtbProfile.getLibraries().size());
  }

  @Test
  void shouldCorrectlyAssignFieldsWithCustomSetterLogicWhenStringFieldsNull() {
    // Given
    RTBProfile rtbProfile = new RTBProfile();

    // When
    rtbProfile.setBlockedExternalDataProviderMap(Map.of(1L, "ABC", 2L, "XYZ"));
    rtbProfile.setBidderFilterMap(Map.of(3L, "DEF", 4L, "PQR"));

    // Then
    assertEquals(
        Map.of(1L, "ABC", 2L, "XYZ"),
        rtbProfile.getBlockedExternalDataProviderMap(),
        "blockedExternalDataProviderMap not set correctly");
    assertTrue(
        rtbProfile.getBlockedExternalDataProviders().contains("1")
            && rtbProfile.getBlockedExternalDataProviders().contains("2"),
        "blockedExternalDataProviders not set correctly");
    assertEquals(
        Map.of(3L, "DEF", 4L, "PQR"),
        rtbProfile.getBidderFilterMap(),
        "bidderFilterMap not set correctly");
    assertTrue(
        rtbProfile.getBidderFilterList().contains("3")
            && rtbProfile.getBidderFilterList().contains("4"),
        "bidderFilterList not set correctly");
  }

  @Test
  void shouldCorrectlyAssignFieldsWithCustomSetterLogicWhenStringFieldsNotNull() {
    // Given
    RTBProfile rtbProfile = new RTBProfile();

    // When
    rtbProfile.setBlockedExternalDataProviders("ABC");
    rtbProfile.setBlockedExternalDataProviderMap(Map.of(1L, "ABC", 2L, "XYZ"));
    rtbProfile.setBidderFilterList("XYZ");
    rtbProfile.setBidderFilterMap(Map.of(3L, "DEF", 4L, "PQR"));

    // Then
    assertEquals(
        Map.of(1L, "ABC", 2L, "XYZ"),
        rtbProfile.getBlockedExternalDataProviderMap(),
        "blockedExternalDataProviderMap not set correctly");
    assertFalse(
        rtbProfile.getBlockedExternalDataProviders().contains("1")
            && rtbProfile.getBlockedExternalDataProviders().contains("2"),
        "blockedExternalDataProviders not set correctly");
    assertEquals(
        Map.of(3L, "DEF", 4L, "PQR"),
        rtbProfile.getBidderFilterMap(),
        "bidderFilterMap not set correctly");
    assertFalse(
        rtbProfile.getBidderFilterList().contains("3")
            && rtbProfile.getBidderFilterList().contains("4"),
        "bidderFilterList not set correctly");
  }

  @Test
  void shouldNullifyEmptyStringsOnCreation() {
    // Given
    RTBProfile withEmptyStrings = new RTBProfile();

    withEmptyStrings.setBlockedAdTypes("");
    withEmptyStrings.setBlockedAdCategories("");
    withEmptyStrings.setBlockedAdvertisers("");
    withEmptyStrings.setBlockedAttributes("");

    // When
    withEmptyStrings.onCreate();

    // Then
    assertNull(withEmptyStrings.getBlockedAdTypes(), "blockedAdTypes was not nullified");
    assertNull(withEmptyStrings.getBlockedAdCategories(), "blockedAdCategories was not nullified");
    assertNull(withEmptyStrings.getBlockedAdvertisers(), "blockedAdvertisers was not nullified");
    assertNull(withEmptyStrings.getBlockedAttributes(), "blockedAttributes was not nullified");
  }

  @Test
  void shouldDoNothingToNullStringsOnCreation() {
    // Given
    RTBProfile withNullStrings = new RTBProfile();

    // When
    withNullStrings.onCreate();

    // Then
    assertNull(withNullStrings.getBlockedAdTypes(), "blockedAdTypes modified in error");
    assertNull(withNullStrings.getBlockedAdCategories(), "blockedAdCategories modified in error");
    assertNull(withNullStrings.getBlockedAdvertisers(), "blockedAdvertisers modified in error");
    assertNull(withNullStrings.getBlockedAttributes(), "blockedAttributes modified in error");
  }

  @Test
  void shouldCreateDistinctClone() throws CloneNotSupportedException {
    // Given
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.onCreate();
    rtbProfile.setPid(1L);
    rtbProfile.setBlockedExternalDataProviderMap(Map.of(1L, "ABC"));
    rtbProfile.setBidderFilterMap(Map.of(1L, "ABC"));
    rtbProfile.setLibraryPids(Set.of(1L));
    rtbProfile.setBidderSeatWhitelists(Set.of(new RTBProfileBidder()));
    rtbProfile.setLibraries(Set.of(new RTBProfileLibraryAssociation()));

    // When
    RTBProfile clone = rtbProfile.clone();

    // Then
    assertEquals(rtbProfile, clone);
    assertEquals(rtbProfile.getCreationDate(), clone.getCreationDate());
    assertEquals(rtbProfile.getLastUpdate(), clone.getLastUpdate());
    assertEquals(
        rtbProfile.getBlockedExternalDataProviderMap(), clone.getBlockedExternalDataProviderMap());
    assertEquals(rtbProfile.getBidderFilterMap(), clone.getBidderFilterMap());
    assertEquals(rtbProfile.getLibraryPids(), clone.getLibraryPids());
    assertEquals(rtbProfile.getBidderSeatWhitelists(), clone.getBidderSeatWhitelists());
    assertEquals(rtbProfile.getLibraries(), clone.getLibraries());

    assertNotSame(rtbProfile, clone);
    assertNotSame(rtbProfile.getCreationDate(), clone.getCreationDate());
    assertNotSame(rtbProfile.getLastUpdate(), clone.getLastUpdate());
    assertNotSame(
        rtbProfile.getBlockedExternalDataProviderMap(), clone.getBlockedExternalDataProviderMap());
    assertNotSame(rtbProfile.getBidderFilterMap(), clone.getBidderFilterMap());
    assertNotSame(rtbProfile.getLibraryPids(), clone.getLibraryPids());
    assertNotSame(rtbProfile.getBidderSeatWhitelists(), clone.getBidderSeatWhitelists());
    assertNotSame(rtbProfile.getLibraries(), clone.getLibraries());
  }
}
