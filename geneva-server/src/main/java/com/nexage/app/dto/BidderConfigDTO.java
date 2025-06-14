package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexage.admin.core.bidder.support.validation.annotation.CountryLetterCodes;
import com.nexage.admin.core.enums.AdSizeFilter;
import com.nexage.admin.core.enums.BidderFormat;
import com.nexage.admin.core.enums.BillingSource;
import com.nexage.admin.core.enums.BlockListInclusion;
import com.nexage.admin.core.enums.BuyerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.ImpressionFilter;
import com.nexage.admin.core.enums.UserIdBidRequestType;
import com.nexage.admin.core.enums.UserIdPreference;
import com.nexage.admin.core.enums.VerificationType;
import com.nexage.app.util.validator.NativeVersionConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Data transfer representation of {@link com.nexage.admin.core.model.BidderConfig}. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidderConfigDTO implements Serializable {

  private static final long serialVersionUID = 5027325982670996935L;

  @EqualsAndHashCode.Include @ToString.Include private String id;

  @EqualsAndHashCode.Include @ToString.Include private Long pid;

  private Integer version;

  @NotNull
  @Min(0)
  private BigDecimal bidRequestCpm;

  private String bidRequestUrl;

  private String defaultBidCurrency;

  private int defaultBidUnit;

  private int auctionTypeFilter;

  private String categoriesFilter;

  private boolean categoriesFilterMode;

  @CountryLetterCodes private String countryFilter;

  private boolean countryFilterMode;

  private String devicesFilter;

  private boolean devicesFilterMode;

  @Size(max = 10000)
  private String publishersFilter;

  private boolean publishersFilterMode;

  @Size(max = 1000)
  private String sitesFilter;

  private boolean sitesFilterMode;

  @Size(max = 1000)
  private Set<AdSizeFilter> adSizesFilter;

  private boolean adSizesFilterMode;

  @NotNull
  @Min(-1)
  private Integer requestRateFilter;

  private boolean scriptAllowedFilter;

  private BlockListInclusion includeLists;

  private String noticeUrl;

  private boolean adScreeningEnabled;

  @NotNull private BidderFormat formatType;

  private boolean trafficStatus;

  private Long companyPid;

  private Set<BidderSubscriptionInfoDTO> subscriptions;

  private boolean locationEnabledOnly;

  private boolean deviceIdentifiedOnly;

  @NotNull private Set<ExchangeRegionalDTO> exchangeRegionals;

  @ToString.Include private String name;

  private String allowedTraffic;

  @NotNull @Valid private Set<BidderDeviceTypeDTO> allowedDeviceTypes;

  @NotNull private VerificationType verificationType;

  @NotNull @Valid private Set<BidderRegionLimitDTO> regionLimits;

  @NativeVersionConstraint private String nativeVersion;

  private Set<ImpressionFilter> imprFilter;

  private UserIdPreference userIdPreference;

  private BillingSource billingSource;

  private Boolean headerBiddingEnabled;

  private Long uumpGlobalBidderPid;

  private Boolean domainFilterAllowList;

  private Boolean appBundleFilterAllowList;

  @NotNull private Boolean domainFilterAllowUnknownUrls;

  private Boolean appBundleFilterAllowUnknownApps;

  @NotNull private Boolean allowBridgeIdMatch;

  @NotNull private Boolean allowConnectId = false;

  @NotNull private Boolean allowIdGraphMatch;

  @NotNull private Boolean allowLiveramp;

  @NotNull private BuyerDomainVerificationAuthLevel domainVerificationAuthLevel;

  @NotNull private Set<BidderConfigDenyAllowFilterListDTO> bidderConfigDenyAllowFilterLists;

  @NotNull private Boolean sendDealSizes;

  private UserIdBidRequestType userIdBidRequestType;

  private String allowedContentEncoding;

  private Set<Long> identityProviders;

  private Boolean smartQpsOutboundEnabled;

  private Double throttleRate;
}
