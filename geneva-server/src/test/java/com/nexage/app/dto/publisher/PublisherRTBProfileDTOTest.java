package com.nexage.app.dto.publisher;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PublisherRTBProfileDTOTest {

  @Test
  void shouldReturnNullWhenBidderFilterWhitelistNotSet() {
    PublisherRTBProfileDTO publisherRTBProfileDTO = PublisherRTBProfileDTO.newBuilder().build();
    assertNull(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertNull(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBidderFilterAllowlistNotSet() {
    PublisherRTBProfileDTO publisherRTBProfileDTO = PublisherRTBProfileDTO.newBuilder().build();
    assertNull(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertNull(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBidderFilterWhitelistSetTrue() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterWhitelist(true).build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBidderFilterWhitelistSetFalse() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterWhitelist(false).build();
    assertFalse(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertFalse(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBidderFilterAllowlistSetTrue() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterAllowlist(true).build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBidderFilterAllowlistSetFalse() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterAllowlist(false).build();
    assertFalse(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertFalse(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBidderFilterAllowlistSetTrueAndWhitelistSetFalse() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterAllowlist(true)
            .withBidderFilterWhitelist(false)
            .build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBidderFilterWhitelistSetTrueAndAllowlistSetFalse() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(true)
            .withBidderFilterAllowlist(false)
            .build();
    assertFalse(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertFalse(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBidderFilterWhitelistSetNull() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterWhitelist(null).build();
    assertNull(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertNull(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBidderFilterAllowlistSetNull() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder().withBidderFilterAllowlist(null).build();
    assertNull(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertNull(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBidderFilterWhitelistSetAndAllowlistNull() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(true)
            .withBidderFilterAllowlist(null)
            .build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBidderFilterAllowlistSetAndWhitelistNull() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterAllowlist(true)
            .withBidderFilterWhitelist(null)
            .build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBidderFilterAllowlistAndWhitelistSetTrue() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterAllowlist(true)
            .withBidderFilterWhitelist(true)
            .build();
    assertTrue(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertTrue(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBidderFilterWhitelistAndAllowlistSetFalse() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(false)
            .withBidderFilterAllowlist(false)
            .build();
    assertFalse(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertFalse(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBidderFilterWhitelistAndAllowlistSetNull() {
    PublisherRTBProfileDTO publisherRTBProfileDTO =
        PublisherRTBProfileDTO.newBuilder()
            .withBidderFilterWhitelist(null)
            .withBidderFilterAllowlist(null)
            .build();
    assertNull(publisherRTBProfileDTO.getBidderFilterWhitelist());
    assertNull(publisherRTBProfileDTO.getBidderFilterAllowlist());
  }
}
