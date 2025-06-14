package com.nexage.app.util;

import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm.RevenueMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RevenueUtils {

  private static final int EcpmPrecision = 4;
  private static final int MoneyPrecision = 2;

  /** Calculate the Nexage Run-Time ECPM based on the ECPM provided by the publisher */
  public static BigDecimal calculateNexageEcpm(Site site, Tag tag, Double publisherEcpm) {
    return calculateNexageEcpm(site, tag, BigDecimal.valueOf(publisherEcpm));
  }

  public static BigDecimal calculateNexageNetRevenue(Site site, Tag tag, Double publisherEcpm) {
    return calculateNexageNetRevenue(site, tag, BigDecimal.valueOf(publisherEcpm));
  }

  public static BigDecimal calculateNexageEcpm(Site site, Tag tag, BigDecimal publisherEcpm) {
    DealTermValue dealTerm = getCurrentDealTerm(site, tag);
    return calculateNexageNetRevenue(
        dealTerm.getRevenueMode(),
        dealTerm.getRevShare(),
        dealTerm.getRTBFee(),
        publisherEcpm,
        EcpmPrecision);
  }

  public static BigDecimal calculateNexageNetRevenue(
      Site site, Tag tag, BigDecimal publisherValue) {
    DealTermValue dealTerm = getCurrentDealTerm(site, tag);
    return calculateNexageNetRevenue(
        dealTerm.getRevenueMode(),
        dealTerm.getRevShare(),
        dealTerm.getRTBFee(),
        publisherValue,
        MoneyPrecision);
  }

  public static BigDecimal calculateRevShares(Site site, Tag tag, BigDecimal publisherValue) {
    SiteDealTerm siteDealTerm = site.getCurrentDealTerm();
    site.setCurrentDealTerm(siteDealTerm);
    DealTermValue siteDealTermValue = convert(siteDealTerm);
    DealTermValue tagDealTerm = convert(tag.getCurrentDealTerm());
    DealTermValue dealTerm =
        applyTagOverride(siteDealTermValue, tagDealTerm, isExchangeTag(site, tag));
    return calculateNexageNetRevenue(
        dealTerm.getRevenueMode(),
        dealTerm.getRevShare(),
        dealTerm.getRTBFee(),
        publisherValue,
        MoneyPrecision);
  }

  private static boolean isExchangeTag(Site site, Tag tag) {
    for (RTBProfile tempRtb : site.getRtbProfiles()) {
      if (tag.getPrimaryId() != null
          && tempRtb.getExchangeSiteTagId() != null
          && tag.getPrimaryId().equals(tempRtb.getExchangeSiteTagId())) {
        return true;
      }
    }

    return !site.getTags().contains(tag) || tag.isExchangeTag();
  }

  protected static BigDecimal calculateNexageNetRevenue(
      RevenueMode revenueMode,
      BigDecimal revenueShare,
      BigDecimal rtbFee,
      BigDecimal publisherValue,
      int precision) {
    BigDecimal nexageEcpm = BigDecimal.ZERO;
    BigDecimal exchangeFee = rtbFee == null ? BigDecimal.ZERO : rtbFee;
    BigDecimal revShare = revenueShare == null ? BigDecimal.ZERO : revenueShare;
    BigDecimal pubValue = publisherValue.setScale(precision, RoundingMode.HALF_UP);

    switch (revenueMode) {
      case FLAT:
        nexageEcpm = revShare.add(exchangeFee).add(pubValue);
        break;
      case REV_SHARE:
        nexageEcpm =
            BigDecimal.ZERO.compareTo(publisherValue) == 0
                ? BigDecimal.ZERO
                : pubValue.divide(
                    BigDecimal.ONE.subtract(revShare.add(exchangeFee)), RoundingMode.HALF_EVEN);
        break;
    }

    nexageEcpm = nexageEcpm.setScale(precision, RoundingMode.HALF_UP);

    return nexageEcpm;
  }

  /** Calculate the Publisher ECPM based on the Nexage Run-Time ECPM */
  public static BigDecimal calculatePublisherEcpm(Site site, Tag tag, Double nexageEcpm) {
    return calculatePublisherEcpm(site, tag, BigDecimal.valueOf(nexageEcpm));
  }

  public static BigDecimal calculatePublisherNetRevenue(Site site, Tag tag, Double nexageValue) {
    return calculatePublisherNetRevenue(site, tag, BigDecimal.valueOf(nexageValue));
  }

  public static BigDecimal calculatePublisherEcpm(Site site, Tag tag, BigDecimal nexageEcpm) {
    DealTermValue dealTerm = getCurrentDealTerm(site, tag);
    return calculatePublisherNetRevenue(
        dealTerm.getRevenueMode(),
        dealTerm.getRevShare(),
        dealTerm.getRTBFee(),
        nexageEcpm,
        EcpmPrecision);
  }

  public static BigDecimal calculatePublisherNetRevenue(
      Site site, Tag tag, BigDecimal nexageValue) {
    DealTermValue dealTerm = getCurrentDealTerm(site, tag);
    return calculatePublisherNetRevenue(
        dealTerm.getRevenueMode(),
        dealTerm.getRevShare(),
        dealTerm.getRTBFee(),
        nexageValue,
        MoneyPrecision);
  }

  protected static BigDecimal calculatePublisherNetRevenue(
      RevenueMode revenueMode,
      BigDecimal revenueShare,
      BigDecimal rtbFee,
      BigDecimal nexageValue,
      int precision) {
    BigDecimal publisherEcpm = BigDecimal.ZERO;
    BigDecimal exchangeFee = rtbFee == null ? BigDecimal.ZERO : rtbFee;
    BigDecimal revShare = revenueShare == null ? BigDecimal.ZERO : revenueShare;
    BigDecimal nexValue = nexageValue.setScale(precision, RoundingMode.HALF_UP);

    switch (revenueMode) {
      case FLAT:
        publisherEcpm = nexValue.subtract(revShare.add(exchangeFee));
        break;
      case REV_SHARE:
        publisherEcpm =
            BigDecimal.ZERO.compareTo(nexageValue) == 0
                ? BigDecimal.ZERO
                : nexValue.multiply(BigDecimal.ONE.subtract(revShare.add(exchangeFee)));
        break;
    }
    publisherEcpm = publisherEcpm.setScale(precision, RoundingMode.HALF_UP);
    return publisherEcpm;
  }

  private static DealTermValue getCurrentDealTerm(Site site, Tag tag) {
    SiteDealTerm siteDealTerm = site.getCurrentDealTerm();
    site.setCurrentDealTerm(siteDealTerm);
    DealTermValue siteDealTermValue = convert(siteDealTerm);
    DealTermValue tagDealTerm = convert(tag.getCurrentDealTerm());
    DealTermValue dealTerm = applyTagOverride(siteDealTermValue, tagDealTerm, tag.isExchangeTag());
    return dealTerm;
  }

  private static DealTermValue applyTagOverride(
      DealTermValue siteValue, DealTermValue tagValue, boolean isExchangeTag) {

    BigDecimal revShare = null;
    BigDecimal rtbFee = null;
    RevenueMode revenueMode = null;
    if (null != tagValue) {
      revShare = tagValue.getRevShare();
      if (isExchangeTag) {
        rtbFee = tagValue.getRTBFee();
      } else {
        rtbFee = BigDecimal.ZERO;
      }
      revenueMode = tagValue.getRevenueMode();
    }
    if (null != siteValue) {
      if (null == revShare) {
        revShare = siteValue.getRevShare();
      }
      if (null == rtbFee && isExchangeTag) {
        rtbFee = siteValue.getRTBFee();
      }
      if (null == revenueMode) {
        revenueMode = siteValue.getRevenueMode();
      }
      // Question: do we need to worry about the case where the effective date of the site dt is
      // later than the tag dt (later site overrides tag override ?)
      // Answer: No, tag override supercedes, regardless of date of site deal term (see, for
      // instance ExWinsAndInboundJob).
    }

    if (null == revShare) {
      revShare = BigDecimal.ZERO;
    }
    if (null == rtbFee) {
      rtbFee = BigDecimal.ZERO;
    }

    return new DealTermValue(revenueMode, revShare, rtbFee);
  }

  private static DealTermValue convert(SiteDealTerm dealTerm) {
    if (null == dealTerm) {
      return null;
    } else {
      return new DealTermValue(
          dealTerm.getRevenueMode(), dealTerm.getNexageRevenueShare(), dealTerm.getRtbFee());
    }
  }

  private static class DealTermValue {
    private BigDecimal revShare;
    private BigDecimal rtbFee;
    RevenueMode revenueMode;

    public DealTermValue(RevenueMode revenueMode, BigDecimal revShare, BigDecimal rtbFee) {
      this.revenueMode = revenueMode;
      this.revShare = revShare;
      this.rtbFee = rtbFee;
    }

    public BigDecimal getRTBFee() {
      return rtbFee;
    }

    public BigDecimal getRevShare() {
      return revShare;
    }

    public RevenueMode getRevenueMode() {
      return revenueMode;
    }
  }
}
