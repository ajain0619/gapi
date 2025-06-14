package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PubTagHierarchyDTOTest {
  @Test
  void shouldReturnNullWhenFilterBiddersWhitelistNotSet() {
    PubTagHierarchyDTO pubTagHierarchyDTO = PubTagHierarchyDTO.newBuilder().build();
    assertNull(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertNull(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersAllowlistNotSet() {
    PubTagHierarchyDTO pubTagHierarchyDTO = PubTagHierarchyDTO.newBuilder().build();
    assertNull(pubTagHierarchyDTO.getFilterBiddersAllowlist());
    assertNull(pubTagHierarchyDTO.getFilterBiddersWhitelist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersWhitelistSetTrue() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersWhitelist(true).build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersWhitelistSetFalse() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersWhitelist(false).build();
    assertFalse(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertFalse(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetTrue() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersAllowlist(true).build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersAllowlistSetFalse() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersAllowlist(false).build();
    assertFalse(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertFalse(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetTrueAndWhitelistSetFalse() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersAllowlist(true)
            .withFilterBiddersWhitelist(false)
            .build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersWhitelistSetTrueAndAllowlistSetFalse() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersWhitelist(true)
            .withFilterBiddersAllowlist(false)
            .build();
    assertFalse(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertFalse(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersWhitelistSetNull() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersWhitelist(null).build();
    assertNull(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertNull(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersAllowlistSetNull() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder().withFilterBiddersAllowlist(null).build();
    assertNull(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertNull(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersWhitelistSetAndAllowlistNull() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersWhitelist(true)
            .withFilterBiddersAllowlist(null)
            .build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetAndWhitelistNull() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersAllowlist(true)
            .withFilterBiddersWhitelist(null)
            .build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersAllowlistAndWhitelistSetTrue() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersAllowlist(true)
            .withFilterBiddersWhitelist(true)
            .build();
    assertTrue(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertTrue(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersWhitelistAndAllowlistSetFalse() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersWhitelist(false)
            .withFilterBiddersAllowlist(false)
            .build();
    assertFalse(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertFalse(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersWhitelistAndAllowlistSetNull() {
    PubTagHierarchyDTO pubTagHierarchyDTO =
        PubTagHierarchyDTO.newBuilder()
            .withFilterBiddersWhitelist(null)
            .withFilterBiddersAllowlist(null)
            .build();
    assertNull(pubTagHierarchyDTO.getFilterBiddersWhitelist());
    assertNull(pubTagHierarchyDTO.getFilterBiddersAllowlist());
  }
}
