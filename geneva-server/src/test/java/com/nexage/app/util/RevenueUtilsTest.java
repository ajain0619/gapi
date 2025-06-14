package com.nexage.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm.RevenueMode;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RevenueUtilsTest {

  @Test
  void shouldCalculateNexageEcpm() {
    BigDecimal nexageEcpm = BigDecimal.ZERO;

    nexageEcpm =
        RevenueUtils.calculateNexageNetRevenue(
            RevenueMode.REV_SHARE,
            BigDecimal.valueOf(0.25D),
            BigDecimal.valueOf(0.0D),
            BigDecimal.valueOf(2.0D),
            4);
    assertEquals(
        0,
        nexageEcpm.compareTo(BigDecimal.valueOf(2.6667D)),
        "nexageEcpm not calculated properly: " + nexageEcpm);

    nexageEcpm =
        RevenueUtils.calculateNexageNetRevenue(
            RevenueMode.REV_SHARE,
            BigDecimal.valueOf(0.25D),
            BigDecimal.valueOf(0.25D),
            BigDecimal.valueOf(2.0D),
            4);
    assertEquals(
        0,
        nexageEcpm.compareTo(BigDecimal.valueOf(4.0000D)),
        "nexageEcpm not calculated properly " + nexageEcpm);
  }

  @Test
  void shouldCalculatePublisherEcpm() {
    BigDecimal publisherEcpm = BigDecimal.ZERO;

    publisherEcpm =
        RevenueUtils.calculatePublisherNetRevenue(
            RevenueMode.REV_SHARE,
            BigDecimal.valueOf(0.25D),
            BigDecimal.valueOf(0.25D),
            BigDecimal.valueOf(3.0D),
            4);
    assertEquals(
        0,
        publisherEcpm.compareTo(BigDecimal.valueOf(1.5D)),
        "publisherEcpm not calculated properly " + publisherEcpm);

    publisherEcpm =
        RevenueUtils.calculatePublisherNetRevenue(
            RevenueMode.REV_SHARE,
            BigDecimal.valueOf(0.25D),
            BigDecimal.ZERO,
            BigDecimal.valueOf(3.0D),
            4);
    assertEquals(
        0,
        publisherEcpm.compareTo(BigDecimal.valueOf(2.25D)),
        "publisherEcpm not calculated properly " + publisherEcpm);

    publisherEcpm =
        RevenueUtils.calculatePublisherNetRevenue(
            RevenueMode.REV_SHARE,
            BigDecimal.valueOf(0.25D),
            BigDecimal.ZERO,
            BigDecimal.valueOf(2.5D),
            4);
    assertEquals(
        0,
        publisherEcpm.compareTo(BigDecimal.valueOf(1.875D)),
        "publisherEcpm not calculated properly " + publisherEcpm);
  }

  @Test
  void shouldCalculateFloorsCorrectly() {

    BigDecimal publisherDesiredFloor = new BigDecimal("1.00");
    BigDecimal expectedNexageFloor = new BigDecimal("1.33");
    BigDecimal revShare = new BigDecimal("0.20");
    BigDecimal rtbFee = new BigDecimal("0.05");

    // rtb floor test
    BigDecimal nexageFloor =
        RevenueUtils.calculateNexageNetRevenue(
            RevenueMode.REV_SHARE, revShare, rtbFee, publisherDesiredFloor, 2);
    // nexageEcpm = nexageEcpm.setScale(2, RoundingMode.HALF_UP);
    assertEquals(
        0,
        expectedNexageFloor.compareTo(nexageFloor),
        "nexage floor calculation is wrong: " + nexageFloor);

    BigDecimal publisherFloor =
        RevenueUtils.calculatePublisherNetRevenue(
            RevenueMode.REV_SHARE, revShare, rtbFee, nexageFloor, 2);
    // publisherEcpm = publisherEcpm.setScale(2, RoundingMode.HALF_UP);
    assertEquals(
        0,
        publisherDesiredFloor.compareTo(publisherFloor),
        "publisher floor calculation is wrong: " + publisherFloor);
  }

  @Test
  void shouldCalculateEcpmCorrectly() {

    BigDecimal publisherDesiredEcpm = new BigDecimal("1.00");
    BigDecimal expectedNexageEcpm = new BigDecimal("1.3333");
    BigDecimal revShare = new BigDecimal("0.20");
    BigDecimal rtbFee = new BigDecimal("0.05");

    // ecpm test
    BigDecimal nexageEcpm =
        RevenueUtils.calculateNexageNetRevenue(
            RevenueMode.REV_SHARE, revShare, rtbFee, publisherDesiredEcpm, 4);
    assertEquals(
        0,
        expectedNexageEcpm.compareTo(nexageEcpm),
        "nexage ecpm calculation is wrong: " + nexageEcpm);

    BigDecimal publisherEcpm =
        RevenueUtils.calculatePublisherNetRevenue(
            RevenueMode.REV_SHARE, revShare, rtbFee, nexageEcpm, 4);
    // publisherEcpm = publisherEcpm.setScale(2, RoundingMode.HALF_UP);
    assertEquals(
        0,
        publisherDesiredEcpm.compareTo(publisherEcpm),
        "publisher ecpm calculation is wrong: " + publisherEcpm);
  }

  @Test
  void shouldReturnCorrectRevenueShareNullTagId() {
    // Given
    Site site = new Site();
    site.setRtbProfiles(Set.of(new RTBProfile()));
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setRevenueMode(RevenueMode.REV_SHARE);
    siteDealTerm.setNexageRevenueShare(new BigDecimal(10));
    siteDealTerm.setRtbFee(new BigDecimal(100));
    site.setCurrentDealTerm(siteDealTerm);

    // When
    BigDecimal result = RevenueUtils.calculateRevShares(site, new Tag(), new BigDecimal(1000));

    // Then
    assertEquals(BigDecimal.valueOf(-9.17), result);
  }

  @Test
  void shouldReturnCorrectRevenueShare() {
    // Given
    Site site = new Site();
    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setExchangeSiteTagId("ABC");
    site.setRtbProfiles(Set.of(rtbProfile));
    SiteDealTerm siteDealTerm = new SiteDealTerm();
    siteDealTerm.setRevenueMode(RevenueMode.REV_SHARE);
    siteDealTerm.setNexageRevenueShare(new BigDecimal(10));
    siteDealTerm.setRtbFee(new BigDecimal(100));
    site.setCurrentDealTerm(siteDealTerm);
    Tag tag = new Tag();
    tag.setPrimaryId("ABC");

    // When
    BigDecimal result = RevenueUtils.calculateRevShares(site, tag, new BigDecimal(1000));

    // Then
    assertEquals(BigDecimal.valueOf(-9.17), result);
  }
}
