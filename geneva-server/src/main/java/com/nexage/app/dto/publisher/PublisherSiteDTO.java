package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.Status;
import com.nexage.app.util.validator.PublisherAndSiteAssociationTypeConstraint;
import com.nexage.app.util.validator.SiteIabCategoriesConstraint;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@JsonInclude(Include.NON_NULL)
@Setter
@Getter
public class PublisherSiteDTO implements Serializable {

  public enum SiteType {
    MOBILE_WEB,
    APPLICATION,
    DESKTOP,
    DOOH,
    WEBSITE
  }

  public enum Platform {
    ANDROID,
    ANDROID_TAB,
    ANDROID_PHONE_TAB,
    BLACKBERRY,
    IPAD,
    IPAD_IPHONE,
    IPHONE,
    J2ME,
    PALM,
    WINDOWS,
    OTHER,
    UNKNOWN,
    CTV_OTT;
  }

  public enum Mode {
    TEST(false),
    LIVE(true);

    public boolean modeCode;

    private Mode(boolean modeCode) {
      this.modeCode = modeCode;
    }

    public boolean getModeCode() {
      return modeCode;
    }

    private static final HashMap<Boolean, Mode> fromBooleanMap = new HashMap<>();

    static {
      for (Mode m : Mode.values()) {
        fromBooleanMap.put(m.getModeCode(), m);
      }
    }

    public static Mode getMode(Boolean val) {
      return fromBooleanMap.get(val);
    }
  }

  private Long pid;
  private Integer version;
  private PublisherDTO publisher;
  private String dcn;

  @Size(max = 512)
  private String description;

  @NotNull private String domain;

  @NotBlank
  @Size(max = 255)
  private String name;

  @NotNull private Platform platform;
  @NotNull private Status status;
  @NotNull private SiteType type;

  @NotNull
  @Size(max = 255)
  private String url;

  @Size(max = 100)
  private String appBundle;

  @NotNull private boolean coppaRestricted;

  @Size(max = 1000)
  private String rtb1CategoryRollup;

  @NotNull
  @SiteIabCategoriesConstraint(min = 0, max = 4, nullable = false)
  private Set<String> iabCategories;

  private PublisherSiteIconDTO icon;

  private Set<PublisherPositionDTO> positions;
  private Set<PublisherTagDTO> tags;
  private Set<PublisherRTBProfileDTO> rtbProfiles;
  private Mode mode;
  private boolean hbEnabled;

  // additional fields
  private Integer trafficThrottle;
  private boolean adTruthEnabled;
  private String globalAliasName;
  private Boolean metadataEnablement;
  private boolean adScreeningEnabled = true;
  private PublisherImpressionGroupDTO impressionGroup;
  private Integer reportFrequency;
  private Integer reportBatchSize;
  private Integer rulesUpdateFrequency;
  private boolean filterBots = true;
  private Integer buyerTimeout;
  private Integer daysFree;
  private Integer totalTimeout;
  private Set<String> defaultPositions;
  private Set<String> passthruParameters;

  private boolean consumerProfileContributed;
  private boolean consumerProfileUsed;
  private boolean overrideIP;
  private String ethnicityMap;
  private String genderMap;
  private String maritalStatusMap;
  private String inputDateFormat;
  private PublisherDefaultRTBProfileDTO defaultRtbProfile;
  private PublisherSiteDealTermDTO currentDealTerm;

  @Valid @PublisherAndSiteAssociationTypeConstraint
  private Set<HbPartnerAssignmentDTO> hbPartnerAttributes;

  private BigDecimal creativeSuccessRateThreshold;

  public PublisherSiteDTO() {}

  private PublisherSiteDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.publisher = builder.publisher;
    this.dcn = builder.dcn;
    this.description = builder.description;
    this.domain = builder.domain;
    this.platform = builder.platform;
    this.status = builder.status;
    this.type = builder.type;
    this.url = builder.url;
    this.appBundle = builder.appBundle;
    this.coppaRestricted = builder.coppaRestricted;
    this.rtb1CategoryRollup = builder.rtb1CategoryRollup;
    this.iabCategories = builder.iabCategories;
    this.positions = builder.positions;
    this.tags = builder.tags;
    this.rtbProfiles = builder.rtbProfiles;
    this.name = builder.name;
    this.icon = builder.icon;
    this.mode = builder.mode;
    this.hbEnabled = builder.hbEnabled;
    this.trafficThrottle = builder.trafficThrottle;
    this.adTruthEnabled = builder.adTruthEnabled;
    this.globalAliasName = builder.globalAliasName;
    this.metadataEnablement = builder.metadataEnablement;
    this.adScreeningEnabled = builder.adScreeningEnabled;
    this.impressionGroup = builder.impressionGroup;
    this.reportFrequency = builder.reportFrequency;
    this.reportBatchSize = builder.reportBatchSize;
    this.rulesUpdateFrequency = builder.rulesUpdateFrequency;

    this.filterBots = builder.filterBots;
    this.buyerTimeout = builder.buyerTimeout;
    this.daysFree = builder.daysFree;
    this.totalTimeout = builder.totalTimeout;
    this.defaultPositions = builder.defaultPositions;
    this.passthruParameters = builder.passthruParameters;

    this.consumerProfileContributed = builder.consumerProfileContributed;
    this.consumerProfileUsed = builder.consumerProfileUsed;
    this.overrideIP = builder.overrideIP;
    this.ethnicityMap = builder.ethnicityMap;
    this.genderMap = builder.genderMap;
    this.maritalStatusMap = builder.maritalStatusMap;
    this.inputDateFormat = builder.inputDateFormat;
    this.defaultRtbProfile = builder.defaultRtbProfile;
    this.currentDealTerm = builder.currentDealTerm;
    this.hbPartnerAttributes = builder.hbPartnerAttributes;
    this.creativeSuccessRateThreshold = builder.creativeSuccessRateThreshold;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private PublisherDTO publisher;
    private String dcn;
    private String description;
    private String name;
    private String domain;
    private Platform platform;
    private Status status;
    private SiteType type;
    private String url;
    private String appBundle;
    private boolean coppaRestricted;
    private String rtb1CategoryRollup;
    private Set<String> iabCategories;
    private PublisherSiteIconDTO icon;

    private Set<PublisherPositionDTO> positions;
    private Set<PublisherTagDTO> tags;
    private Set<PublisherRTBProfileDTO> rtbProfiles;
    private Mode mode;
    private boolean hbEnabled;

    // additional fields
    private Integer trafficThrottle;
    private boolean adTruthEnabled;
    private String globalAliasName;
    private Boolean metadataEnablement;
    private boolean adScreeningEnabled;
    private PublisherImpressionGroupDTO impressionGroup;
    private Integer reportFrequency;
    private Integer reportBatchSize;
    private Integer rulesUpdateFrequency;
    private boolean filterBots;
    private Integer buyerTimeout;
    private Integer daysFree;
    private Integer totalTimeout;
    private Set<String> defaultPositions;
    private Set<String> passthruParameters;

    private boolean consumerProfileContributed;
    private boolean consumerProfileUsed;
    private boolean overrideIP;
    private String ethnicityMap;
    private String genderMap;
    private String maritalStatusMap;
    private String inputDateFormat;
    private PublisherDefaultRTBProfileDTO defaultRtbProfile;
    private PublisherSiteDealTermDTO currentDealTerm;
    private Set<HbPartnerAssignmentDTO> hbPartnerAttributes;
    private BigDecimal creativeSuccessRateThreshold;

    public Builder withFilterBots(boolean filterBots) {
      this.filterBots = filterBots;
      return this;
    }

    public Builder withBuyerTimeout(Integer buyerTimeout) {
      this.buyerTimeout = buyerTimeout;
      return this;
    }

    public Builder withDaysFree(Integer daysFree) {
      this.daysFree = daysFree;
      return this;
    }

    public Builder withTotalTimeout(Integer totalTimeout) {
      this.totalTimeout = totalTimeout;
      return this;
    }

    public Builder withDefaultPositions(Set<String> defaultPositions) {
      this.defaultPositions = defaultPositions;
      return this;
    }

    public Builder withPassthruParameters(Set<String> passthruParameters) {
      this.passthruParameters = passthruParameters;
      return this;
    }

    public Builder withReportFrequency(Integer reportFrequency) {
      this.reportFrequency = reportFrequency;
      return this;
    }

    public Builder withReportBatchSize(Integer reportBatchSize) {
      this.reportBatchSize = reportBatchSize;
      return this;
    }

    public Builder withRulesUpdateFrequency(Integer rulesUpdateFrequency) {
      this.rulesUpdateFrequency = rulesUpdateFrequency;
      return this;
    }

    public Builder withTrafficThrottle(Integer trafficThrottle) {
      this.trafficThrottle = trafficThrottle;
      return this;
    }

    public Builder withAdTruthEnabled(boolean adTruthEnabled) {
      this.adTruthEnabled = adTruthEnabled;
      return this;
    }

    public Builder withGlobalAliasName(String globalAliasName) {
      this.globalAliasName = globalAliasName;
      return this;
    }

    public Builder withMetadataEnablement(Boolean metadataEnablement) {
      this.metadataEnablement = metadataEnablement;
      return this;
    }

    public Builder withAdScreeningEnabled(boolean adScreeningEnabled) {
      this.adScreeningEnabled = adScreeningEnabled;
      return this;
    }

    public Builder withImpressionGroup(PublisherImpressionGroupDTO impressionGroup) {
      this.impressionGroup = impressionGroup;
      return this;
    }

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withPublisher(PublisherDTO publisher) {
      this.publisher = publisher;
      return this;
    }

    public Builder withDcn(String dcn) {
      this.dcn = dcn;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder withDomain(String domain) {
      this.domain = domain;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withPlatform(Platform platform) {
      this.platform = platform;
      return this;
    }

    public Builder withStatus(Status status) {
      this.status = status;
      return this;
    }

    public Builder withType(SiteType type) {
      this.type = type;
      return this;
    }

    public Builder withUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder withIcon(PublisherSiteIconDTO icon) {
      this.icon = icon;
      return this;
    }

    public Builder withAppBundle(String appBundle) {
      this.appBundle = appBundle;
      return this;
    }

    public Builder withCoppaRestricted(boolean coppaRestricted) {
      this.coppaRestricted = coppaRestricted;
      return this;
    }

    public Builder withRtb1CategoryRollup(String rtb1CategoryRollup) {
      this.rtb1CategoryRollup = rtb1CategoryRollup;
      return this;
    }

    public Builder withIabCategory(String iabCategory) {
      if (iabCategories == null) {
        iabCategories = new HashSet<>();
      }
      iabCategories.add(iabCategory);
      return this;
    }

    public Builder withIabCategories(Set<String> iabCategories) {
      if (this.iabCategories == null) {
        this.iabCategories = new HashSet<>();
      }
      this.iabCategories.addAll(iabCategories);
      return this;
    }

    public Builder withPosition(PublisherPositionDTO position) {
      if (positions == null) {
        positions = new HashSet<>();
      }
      this.positions.add(position);
      return this;
    }

    public Builder withTag(PublisherTagDTO tag) {
      if (tags == null) {
        tags = new HashSet<>();
      }
      this.tags.add(tag);
      return this;
    }

    public Builder withRtbProfile(PublisherRTBProfileDTO rtbProfile) {
      if (rtbProfiles == null) {
        rtbProfiles = new HashSet<>();
      }
      this.rtbProfiles.add(rtbProfile);
      return this;
    }

    public Builder withMode(Mode mode) {
      this.mode = mode;
      return this;
    }

    public Builder withHbEnabled(boolean hbEnabled) {
      this.hbEnabled = hbEnabled;
      return this;
    }

    public Builder withConsumerProfileContributed(Boolean consumerProfileContributed) {
      this.consumerProfileContributed = consumerProfileContributed;
      return this;
    }

    public Builder withConsumerProfileUsed(boolean consumerProfileUsed) {
      this.consumerProfileUsed = consumerProfileUsed;
      return this;
    }

    public Builder withOverrideIP(boolean overrideIP) {
      this.overrideIP = overrideIP;
      return this;
    }

    public Builder withEthnicityMap(String ethnicityMap) {
      this.ethnicityMap = ethnicityMap;
      return this;
    }

    public Builder withGenderMap(String genderMap) {
      this.genderMap = genderMap;
      return this;
    }

    public Builder withMaritalStatusMap(String maritalStatusMap) {
      this.maritalStatusMap = maritalStatusMap;
      return this;
    }

    public Builder withInputDateFormat(String inputDateFormat) {
      this.inputDateFormat = inputDateFormat;
      return this;
    }

    public Builder withDefaultRtbProfile(PublisherDefaultRTBProfileDTO defaultRtbProfile) {
      this.defaultRtbProfile = defaultRtbProfile;
      return this;
    }

    public Builder withCurrentDealTerm(PublisherSiteDealTermDTO currentDealTerm) {
      this.currentDealTerm = currentDealTerm;
      return this;
    }

    public Builder withHbPartnerAttributes(Set<HbPartnerAssignmentDTO> hbPartnerAttributes) {
      this.hbPartnerAttributes = hbPartnerAttributes;
      return this;
    }

    public Builder withCreativeSuccessRateThreshold(BigDecimal successRateThreshold) {
      this.creativeSuccessRateThreshold = successRateThreshold;
      return this;
    }

    public PublisherSiteDTO build() {
      return new PublisherSiteDTO(this);
    }
  }
}
