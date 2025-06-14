package com.nexage.admin.core.pubselfserve;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag.Owner;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView.AdSourceType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.hibernate.envers.NotAudited;

@Table(name = "tag")
@Immutable
@Entity
public class TagPubSelfServeView implements Serializable {

  @Column @Id private long pid;

  @Column(name = "site_pid", insertable = false, updatable = false)
  @NotAudited
  private Long sitePid;

  @ManyToOne
  @JoinColumn(name = "site_pid", referencedColumnName = "pid")
  @JsonIgnore
  @JsonBackReference
  private Site site;

  @Column private String name;

  @ManyToOne
  @JoinColumn(name = "buyer_pid")
  private AdsourcePubSelfServeView adsource;

  @ManyToOne
  @JoinColumn(name = "position_pid")
  private PositionPubSelfServeView position;

  @Formula(
      "(SELECT concat(if(count(1) = 0, 0, 1), ',', rtb.default_reserve) FROM exchange_site_tag rtb WHERE rtb.tag_id=primary_id)")
  @Type(type = "com.nexage.admin.core.pubselfserve.ExchangeTagUserType")
  private ExchangeTag exchangeTag;

  @Formula(
      "(select concat(coalesce(dt.nexage_rev_share, 'x'), ',', coalesce(dt.rtb_fee,'x')) from deal_term dt where dt.site_pid=site_pid and dt.tag_pid is null order by dt.pid desc limit 1)")
  @Type(type = "com.nexage.admin.core.pubselfserve.DealTermUserType")
  private DealTerm siteDealTerm;

  @Formula(
      "(select concat(coalesce(tdt.nexage_rev_share, 'x'), ',', coalesce(tdt.rtb_fee,'x')) from deal_term tdt where tdt.site_pid=site_pid and tdt.tag_pid=pid order by tdt.pid desc limit 1)")
  @Type(type = "com.nexage.admin.core.pubselfserve.DealTermUserType")
  private DealTerm tagDealTerm;

  @Formula(
      "(SELECT t.level FROM tier t JOIN tier_tag tt ON tt.tier_pid = t.pid WHERE tt.tag_pid = pid order by t.level limit 1)")
  private Integer level;

  @Column(name = "ecpm_provision")
  private String ecpmProvision;

  @Column(name = "ecpm_manual")
  private BigDecimal ecpmManual;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private Owner owner;

  @Column(name = "status", nullable = false)
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  private Status status;

  @Formula(
      "(SELECT t.tier_type FROM tier t JOIN tier_tag tt ON tt.tier_pid = t.pid WHERE tt.tag_pid = pid limit 1)")
  private Integer tierType;

  public Integer getTierType() {
    return tierType;
  }

  public long getPid() {
    return pid;
  }

  public long getSitePid() {
    return sitePid;
  }

  public String getName() {
    return name;
  }

  public AdsourcePubSelfServeView getAdsource() {
    return adsource;
  }

  public boolean isExchangeTag() {
    return exchangeTag != null ? exchangeTag.exchangeTag : false;
  }

  public Site getSite() {
    return site;
  }

  public String getEcpmProvision() {
    return ecpmProvision;
  }

  public BigDecimal getEcpmManual() {
    return ecpmManual;
  }

  public Double getExchangeFloor() {
    if (!isExchangeTag()) {
      return null;
    }
    DealTerm dealTerm = applyTagOverride(this.siteDealTerm, this.tagDealTerm);
    if (null == dealTerm) {
      return exchangeTag.exchangeFloor;
    }

    BigDecimal floor = BigDecimal.valueOf(exchangeTag.exchangeFloor);
    BigDecimal revShare = dealTerm.getRevShare().add(dealTerm.getRtbFee());
    BigDecimal pubFloor = floor.multiply(BigDecimal.ONE.subtract(revShare));

    return pubFloor.setScale(2, RoundingMode.HALF_UP).doubleValue();
  }

  public Integer getLevel() {
    return level;
  }

  private DealTerm applyTagOverride(DealTerm siteValue, DealTerm tagValue) {

    BigDecimal revShare = null;
    BigDecimal rtbFee = null;
    if (null != tagValue) {
      revShare = tagValue.getRevShare();
      if (isExchangeTag()) {
        rtbFee = tagValue.getRtbFee();
      } else {
        rtbFee = BigDecimal.ZERO;
      }
    }
    if (null != siteValue) {
      if (null == revShare) {
        revShare = siteValue.getRevShare();
      }
      if (null == rtbFee && isExchangeTag()) {
        rtbFee = siteValue.getRtbFee();
      }
    }

    if (null == revShare) {
      revShare = BigDecimal.ZERO;
    }
    if (null == rtbFee) {
      rtbFee = BigDecimal.ZERO;
    }

    return new DealTerm(revShare, rtbFee);
  }

  public Status getStatus() {
    return status;
  }

  public Owner getOwner() {
    return owner;
  }

  public AdSourceType getType() {
    return exchangeTag != null ? AdSourceType.RTB : AdSourceType.Mediation;
  }

  static final class ExchangeTag implements Serializable {
    private final boolean exchangeTag;
    private final double exchangeFloor;

    public ExchangeTag(boolean exchangeTag, double rtbFloor) {
      this.exchangeTag = exchangeTag;
      this.exchangeFloor = rtbFloor;
    }

    public boolean isExchangeTag() {
      return exchangeTag;
    }

    public double getExchangeFloor() {
      return exchangeFloor;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (exchangeTag ? 1231 : 1237);
      long temp;
      temp = Double.doubleToLongBits(exchangeFloor);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ExchangeTag other = (ExchangeTag) obj;
      if (exchangeTag != other.exchangeTag) return false;
      if (Double.doubleToLongBits(exchangeFloor) != Double.doubleToLongBits(other.exchangeFloor))
        return false;
      return true;
    }
  }

  public enum EcpmProvision {
    Manual,
    Auto
  }
}
