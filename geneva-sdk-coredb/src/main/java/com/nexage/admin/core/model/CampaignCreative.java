package com.nexage.admin.core.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "as_campaign_creative")
@AssociationOverride(
    name = "campaignCreativePk.campaign",
    joinColumns = @JoinColumn(name = "campaign_id"))
@AssociationOverride(
    name = "campaignCreativePk.creative",
    joinColumns = @JoinColumn(name = "creative_id"))
@NamedQuery(
    name = "getCampaignCreativesForCampaignAndCreative",
    query =
        "SELECT cc FROM CampaignCreative cc WHERE cc.campaignCreativePk.campaign.pid = :campaignId and cc.campaignCreativePk.creative.pid = :creativeId")
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class CampaignCreative implements Serializable {

  private static final long serialVersionUID = 7940713574102650200L;

  @EmbeddedId @EqualsAndHashCode.Include @ToString.Include
  private CampaignCreativePk campaignCreativePk;

  @Column(name = "seller_id", nullable = false)
  private long sellerId;

  @Column(name = "advertiser_id", nullable = false)
  private long advertiserId;

  @Lob
  @Column(name = "markup", nullable = true)
  private String markup;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private java.util.Date lastUpdate;

  public CampaignCreative(Campaign campaign, Creative creative) {
    campaignCreativePk = new CampaignCreativePk(campaign, creative);
    sellerId = campaign.getSellerId();
    advertiserId = campaign.getAdvertiserId();
  }

  @PrePersist
  @PreUpdate
  protected void updateLastUpdated() {
    lastUpdate = new Date();
  }
}
