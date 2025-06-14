package com.nexage.app.dto.pub.self.serve;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tag.Owner;
import com.nexage.admin.core.pubselfserve.AdsourcePubSelfServeView.AdSourceType;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView;
import com.nexage.admin.core.pubselfserve.TagPubSelfServeView.EcpmProvision;
import com.nexage.app.dto.BasePubSelfServeMetricsDTO;
import com.nexage.app.util.RevenueUtils;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;

public class PubSelfServeTagMetricsDTO extends BasePubSelfServeMetricsDTO {

  private final long pid;
  private final String name;
  private final boolean exchangeTag;
  private final Double floor;
  private final AdSourceType type;
  private final String adsourceName;
  private final String adSourceLogoUrl;
  private final BigDecimal ecpmManual;
  private final boolean ecpmOverride;
  private final Status status;
  private final Owner owner;
  private final Integer level;
  private final TierType tierType;

  public PubSelfServeTagMetricsDTO(
      TagPubSelfServeView pubTag, Site site, Tag tag, String adSourceLogoBaseUrl) {
    this.pid = pubTag.getPid();
    this.name = pubTag.getName();
    this.level = pubTag.getLevel();
    this.exchangeTag = pubTag.isExchangeTag();
    this.floor = pubTag.getExchangeFloor();
    this.type = pubTag.getType();
    this.adsourceName = pubTag.getAdsource().getName();
    this.ecpmOverride = pubTag.getEcpmProvision().equals(EcpmProvision.Manual.name());
    this.ecpmManual =
        (this.ecpmOverride)
            ? RevenueUtils.calculatePublisherEcpm(site, tag, pubTag.getEcpmManual())
            : null;
    this.status = pubTag.getStatus();
    this.owner = pubTag.getOwner();
    this.tierType = TierType.fromInt(pubTag.getTierType());

    if (tag != null) {
      this.adSourceLogoUrl =
          (StringUtils.isNotBlank(adSourceLogoBaseUrl)
                  && StringUtils.isNotBlank(tag.getBuyerLogo()))
              ? adSourceLogoBaseUrl + tag.getBuyerLogo()
              : null;
    } else {
      this.adSourceLogoUrl = null;
    }
  }

  public void aggregate(PubSelfServeMetrics m) {
    this.requests += m.getCurrentOutboundReqs();
    this.served += m.getCurrentServed();
    this.delivered += m.getCurrentDelivered();
    this.clicks += m.getCurrentClicks();
    this.revenue += m.getCurrentRevenue();
    this.preRevenue += m.getPrevRevenue();
  }

  public long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public boolean isExchangeTag() {
    return exchangeTag;
  }

  public AdSourceType getType() {
    return type;
  }

  public Double getFloor() {
    return floor;
  }

  public String getAdsourceName() {
    return adsourceName;
  }

  public String getAdSourceLogoUrl() {
    return adSourceLogoUrl;
  }

  public BigDecimal getEcpmManual() {
    return ecpmManual;
  }

  public boolean getEcpmOverride() {
    return ecpmOverride;
  }

  public Status getStatus() {
    return status;
  }

  public Owner getOwner() {
    return owner;
  }

  public TierType getTierType() {
    return tierType;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Integer getLevel() {
    return level;
  }
}
