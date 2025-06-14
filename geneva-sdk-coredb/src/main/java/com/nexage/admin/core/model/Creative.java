package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "as_creative")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Creative implements Serializable, Cloneable {

  /** */
  private static final long serialVersionUID = 1551116643899789044L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  private Long pid;

  @Column(name = "seller_id", nullable = false)
  private Long sellerId;

  @Column(name = "advertiser_id", nullable = false)
  private Long advertiserId;

  @Column(length = 100, nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @Column(name = "ad_type", nullable = false)
  @EqualsAndHashCode.Include
  @ToString.Include
  private CreativeAdType adType;

  @Column private Integer height;
  @Column private Integer width;

  @Column(name = "banner", length = 1024, nullable = true)
  private String banner;

  @Column(name = "mma_120x20", length = 1024, nullable = true)
  private String mma120x20;

  @Column(name = "mma_168x28", length = 1024, nullable = true)
  private String mma168x28;

  @Column(name = "mma_216x36", length = 1024, nullable = true)
  private String mma216x36;

  @Column(name = "mma_300x50", length = 1024, nullable = true)
  private String mma300x50;

  @Column(name = "mma_320x50", length = 1024, nullable = true)
  private String mma320x50;

  @Lob
  @Column(name = "banner_alt")
  private String bannerAlt;

  @Lob
  @Column(name = "ad_text")
  private String adText;

  @Column(name = "landing_url", length = 4096, nullable = true)
  private String landingUrl;

  @Column(name = "tracking_url", length = 4096, nullable = true)
  private String trackingUrl;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "lastUpdate", nullable = false)
  private java.util.Date lastUpdate;

  @Column(nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private CreativeStatus status;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "campaignCreativePk.creative")
  @JsonBackReference
  private Collection<CampaignCreative> campaignCreatives = new ArrayList<>();

  @Column(name = "template_id", nullable = true)
  private Long templateId;

  @Column(name = "custom_markup", nullable = true)
  private String customMarkup;

  public boolean hasThirdPartyMarkup() {
    return customMarkup != null;
  }

  @PrePersist
  @PreUpdate
  protected void updateLastUpdated() {
    lastUpdate = new Date();
  }

  /** Returns a shallow copy of this Creative. */
  @Override
  public Creative clone() throws CloneNotSupportedException {
    return (Creative) super.clone();
  }

  @AllArgsConstructor
  @Getter
  public enum CreativeAdType {
    TEXT_ONLY(0),
    MOBILE_WEB(1),
    SMARTPHONE(2, 320, 50),
    TABLET_BANNER(3, 728, 90),
    MEDIUM_RECTANGLE(4, 300, 250),
    CUSTOM(5);

    private final int externalValue;

    private final int width;

    private final int height;

    CreativeAdType(int externalValue) {
      this(externalValue, -1, -1);
    }

    public void setCreativeSize(Creative creative) {
      if (!CUSTOM.equals(this)) {
        creative.setHeight(height);
        creative.setWidth(width);
      }
    }
  }

  @AllArgsConstructor
  @Getter
  public enum CreativeStatus {
    NOT_DELETED(0),
    DELETED(1);

    private final int externalValue;
  }
}
