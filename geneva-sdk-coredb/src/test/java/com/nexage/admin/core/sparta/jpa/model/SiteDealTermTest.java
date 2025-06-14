package com.nexage.admin.core.sparta.jpa.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.model.Site;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SiteDealTermTest {
  @Test
  void shouldCreateCopyWhenSiteIsNull() {
    // given
    var original = new SiteDealTerm();
    original.setSitePid(1L);

    // when
    var copy = new SiteDealTerm(original);

    // then
    assertEquals(1L, copy.getSitePid());
  }

  @Test
  void shouldCreateCopyWhenSiteIsNotNull() {
    // given
    var original = new SiteDealTerm();
    var site = new Site();
    site.setPid(1L);
    original.setSite(site);
    original.setSitePid(2L);

    // when
    var copy = new SiteDealTerm(original);

    // then
    assertEquals(1L, copy.getSitePid());
  }

  @Test
  void shouldDeterminePssEqualityCorrectlyWhenValuesNotNull() {
    // given
    var siteDealTerm = new SiteDealTerm();
    siteDealTerm.setNexageRevenueShare(BigDecimal.valueOf(1L));
    siteDealTerm.setRtbFee(BigDecimal.valueOf(1L));

    // when & then
    assertTrue(siteDealTerm.equalsPss(BigDecimal.valueOf(1L), BigDecimal.valueOf(1L)));

    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(2L), BigDecimal.valueOf(2L)));
    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(1L), BigDecimal.valueOf(2L)));
    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(2L), BigDecimal.valueOf(1L)));
    assertFalse(siteDealTerm.equalsPss(null, null));
    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(1L), null));
    assertFalse(siteDealTerm.equalsPss(null, BigDecimal.valueOf(1L)));
  }

  @Test
  void shouldDeterminePssEqualityCorrectlyWhenValuesNull() {
    // given
    var siteDealTerm = new SiteDealTerm();

    // when & then
    assertTrue(siteDealTerm.equalsPss(null, null));

    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(1L), BigDecimal.valueOf(1L)));
    assertFalse(siteDealTerm.equalsPss(BigDecimal.valueOf(1L), null));
    assertFalse(siteDealTerm.equalsPss(null, BigDecimal.valueOf(1L)));
  }
}
