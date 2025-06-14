package com.nexage.app.dto.seller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RTBProfileDTOTest {

  @Test
  void shouldReturnNullWhenBiddersFilterWhitelistNotSet() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    assertNull(rtbProfileDTO.getBiddersFilterWhitelist());
    assertNull(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterAllowlistNotSet() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    assertNull(rtbProfileDTO.getBiddersFilterWhitelist());
    assertNull(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterWhitelistSetTrue() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterWhitelistSetFalse() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(false);
    assertFalse(rtbProfileDTO.getBiddersFilterWhitelist());
    assertFalse(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetTrue() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterAllowlistSetFalse() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfileDTO.getBiddersFilterWhitelist());
    assertFalse(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetTrueAndWhitelistSetFalse() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    rtbProfileDTO.setBiddersFilterWhitelist(false);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnFalseWhenBiddersFilterWhitelistSetTrueAndAllowlistSetFalse() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfileDTO.getBiddersFilterWhitelist());
    assertFalse(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterWhitelistSetNull() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(null);
    assertNull(rtbProfileDTO.getBiddersFilterWhitelist());
    assertNull(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnNullWhenBiddersFilterAllowlistSetNull() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(null);
    assertNull(rtbProfileDTO.getBiddersFilterWhitelist());
    assertNull(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterWhitelistSetAndAllowlistNull() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    rtbProfileDTO.setBiddersFilterAllowlist(null);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBiddersFilterAllowlistSetAndWhitelistNull() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    rtbProfileDTO.setBiddersFilterWhitelist(null);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterAllowlistAndWhitelistSetTrue() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterAllowlist(true);
    rtbProfileDTO.setBiddersFilterWhitelist(true);
    assertTrue(rtbProfileDTO.getBiddersFilterWhitelist());
    assertTrue(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterWhitelistAndAllowlistSetFalse() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(false);
    rtbProfileDTO.setBiddersFilterAllowlist(false);
    assertFalse(rtbProfileDTO.getBiddersFilterWhitelist());
    assertFalse(rtbProfileDTO.getBiddersFilterAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothBiddersFilterWhitelistAndAllowlistSetNull() {
    RTBProfileDTO rtbProfileDTO = new RTBProfileDTO();
    rtbProfileDTO.setBiddersFilterWhitelist(null);
    rtbProfileDTO.setBiddersFilterAllowlist(null);
    assertNull(rtbProfileDTO.getBiddersFilterWhitelist());
    assertNull(rtbProfileDTO.getBiddersFilterAllowlist());
  }
}
