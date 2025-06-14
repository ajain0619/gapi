package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SellerDTO implements Serializable {

  private String id;
  private Long pid;
  @NotNull private Integer version;
  private String name;
  private String type;
  private String website;
  private String description;
  private Long contactUserPid;
  private Boolean reportingApiEnabled;
  private Boolean adServingEnabled;
  private Boolean dynamicBuyerRegistrationEnabled;
  private Boolean brxdBuyerIdEnabledOnBidRequest;
  private Boolean restrictDrillDown;
  private String reportingType;
  private Double directAdServingFee;
  private Double houseAdServingFee;
  private Double nonRemnantHouseAdCap;
  private Double houseAdOverageFee;
  private Boolean cpiTrackingEnabled;
  private String cpiConversionNoticeUrl;
  private Boolean rtbEnabled;
  private Boolean mediationEnabled;
  private Boolean rtbRevenueReportEnabled;
  private String salesforceId;
  private BigDecimal bidderAdServingFee;
  private String status;
  private String globalAliasName;
  private BigDecimal credit;
  private Boolean test;
  private Boolean selfServeAllowed;
  private Long regionId;
  private Boolean payoutEnabled;
  private Boolean defaultRtbProfilesEnabled;
  private Boolean thirdPartyFraudDetectionEnabled;
  private String dhReportingId;
  private Long defaultBuyerGroup;
  private String currency;
  private Long sellerSeatPid;
  private Set<PublisherEligibleBiddersDTO> eligibleBidderGroups;
  private Long revenueGroupPid;
}
