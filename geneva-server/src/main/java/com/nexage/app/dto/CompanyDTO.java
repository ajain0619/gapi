package com.nexage.app.dto;

import com.nexage.app.dto.seller.SellerAttributesDTO;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CompanyDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Include private String id;
  @ToString.Include private Long pid;
  @ToString.Include private Integer version;
  @EqualsAndHashCode.Include @ToString.Include private String name;
  @EqualsAndHashCode.Include @ToString.Include private CompanyType type;
  @EqualsAndHashCode.Include @ToString.Include private String website;
  private String description;
  private Long contactUserPid;
  private boolean adServingEnabled;
  private boolean dynamicBuyerRegistrationEnabled;
  private boolean brxdBuyerIdEnabledOnBidRequest;
  private boolean restrictDrillDown;
  private Double directAdServingFee;
  private Double houseAdServingFee;
  private Double nonRemnantHouseAdCap;
  private Double houseAdOverageFee;
  private boolean cpiTrackingEnabled;
  private String cpiConversionNoticeUrl;
  private boolean rtbEnabled;
  private boolean mediationEnabled;
  private boolean rtbRevenueReportEnabled;
  private String salesforceId;
  private BigDecimal bidderAdServingFee;
  private com.nexage.admin.core.enums.Status status;
  private String globalAliasName;
  private boolean disableAdFeedback;
  private boolean test;
  private boolean selfServeAllowed;
  private SellerAttributesDTO sellerAttributes;
  private Long regionId;
  private Boolean payoutEnabled;
  private boolean thirdPartyFraudDetectionEnabled;
  private boolean defaultRtbProfilesEnabled;
  private String dhReportingId;
  private Long defaultBuyerGroup;
  @EqualsAndHashCode.Include @ToString.Include private String currency;
  private Long sellerSeatPid;
  private Boolean externalAdVerificationEnabled;
  private Boolean fraudDetectionJavascriptEnabled;
}
