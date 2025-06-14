package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.enums.CrsReviewStatusBlock;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.json.BigDecimalSerializer;
import com.nexage.admin.core.sparta.jpa.model.SellerType;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.util.validator.publisherattributes.PublisherAttributesConstraint;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.BooleanUtils;

@JsonInclude(Include.NON_NULL)
@PublisherAttributesConstraint
@Builder(builderClassName = "Builder", setterPrefix = "with", builderMethodName = "newBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class PublisherAttributes {

  private int version;
  private Set<Long> defaultBlock;
  private Set<Long> individualsDefaultBlock;
  private Set<Long> defaultBidderGroups;
  private Set<Long> individualsDefaultBidderGroups;
  private boolean defaultBiddersAllowList;
  private TransparencySettingsDTO defaultTransparencySettings;
  private TransparencyMgmtEnablement defaultTransparencyMgmtEnablement;
  private CrsReviewStatusBlock reviewStatusBlock;
  private PublisherDefaultRTBProfileDTO defaultRtbProfile;
  private CrsSecureStatusBlock secureStatusBlock;
  // additional fields
  private Date effectiveDate;

  private Integer publisherDataProtectionRole;

  private Boolean videoUseInboundSiteOrApp;

  @JsonSerialize(using = BigDecimalSerializer.class)
  @DecimalMax("99999999.99999999")
  private BigDecimal revenueShare;

  @JsonSerialize(using = BigDecimalSerializer.class)
  @DecimalMax("99999999.99999999")
  private BigDecimal rtbFee;

  private Boolean hbThrottleEnabled;

  @Max(100)
  @Min(0)
  private Integer hbThrottlePercentage;

  private Boolean pfoEnabled;

  @Max(32767)
  @Min(-32768)
  private Integer siteLimit;

  @Max(32767)
  @Min(-32768)
  private Integer positionsPerSiteLimit;

  @Max(32767)
  @Min(-32768)
  private Integer tagsPerPositionLimit;

  @Max(32767)
  @Min(-32768)
  private Integer campaignsLimit;

  @Max(32767)
  @Min(-32768)
  private Integer creativesPerCampaignLimit;

  @Max(99999)
  @Min(0)
  private Integer bidderLibrariesLimit;

  @Max(99999)
  @Min(0)
  private Integer blockLibrariesLimit;

  @Max(32767)
  @Min(-32768)
  private Integer userLimit;

  private Boolean limitEnabled;

  @Max(65535)
  private Integer hbPricePreference;

  private Boolean superAuctionEnabled;

  private Long defaultRTBProfileId;

  @Getter(value = AccessLevel.NONE)
  private Boolean adFeedbackOptOut;

  @Getter(value = AccessLevel.NONE)
  private Boolean buyerTransparencyOptOut;

  private Long revenueGroupPid;

  private SellerType sellerType;

  private SellerDomainVerificationAuthLevel sellerDomainVerificationAuthLevel;

  private Boolean humanOptOut = false;

  private Boolean smartQPSEnabled = false;

  private Float externalAdVerificationSamplingRate;

  private Boolean dynamicFloorEnabled = false;

  private Boolean adStrictApproval;

  @Valid private SmartExchangeAttributesDTO smartExchangeAttributes;

  private String externalAdVerificationPolicyKey;

  @JsonSerialize(using = BigDecimalSerializer.class)
  @DecimalMax("1.00")
  private BigDecimal sspDealRevShare;

  @JsonSerialize(using = BigDecimalSerializer.class)
  @DecimalMax("1.00")
  private BigDecimal jointDealRevShare;

  @JsonSerialize(using = BigDecimalSerializer.class)
  @DecimalMax("1.00")
  private BigDecimal sellerDealRevShare;

  private boolean enableCtvSelling;

  private boolean creativeSuccessRateThresholdOptOut = false;

  @DecimalMin(value = "0.0", inclusive = false)
  @DecimalMax(value = "100.0")
  @Digits(integer = 3, fraction = 2)
  private BigDecimal creativeSuccessRateThreshold;

  private Boolean rawResponse;

  @Min(value = 0)
  @Max(value = 100)
  @Digits(integer = 3, fraction = 0)
  private Integer humanPrebidSampleRate;

  @Min(value = 0)
  @Max(value = 100)
  @Digits(integer = 3, fraction = 0)
  private Integer humanPostbidSampleRate;

  private boolean customDealFloorEnabled;

  public Boolean getAdFeedbackOptOut() {
    return BooleanUtils.toBoolean(adFeedbackOptOut);
  }

  public Boolean getBuyerTransparencyOptOut() {
    return BooleanUtils.toBoolean(buyerTransparencyOptOut);
  }

  public Boolean getDynamicFloorEnabled() {
    return BooleanUtils.toBoolean(dynamicFloorEnabled);
  }
}
