package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TagViewTest {

  @Test
  void shouldReturnNullWhenFilterBiddersWhitelistNotSet() {
    TagView tagView = new TagView();
    assertNull(tagView.getFilterBiddersWhitelist());
    assertNull(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersAllowlistNotSet() {
    TagView tagView = new TagView();
    assertNull(tagView.getFilterBiddersWhitelist());
    assertNull(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersWhitelistSetTrue() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(true);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersWhitelistSetFalse() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(false);
    assertFalse(tagView.getFilterBiddersWhitelist());
    assertFalse(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetTrue() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(true);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersAllowlistSetFalse() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(false);
    assertFalse(tagView.getFilterBiddersWhitelist());
    assertFalse(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetTrueAndWhitelistSetFalse() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(true);
    tagView.setFilterBiddersWhitelist(false);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnFalseWhenFilterBiddersWhitelistSetTrueAndAllowlistSetFalse() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(true);
    tagView.setFilterBiddersAllowlist(false);
    assertFalse(tagView.getFilterBiddersWhitelist());
    assertFalse(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersWhitelistSetNull() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(null);
    assertNull(tagView.getFilterBiddersWhitelist());
    assertNull(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnNullWhenFilterBiddersAllowlistSetNull() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(null);
    assertNull(tagView.getFilterBiddersWhitelist());
    assertNull(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersWhitelistSetAndAllowlistNull() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(true);
    tagView.setFilterBiddersAllowlist(null);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenFilterBiddersAllowlistSetAndWhitelistNull() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(true);
    tagView.setFilterBiddersWhitelist(null);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersAllowlistAndWhitelistSetTrue() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersAllowlist(true);
    tagView.setFilterBiddersWhitelist(true);
    assertTrue(tagView.getFilterBiddersWhitelist());
    assertTrue(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersWhitelistAndAllowlistSetFalse() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(false);
    tagView.setFilterBiddersAllowlist(false);
    assertFalse(tagView.getFilterBiddersWhitelist());
    assertFalse(tagView.getFilterBiddersAllowlist());
  }

  @Test
  void shouldReturnTrueWhenBothFilterBiddersWhitelistAndAllowlistSetNull() {
    TagView tagView = new TagView();
    tagView.setFilterBiddersWhitelist(null);
    tagView.setFilterBiddersAllowlist(null);
    assertNull(tagView.getFilterBiddersWhitelist());
    assertNull(tagView.getFilterBiddersAllowlist());
  }
}
