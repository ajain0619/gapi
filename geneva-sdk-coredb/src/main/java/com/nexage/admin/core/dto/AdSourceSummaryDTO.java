package com.nexage.admin.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.AdSource.AuthenticationType;
import com.nexage.admin.core.model.AdSource.BidEnabled;
import com.nexage.admin.core.model.AdSource.DecisionMakerEnabled;
import com.nexage.admin.core.model.AdSource.ParamKey;
import com.nexage.admin.core.model.AdSource.SelfServeEnablement;
import com.nexage.admin.core.util.MapSplitter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@Getter
@Setter
public class AdSourceSummaryDTO
    implements com.nexage.admin.core.model.AdSourceSummary, Serializable {

  private static final long serialVersionUID = 1L;

  @JsonIgnore private String id;
  private Long pid;
  private String name;
  @JsonIgnore private Status status;
  @JsonIgnore private String description;
  @JsonIgnore private String primaryId;
  @JsonIgnore private String primaryName;
  @JsonIgnore private String secondaryId;
  @JsonIgnore private String secondaryName;
  @JsonIgnore private String primaryIdDefault;
  @JsonIgnore private String primaryNameDefault;
  @JsonIgnore private String secondaryIdDefault;
  @JsonIgnore private String secondaryNameDefault;
  @JsonIgnore private boolean primaryIdRequired;
  @JsonIgnore private boolean primaryNameRequired;
  @JsonIgnore private boolean secondaryIdRequired;
  @JsonIgnore private boolean secondaryNameRequired;
  private Boolean exchange = false; // this is actually set in the service methods;
  private Boolean testExchange = false; // this is actually set in the service methods;
  private List<ParameterConfig> parameterConfig = new ArrayList<>();
  private Map<String, String> paramMetadata = new HashMap<>();
  private AuthenticationType reportAuthType;

  private SelfServeEnablement selfServeEnablement;
  private AdSource.BidEnabled bidEnabled;
  private AdSource.DecisionMakerEnabled decisionMakerEnabled;

  @JsonIgnore private String logo;

  private String logoUrl;

  private static final MapSplitter mapSplitter =
      MapSplitter.separator(",").withKeyValueSeparator("=");

  public AdSourceSummaryDTO(
      String id,
      Long pid,
      String name,
      String description,
      Status status,
      String paramMap,
      SelfServeEnablement selfServeEnablement,
      String paramDefaultMap,
      String paramRequiredList,
      AuthenticationType reportAuthType,
      Map<String, String> paramMetadata,
      BidEnabled bidEnabled,
      DecisionMakerEnabled decisionMakerEnabled,
      String logo) {
    this.id = id;
    this.pid = pid;
    this.name = name;
    this.description = description;
    this.status = status;
    this.reportAuthType = reportAuthType;

    Map<String, String> aliases =
        paramMap != null
            ? Splitter.on(",").omitEmptyStrings().withKeyValueSeparator("=").split(paramMap)
            : new HashMap<>();
    this.primaryId = aliases.get(ParamKey.pid.name());
    this.primaryName = aliases.get(ParamKey.pname.name());
    this.secondaryId = aliases.get(ParamKey.sid.name());
    this.secondaryName = aliases.get(ParamKey.sname.name());
    this.selfServeEnablement = selfServeEnablement;

    Map<String, String> defaults =
        paramDefaultMap != null
            ? Splitter.on(",").omitEmptyStrings().withKeyValueSeparator("=").split(paramDefaultMap)
            : new HashMap<>();
    this.primaryIdDefault = defaults.get(ParamKey.pid.name());
    this.primaryNameDefault = defaults.get(ParamKey.pname.name());
    this.secondaryIdDefault = defaults.get(ParamKey.sid.name());
    this.secondaryNameDefault = defaults.get(ParamKey.sname.name());

    if (StringUtils.isBlank(paramRequiredList)) {
      this.primaryIdRequired = false;
      this.primaryNameRequired = false;
      this.secondaryIdRequired = false;
      this.secondaryNameRequired = false;
    } else {
      List<String> required =
          Lists.newArrayList(Splitter.on(",").trimResults().split(paramRequiredList));
      this.primaryIdRequired = required.contains(ParamKey.pid.name());
      this.primaryNameRequired = required.contains(ParamKey.pname.name());
      this.secondaryIdRequired = required.contains(ParamKey.sid.name());
      this.secondaryNameRequired = required.contains(ParamKey.sname.name());
    }
    if (paramMetadata != null && paramMetadata.size() > 0) {
      this.paramMetadata = paramMetadata;
    }

    this.bidEnabled = bidEnabled;
    this.decisionMakerEnabled = decisionMakerEnabled;
    this.logo = logo;
  }

  public AdSourceSummaryDTO(AdSource adSource) {
    id = adSource.getId();
    pid = adSource.getPid();
    name = adSource.getName();
    description = adSource.getDescription();
    status = adSource.getStatus();
    primaryId = adSource.getPrimaryId();
    primaryName = adSource.getPrimaryName();
    secondaryId = adSource.getSecondaryId();
    secondaryName = adSource.getSecondaryName();
    selfServeEnablement = adSource.getSelfServeEnablement();
    bidEnabled = adSource.getBidEnabled();
    decisionMakerEnabled = adSource.getDecisionMakerEnabled();
    logo = adSource.getLogo();
  }

  public static AdSourceSummaryDTO createAdSourceSummaryForGeneva(
      String id,
      Long pid,
      String name,
      String description,
      Status status,
      String paramAliases,
      SelfServeEnablement selfServeEnablement,
      String paramDefaultMap,
      String paramRequiredList,
      AuthenticationType reportAuthType,
      Map<String, String> paramMetadata,
      BidEnabled bidEnabled,
      DecisionMakerEnabled decisionMakerEnabled,
      String logo) {
    AdSourceSummaryDTO summary = new AdSourceSummaryDTO();
    summary.id = id;
    summary.pid = pid;
    summary.name = name;
    summary.description = description;
    summary.status = status;
    summary.reportAuthType = reportAuthType;
    Map<String, String> aliases = mapSplitter.split(paramAliases);
    for (String key : aliases.keySet()) {
      String alias = aliases.get(key);
      if (!alias.equals("none")) {
        summary.parameterConfig.add(new ParameterConfig(key, alias.equals("none") ? "" : alias));
      }
    }
    summary.selfServeEnablement = selfServeEnablement;

    Map<String, String> defaults =
        paramDefaultMap != null
            ? Splitter.on(",").omitEmptyStrings().withKeyValueSeparator("=").split(paramDefaultMap)
            : new HashMap<>();
    summary.primaryIdDefault = getValidParameter(defaults.get(ParamKey.pid.name()));
    summary.primaryNameDefault = getValidParameter(defaults.get(ParamKey.pname.name()));
    summary.secondaryIdDefault = getValidParameter(defaults.get(ParamKey.sid.name()));
    summary.secondaryNameDefault = getValidParameter(defaults.get(ParamKey.sname.name()));

    if (StringUtils.isBlank(paramRequiredList)) {
      summary.primaryIdRequired = false;
      summary.primaryNameRequired = false;
      summary.secondaryIdRequired = false;
      summary.secondaryNameRequired = false;
    } else {
      List<String> required =
          Lists.newArrayList(Splitter.on(",").trimResults().split(paramRequiredList));
      summary.primaryIdRequired = required.contains(ParamKey.pid.name());
      summary.primaryNameRequired = required.contains(ParamKey.pname.name());
      summary.secondaryIdRequired = required.contains(ParamKey.sid.name());
      summary.secondaryNameRequired = required.contains(ParamKey.sname.name());
    }

    if (paramMetadata != null && paramMetadata.size() > 0) {
      summary.paramMetadata = paramMetadata;
    }

    summary.bidEnabled = bidEnabled;
    summary.decisionMakerEnabled = decisionMakerEnabled;
    summary.logo = logo;

    return summary;
  }

  private static String getValidParameter(String value) {
    String returnValue = null;
    if (null != value && !value.equals("none")) {
      returnValue = value;
    }
    return returnValue;
  }

  public static final class ParameterConfig {
    private final String name;
    private final String alias;

    public ParameterConfig(
        @JsonProperty(value = "name") String name, @JsonProperty(value = "alias") String alias) {
      this.name = name;
      this.alias = alias;
    }

    public String getName() {
      return name;
    }

    public String getAlias() {
      return alias;
    }
  }
}
