package com.nexage.app.dto.seller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.json.MapSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RTBProfileDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private String id;
  private Long pid;
  private Integer version;
  private String description;
  private String name;
  private Long siteAlias;
  private String siteNameAlias;
  private char siteType;
  private Long pubAlias;
  private String pubNameAlias;
  private int auctionType;
  private String blockedAdCategories;
  private String blockedAdTypes;
  private String blockedAdvertisers;
  private String blockedAttributes;
  private BigDecimal defaultReserve;
  private BigDecimal pubNetReserve;
  private boolean includeConsumerId;
  private boolean includeConsumerProfile;
  private boolean includeDomainReferences;
  private Integer includeSiteName;
  private BigDecimal lowReserve;
  private BigDecimal pubNetLowReserve;
  /** @deprecated use the inclusive term {@link #biddersFilterAllowlist} instead. */
  @Deprecated(forRemoval = true)
  private Boolean biddersFilterWhitelist;

  private Boolean biddersFilterAllowlist;
  private java.util.Date creationDate;
  private java.util.Date lastUpdate;
  private Long sitePid;

  @JsonSerialize(using = MapSerializer.class)
  private Map<Long, String> blockedExternalDataProviderMap = new HashMap<>();

  @JsonSerialize(using = MapSerializer.class)
  private Map<Long, String> bidderFilterMap = new HashMap<>();

  private Integer includePubName;
  private Long defaultRtbProfileOwnerCompanyPid;
  private Status status = Status.ACTIVE;
  private boolean includeGeoData = true;
  private boolean useDefaultBlock = true;
  private boolean useDefaultBidders = true;
  private String alterReserve;

  /** @deprecated use the inclusive term {@link #biddersFilterAllowlist} instead. */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public Boolean getBiddersFilterWhitelist() {
    return biddersFilterAllowlist != null ? biddersFilterAllowlist : biddersFilterWhitelist;
  }

  /** @deprecated use the inclusive term {@link #biddersFilterAllowlist} instead. */
  @Deprecated(since = "SSP-17992", forRemoval = true)
  public void setBiddersFilterWhitelist(Boolean biddersFilterWhitelist) {
    if (this.biddersFilterAllowlist != null) {
      this.biddersFilterWhitelist = this.biddersFilterAllowlist;
    } else if (biddersFilterWhitelist != null) {
      this.biddersFilterWhitelist = biddersFilterWhitelist;
      this.biddersFilterAllowlist = biddersFilterWhitelist;
    }
  }

  public void setBiddersFilterAllowlist(Boolean biddersFilterAllowlist) {
    // To give precedence to allowlist over whitelist
    if ((this.biddersFilterAllowlist == null
            || this.biddersFilterWhitelist == null
            || !this.biddersFilterAllowlist.equals(biddersFilterAllowlist))
        && biddersFilterAllowlist != null) {
      this.biddersFilterWhitelist = biddersFilterAllowlist;
      this.biddersFilterAllowlist = biddersFilterAllowlist;
    }
  }

  public Boolean getBiddersFilterAllowlist() {
    return biddersFilterAllowlist != null ? biddersFilterAllowlist : biddersFilterWhitelist;
  }
}
