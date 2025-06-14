package com.nexage.app.dto.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@Setter
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SiteDTO {

  @Schema(title = "DCN value also known an alternate Site ID")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String dcn;

  @Schema(title = "Site Id")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String id;

  @Schema(title = "Primary key for the table")
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long pid;

  @Schema(title = "Version of the site")
  @ToString.Include
  private Integer version;

  @ToString.Include private boolean adScreeningEnabled;

  @ToString.Include private boolean groupsEnabled;

  @ToString.Include private Integer buyerTimeout;

  @ToString.Include private boolean consumerProfileContributed;

  @ToString.Include private boolean consumerProfileUsed;

  @ToString.Include private Date creationDate;

  @ToString.Include private Date lastUpdate;

  @ToString.Include private Integer daysFree;

  @ToString.Include private Date revenueLaunchDate;

  @ToString.Include private String description;

  @ToString.Include private String domain;

  @ToString.Include private String ethnicityMap;

  @ToString.Include private boolean filterBots;

  @ToString.Include private String genderMap;

  @ToString.Include private String inputDateFormat;

  @ToString.Include private Type type;

  @ToString.Include private boolean live;

  @ToString.Include private String maritalStatusMap;

  @Schema(title = "Site Name")
  @EqualsAndHashCode.Include
  @ToString.Include
  private String name;

  @ToString.Include private boolean overrideIP;

  @ToString.Include private Platform platform;

  @ToString.Include private Integer reportBatchSize;

  @ToString.Include private Integer reportFrequency;

  @ToString.Include private Integer rulesUpdateFrequency;

  @Schema(title = "Status of Site")
  @ToString.Include
  private Status status;

  @JsonProperty("isAdTruthEnabled")
  @ToString.Include
  private boolean adTruthEnabled;

  @ToString.Include private Integer statusVal;

  @ToString.Include private Integer trafficThrottle;

  @ToString.Include private Integer totalTimeout;

  @ToString.Include private String url;

  @ToString.Include private boolean coppaRestricted;

  @ToString.Include private String rtb1CategoryRollup;

  @ToString.Include private String globalAliasName;

  @ToString.Include private Boolean metadataEnablement = false;

  @ToString.Include private Boolean hbEnabled = false;

  @ToString.Include
  @Schema(title = "Seller Id")
  @EqualsAndHashCode.Include
  private Long companyPid;

  @Schema(title = "Seller Name")
  private String companyName;

  @Schema(title = "Seller GlobalAliasName")
  private String companyGlobalAliasName;

  @ToString.Include private String appBundle;

  @ToString.Include private Integer includeSiteName;

  @ToString.Include private Long siteAliasId;

  @ToString.Include private String siteNameAlias;

  @ToString.Include private Integer includePubName;

  @ToString.Include private Long pubAliasId;

  @ToString.Include private String pubNameAlias;

  /**
   * Pid getter. Not to be used in code; its only role is to ensure the misspelled "pId" field is in
   * the serialized form to avoid breaking the contract.
   *
   * @deprecated
   * @return site PID
   */
  @JsonProperty("pId")
  @Deprecated(forRemoval = true)
  public Long getMisspelledPid() {
    return getPid();
  }

  /**
   * Pid setter. Not to be used in code; its only role isto enable deserialization of the misspelled
   * "pId" field to avoid breaking the contract.
   *
   * @deprecated
   * @param pid site pid
   */
  @Deprecated(forRemoval = true)
  public void setMisspelledPid(Long pid) {
    setPid(pid);
  }
}
