package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.json.BigDecimalSerializer;
import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.util.validator.PublisherAndSiteAssociationTypeConstraint;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.math.BigDecimal;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Builder(builderClassName = "Builder", setterPrefix = "with", builderMethodName = "newBuilder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PublisherDTO {

  @Getter @Setter private Long pid;
  @Getter @Setter private Integer version;
  @Getter @Setter private String name;
  @Getter @Setter private String id;

  @Getter @Setter private Boolean videoUseInboundSiteOrApp;
  @Getter @Setter @Valid private PublisherAttributes attributes;
  @Getter @Setter private Set<PublisherEligibleBiddersDTO> eligibleBidderGroups;

  // advanced fields
  @Getter @Setter private Long contactUserPid;
  @Setter private boolean adServingEnabled;
  @Setter private boolean restrictDrillDown;

  @DecimalMax("99999999.99999999")
  @Getter
  @Setter
  private Double directAdServingFee;

  @DecimalMax("99999999.99999999")
  @Getter
  @Setter
  private Double houseAdServingFee;

  @DecimalMax("99999999.99999999")
  @Getter
  @Setter
  private Double nonRemnantHouseAdCap;

  @DecimalMax("99999999.99999999")
  @Getter
  @Setter
  private Double houseAdOverageFee;

  @Getter @Setter @NotNull private Boolean cpiTrackingEnabled;

  @Size(max = 200)
  @Getter
  @Setter
  private String cpiConversionNoticeUrl;

  @JsonIgnore private String obiGuid;

  @Size(max = 255)
  @Getter
  @Setter
  @JsonIgnore
  private String email;

  @Getter @Setter @NotNull private Boolean rtbEnabled;
  @Getter @Setter @NotNull private Boolean mediationEnabled;
  @Getter @Setter @NotNull private Boolean rtbRevenueReportEnabled;

  @Getter
  @Setter
  @Size(max = 100)
  private String salesforceId;

  @Getter
  @Setter
  @DecimalMax("9999999.99")
  private BigDecimal bidderAdServingFee;

  @Getter @Setter @NotNull private Status status;

  @Size(max = 100)
  @Getter
  @Setter
  private String globalAliasName;

  @Getter
  @Setter
  @NotNull
  @Size(max = 100)
  private String website;

  @Getter
  @Setter
  @Size(max = 255)
  private String description;

  @Getter @Setter @NotNull private CompanyType type;
  @Setter private boolean test;
  @Setter private boolean selfServeAllowed;
  @Getter @Setter private Long regionId;
  @Getter @Setter private Boolean payoutEnabled;

  // transient fields
  @Getter @Setter private Integer numberOfRtbTags;
  @Getter @Setter private Integer numberOfMediationSites;
  @Getter @Setter private Set<String> adsourceNames;
  @Getter @Setter private Set<String> externalDataProviderNames;
  @Getter @Setter private Integer activeIOs;

  @Getter
  @Setter
  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal credit;

  @Getter @Setter private Company.EstimateTimeRemaining estimatedTimeRemain;
  @Getter @Setter private int numberOfUsers;
  @Getter @Setter private Boolean hasHeaderBiddingSites;
  @Getter @Setter private Boolean thirdPartyFraudDetectionEnabled;
  @Setter private boolean defaultRtbProfilesEnabled;

  @Valid @PublisherAndSiteAssociationTypeConstraint @Getter @Setter
  private Set<HbPartnerAssignmentDTO> hbPartnerAttributes;

  @Getter
  @Setter
  @Size(max = 3)
  private String currency;

  @Getter @Setter private Long sellerSeatPid;

  public boolean getAdServingEnabled() {
    return adServingEnabled;
  }

  public boolean getRestrictDrillDown() {
    return restrictDrillDown;
  }

  public boolean getDefaultRtbProfilesEnabled() {
    return defaultRtbProfilesEnabled;
  }

  public boolean getTest() {
    return test;
  }

  public boolean getSelfServeAllowed() {
    return selfServeAllowed;
  }

  @Getter @Setter private Boolean externalAdVerificationEnabled;

  @Getter @Setter private Boolean fraudDetectionJavascriptEnabled;

  public Long getPid() {
    return pid;
  }
}
