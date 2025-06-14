package com.nexage.app.dto;

import com.nexage.admin.core.bidder.type.BDRMraidCompliance;
import com.nexage.admin.core.bidder.type.BDRStatus;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BdrCreativeDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @EqualsAndHashCode.Include private Long pid;

  @EqualsAndHashCode.Include private String externalId;

  @EqualsAndHashCode.Include private BdrAdvertiserDTO advertiser;

  @EqualsAndHashCode.Include private String name;

  @EqualsAndHashCode.Include private BDRStatus status = BDRStatus.ACTIVE;

  @EqualsAndHashCode.Include private String bannerURL;

  @EqualsAndHashCode.Include private Integer width;

  @EqualsAndHashCode.Include private Integer height;

  @EqualsAndHashCode.Include private String customMarkup;

  @EqualsAndHashCode.Include private String landingURL;

  @EqualsAndHashCode.Include private String trackingURL;

  @EqualsAndHashCode.Include private String indicativeURL;

  @EqualsAndHashCode.Include private BDRMraidCompliance mraidCompliance = BDRMraidCompliance.NONE;

  @EqualsAndHashCode.Include private Integer version;

  @EqualsAndHashCode.Include private String nexageBannerUrl;
}
