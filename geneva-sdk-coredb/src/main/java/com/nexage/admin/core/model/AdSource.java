package com.nexage.admin.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.sparta.jpa.model.AdSourceLogoFileReference;
import com.nexage.admin.core.sparta.jpa.model.NativeTypeAdsource;
import com.nexage.admin.core.util.MapJoiner;
import com.nexage.admin.core.util.MapSplitter;
import com.nexage.admin.core.util.UUIDGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "ad_source")
@Audited
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class AdSource implements AdSourceSummary, Serializable {

  private static final long serialVersionUID = 1L;
  public static final String PARAM_MAP_NONE = "none";

  /* attempted to use guava splitter but it is sensitive to spacing in name = value pairs */
  private static final MapSplitter mapSplitter =
      MapSplitter.separator(",").withKeyValueSeparator("=");
  private static final MapJoiner mapJoiner =
      MapJoiner.separator(",").withKeyValueSeparator("=").excludeNullValues(true);

  public static final int CRID_HEADER_FIELD_LENGTH_LIMIT = 255;

  public enum SelfServeEnablement {
    NONE(0),
    PUBLISHER(1),
    ADDITIONAL(2);

    int code;

    SelfServeEnablement(int code) {
      this.code = code;
    }

    public int asInt() {
      return code;
    }

    private static final HashMap<Integer, SelfServeEnablement> fromIntMap = new HashMap<>();

    static {
      for (SelfServeEnablement s : SelfServeEnablement.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static SelfServeEnablement fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }

  public enum AuthenticationType {
    NONE(0),
    USERNAME_PASSWORD(1),
    APIKEY(2),
    APITOKEN(3),
    COMBINATION(4);

    int code;

    AuthenticationType(int code) {
      this.code = code;
    }

    public int asInt() {
      return code;
    }

    private static final HashMap<Integer, AuthenticationType> fromIntMap = new HashMap<>();

    static {
      for (AuthenticationType s : AuthenticationType.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static AuthenticationType fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }

  public enum BidEnabled {
    NO(0),
    YES(1);

    int codeInt;

    BidEnabled(int codeInt) {
      this.codeInt = codeInt;
    }

    public int asInt() {
      return codeInt;
    }

    private static final HashMap<Integer, BidEnabled> fromIntMap = new HashMap<>();

    static {
      for (BidEnabled s : BidEnabled.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static BidEnabled fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }

  public enum DecisionMakerEnabled {
    NO(0),
    YES(1);

    int code;

    DecisionMakerEnabled(int code) {
      this.code = code;
    }

    public int asInt() {
      return code;
    }

    private static final HashMap<Integer, DecisionMakerEnabled> fromIntMap = new HashMap<>();

    static {
      for (DecisionMakerEnabled s : DecisionMakerEnabled.values()) {
        fromIntMap.put(s.asInt(), s);
      }
    }

    public static DecisionMakerEnabled fromInt(Integer i) {
      return fromIntMap.get(i);
    }
  }

  @GeneratedValue(generator = "UseIdOrGenerate", strategy = GenerationType.AUTO)
  @GenericGenerator(
      name = "UseIdOrGenerate",
      strategy = "com.nexage.admin.core.sparta.jpa.model.UseIdOrGenerate")
  @Column(nullable = false, updatable = false)
  @Id
  @ToString.Include
  private Long pid;

  @Column(name = "id", nullable = false, length = 32)
  @ToString.Include
  private String id;

  @Version
  @Column(name = "version", nullable = false)
  @ToString.Include
  private Integer version;

  @ManyToOne
  @JoinColumn(name = "company_pid", referencedColumnName = "pid")
  @JsonIgnore
  private Company company;

  @Column(name = "company_pid", insertable = false, updatable = false)
  @ToString.Include
  private Long companyPid;

  @Column(nullable = false, length = 255)
  @ToString.Include
  private String name;

  @Column(nullable = false, length = 255)
  @ToString.Include
  private String description;

  @Column(name = "status", nullable = false)
  @org.hibernate.annotations.Type(type = "com.nexage.admin.core.custom.type.StatusEnumType")
  @ToString.Include
  private Status status;

  @JsonIgnore @Transient private Integer statusVal;

  @Column(name = "partner_id", length = 255)
  private String partnerId;

  @Column(name = "creation_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @Column(name = "last_update", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdate;

  @Column(name = "class_name", nullable = false, length = 255)
  private String className;

  @Lob
  @Column(name = "url_template", nullable = false)
  private String urlTemplate;

  @Column(name = "ad_type", nullable = false)
  private int adType;

  @Lob
  @Column(name = "post_template")
  private String postTemplate;

  @Lob
  @Column(name = "get_template")
  private String getTemplate;

  @Lob
  @Column(name = "extra_post_template")
  private String extraPostTemplate;

  @Lob
  @Column(name = "extra_get_template")
  private String extraGetTemplate;

  @Column(name = "use_device_useragent")
  private boolean useDeviceUseragent;

  @Lob
  @Column(name = "test_mode_param")
  private String testModeParam;

  @Lob
  @Column(name = "live_mode_param")
  private String liveModeParam;

  @Lob
  @Column(name = "header_template")
  private String headerTemplate;

  @Column(name = "header_pass_through", nullable = false)
  private boolean headerPassThrough;

  @Lob
  @Column(name = "header_prefix")
  private String headerPrefix;

  @Lob
  @Column(name = "noad_regex")
  private String noadRegex;

  @Lob
  @Column(name = "ad_process_template")
  private String adProcessTemplate;

  @Column(name = "disable_click_wrap", nullable = false)
  private boolean disableClickWrap;

  @Lob
  @Column(name = "param_map", length = 255)
  private String paramMap;

  @Column(name = "param_metadata")
  @Type(type = "com.nexage.admin.core.custom.type.JsonMapUserType")
  private Map<String, String> paramMetadata = new HashMap<>();

  @Lob
  @Column(name = "param_default", length = 255)
  private String paramDefault;

  @Lob
  @Column(name = "param_required", length = 255)
  private String paramRequired;

  @Column(name = "markup_format_map", length = 255)
  private String markupFormatMap;

  @Lob
  @Column(name = "ad_type_map", length = 255)
  private String adTypeMap;

  @Column(name = "gender_map", length = 255)
  private String genderMap;

  @Column(name = "marital_status_map", length = 255)
  private String maritalStatusMap;

  @Lob
  @Column(name = "ethnicity_map", length = 255)
  private String ethnicityMap;

  @Column(name = "dob_format", length = 255)
  private String dobFormat;

  @Column(name = "line_item_map", length = 255)
  private String lineItemMap;

  @Column(name = "ad_screening", nullable = false)
  private boolean adScreening;

  @Lob
  @Column(name = "as_click_url_regex")
  private String asClickUrlRegex;

  @Lob
  @Column(name = "as_creative_url_regex")
  private String asCreativeUrlRegex;

  @Lob
  @Column(name = "as_text_regex")
  private String asTextRegex;

  @Lob
  @Column(name = "as_click_url_id_regex")
  private String asClickUrlIdRegex;

  @Lob
  @Column(name = "as_creative_url_id_regex")
  private String asCreativeUrlIdRegex;

  @Lob
  @Column(name = "as_text_id_regex")
  private String asTextIdRegex;

  @Lob
  @Column(name = "as_content_id_regex")
  private String asContentIdRegex;

  @Column(name = "use_wrapped_sdk", nullable = false)
  private boolean useWrappedSdk;

  @Lob
  @Column(name = "report_col_seq")
  private String reportColSeq;

  @Column(name = "report_skip_lines")
  private Integer reportSkipLines;

  @Column(name = "report_min_cols")
  private Integer reportMinCols;

  @Column(name = "report_col_sep", length = 2)
  private String reportColSep;

  @Column(name = "report_data_date_format", length = 255)
  private String reportDataDateFormat;

  @Column(name = "xml_mapping_folder", length = 255)
  private String xmlMappingFolder;

  @Column(name = "xml_ad_type_regex", length = 255)
  private String xmlAdTypeRegex;

  @Column(name = "report_auth_type")
  @org.hibernate.annotations.Type(
      type = "com.nexage.admin.core.custom.type.AuthenticationTypeUserType")
  private AuthenticationType reportAuthType;

  @Column(name = "self_serve_enablement", nullable = false)
  @org.hibernate.annotations.Type(
      type = "com.nexage.admin.core.custom.type.SelfServeEnablementType")
  private SelfServeEnablement selfServeEnablement;

  @Column(name = "crid_header_field", length = CRID_HEADER_FIELD_LENGTH_LIMIT)
  private String cridHeaderField;

  @Column(name = "bid_enabled", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private BidEnabled bidEnabled = BidEnabled.NO;

  @Column(name = "decision_maker_enabled", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private DecisionMakerEnabled decisionMakerEnabled = DecisionMakerEnabled.NO;

  @Lob
  @Column(name = "response_parsing_config")
  private String responseParsingConfig;

  @Column(name = "logo")
  private String logo;

  @Transient private Map<String, String> mapParamMap = new TreeMap<>();

  @Transient private Map<String, String> mapParamDefault = new TreeMap<>();

  @Transient private Map<String, String> mapParamRequired = new TreeMap<>();

  @Transient private boolean init = false;

  @Transient private boolean exchange = false;

  @Transient private String logoUrl;

  @Transient
  @JsonInclude(Include.NON_NULL)
  private AdSourceLogoFileReference logoFileReference;

  @JsonInclude(Include.NON_EMPTY)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "adsource", cascade = CascadeType.ALL)
  private List<NativeTypeAdsource> nativeTypeAdSource = new ArrayList<>();

  public AdSource() {
    this.setStatus(Status.INACTIVE);
    this.setUseDeviceUseragent(true);
    this.setHeaderPassThrough(false);
    this.setDisableClickWrap(false);
    this.setAdScreening(false);
    this.setUseWrappedSdk(false);
    this.setSelfServeEnablement(SelfServeEnablement.NONE);
  }

  public AdSource(Company company) {
    this();
    this.company = company;
  }

  @Transient
  public void setStatus(Status status) {
    this.status = status;
    this.statusVal = status.asInt();
  }

  public Integer getStatusVal() {
    if (statusVal == null) {
      statusVal = status.asInt();
    }
    return statusVal;
  }

  public void setStatusVal(Integer statusVal) {
    this.statusVal = statusVal;
    this.status = Status.fromInt(statusVal);
  }

  public void setUrlTemplate(String urlTemplate) {
    this.urlTemplate = trimmed(urlTemplate);
  }

  public void setResponseParsingConfig(String responseParsingConfig) {
    this.responseParsingConfig = trimmed(responseParsingConfig);
  }

  public void setPostTemplate(String postTemplate) {
    this.postTemplate = trimmed(postTemplate);
  }

  public void setGetTemplate(String getTemplate) {
    this.getTemplate = trimmed(getTemplate);
  }

  public void setExtraPostTemplate(String extraPostTemplate) {
    this.extraPostTemplate = trimmed(extraPostTemplate);
  }

  public void setExtraGetTemplate(String extraGetTemplate) {
    this.extraGetTemplate = trimmed(extraGetTemplate);
  }

  public void setParamMap(String paramMap) {
    this.paramMap = paramMap;
    updateMapFromParamMap();
  }

  protected void setParamDefault(String paramDefault) {
    this.paramDefault = paramDefault;
    updateMapFromParamDefault();
  }

  protected void setParamRequired(String paramRequired) {
    this.paramRequired = paramRequired;
    updateMapFromParamRequired();
  }

  private void initTransients() {
    if (init) {
      return;
    }
    init = true;
    updateMapFromParamMap();
    updateMapFromParamDefault();
    updateMapFromParamRequired();
  }

  private void updateMapFromString(
      Map<String, String> map, boolean addDefaultKeys, String param, String defaultValue) {
    map.clear();

    // always add all keys as a default
    if (addDefaultKeys) {
      for (ParamKey key : ParamKey.values()) {
        map.put(key.name(), defaultValue);
      }
    }
    Map<String, String> tmpMap = mapSplitter.split(param == null ? "" : param);
    for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      map.put(key, isStringNullOrEmpty(value) ? defaultValue : value);
    }
  }

  private void updateMapFromParamMap() {
    updateMapFromString(mapParamMap, true, paramMap, PARAM_MAP_NONE);
  }

  private void updateParamMapFromMap() {
    paramMap = mapJoiner.join(mapParamMap);
  }

  private void updateMapFromParamDefault() {
    updateMapFromString(mapParamDefault, true, paramDefault, "");
  }

  private void updateParamDefaultFromMap() {
    paramDefault = mapJoiner.join(mapParamDefault);
  }

  private void updateMapFromParamRequired() {
    updateMapFromString(mapParamRequired, false, paramRequired, null);
  }

  private void updateParamRequiredFromMap() {
    paramRequired = mapJoiner.join(mapParamRequired);
  }

  private boolean isStringNullOrEmpty(String in) {
    return in == null || "".equals(in.trim());
  }

  private String getValueFromMapParamMap(ParamKey key) {
    initTransients();
    return mapParamMap.get(key.name());
  }

  private void setValueForMapParamMap(ParamKey key, String value) {
    initTransients();
    mapParamMap.put(key.name(), isStringNullOrEmpty(value) ? PARAM_MAP_NONE : value);
    updateParamMapFromMap();
  }

  private String getValueFromMapParamDefault(ParamKey key) {
    initTransients();
    return mapParamDefault.get(key.name());
  }

  private void setValueForMapParamDefault(ParamKey key, String value) {
    initTransients();
    mapParamDefault.put(key.name(), isStringNullOrEmpty(value) ? "" : value);
    updateParamDefaultFromMap();
  }

  private boolean isRequiredFromMapParamRequired(ParamKey key) {
    initTransients();
    return mapParamRequired.containsKey(key.name());
  }

  private void setRequiredFromMapParamRequired(ParamKey key, boolean value) {
    initTransients();
    if (value) {
      mapParamRequired.put(key.name(), null);
    } else {
      mapParamRequired.remove(key.name());
    }
    updateParamRequiredFromMap();
  }

  @Transient
  public String getPrimaryId() {
    return getValueFromMapParamMap(ParamKey.pid);
  }

  public void setPrimaryId(String primaryId) {
    setValueForMapParamMap(ParamKey.pid, primaryId);
  }

  @Transient
  public String getPrimaryIdDefault() {
    return getValueFromMapParamDefault(ParamKey.pid);
  }

  public void setPrimaryIdDefault(String primaryIdDefault) {
    setValueForMapParamDefault(ParamKey.pid, primaryIdDefault);
  }

  @Transient
  public boolean isPrimaryIdRequired() {
    return isRequiredFromMapParamRequired(ParamKey.pid);
  }

  public void setPrimaryIdRequired(boolean primaryIdRequired) {
    setRequiredFromMapParamRequired(ParamKey.pid, primaryIdRequired);
  }

  @Transient
  public String getPrimaryName() {
    return getValueFromMapParamMap(ParamKey.pname);
  }

  public void setPrimaryName(String primaryName) {
    setValueForMapParamMap(ParamKey.pname, primaryName);
  }

  @Transient
  public String getPrimaryNameDefault() {
    return getValueFromMapParamDefault(ParamKey.pname);
  }

  public void setPrimaryNameDefault(String primaryNameDefault) {
    setValueForMapParamDefault(ParamKey.pname, primaryNameDefault);
  }

  @Transient
  public boolean isPrimaryNameRequired() {
    return isRequiredFromMapParamRequired(ParamKey.pname);
  }

  public void setPrimaryNameRequired(boolean primaryNameRequired) {
    setRequiredFromMapParamRequired(ParamKey.pname, primaryNameRequired);
  }

  @Transient
  public String getSecondaryId() {
    return getValueFromMapParamMap(ParamKey.sid);
  }

  public void setSecondaryId(String secondaryId) {
    setValueForMapParamMap(ParamKey.sid, secondaryId);
  }

  @Transient
  public String getSecondaryIdDefault() {
    return getValueFromMapParamDefault(ParamKey.sid);
  }

  public void setSecondaryIdDefault(String secondaryIdDefault) {
    setValueForMapParamDefault(ParamKey.sid, secondaryIdDefault);
  }

  @Transient
  public boolean isSecondaryIdRequired() {
    return isRequiredFromMapParamRequired(ParamKey.sid);
  }

  public void setSecondaryIdRequired(boolean secondaryIdRequired) {
    setRequiredFromMapParamRequired(ParamKey.sid, secondaryIdRequired);
  }

  @Transient
  public String getSecondaryName() {
    return getValueFromMapParamMap(ParamKey.sname);
  }

  public void setSecondaryName(String secondaryName) {
    setValueForMapParamMap(ParamKey.sname, secondaryName);
  }

  @Transient
  public String getSecondaryNameDefault() {
    return getValueFromMapParamDefault(ParamKey.sname);
  }

  public void setSecondaryNameDefault(String secondaryNameDefault) {
    setValueForMapParamDefault(ParamKey.sname, secondaryNameDefault);
  }

  @Transient
  public boolean isSecondaryNameRequired() {
    return isRequiredFromMapParamRequired(ParamKey.sname);
  }

  public void setSecondaryNameRequired(boolean secondaryNameRequired) {
    setRequiredFromMapParamRequired(ParamKey.sname, secondaryNameRequired);
  }

  protected String trimmed(String input) {
    if (null != input) {
      return input.trim();
    } else {
      return input;
    }
  }

  @PrePersist
  public void onPersist() {
    // This code is handled here as it was handled by the GenericManager and BaseModel
    if (getId() == null || "".equals(getId())) {
      setId((String) new UUIDGenerator().generate());
    }
    // This is system assigned when created, but may be overridden by the client user
    if (getPartnerId() == null || "".equals(getPartnerId())) {
      setPartnerId((String) new UUIDGenerator().generate());
    }

    Date now = Calendar.getInstance().getTime();
    creationDate = now;
    lastUpdate = now;
  }

  @PreUpdate
  public void onUpdate() {
    lastUpdate = Calendar.getInstance().getTime();
  }

  public enum Ethnicity {
    African_American("0"),
    Asian("1"),
    Hispanic("2"),
    White("3"),
    Other("4");

    public static final Map<String, Ethnicity> defaultStringToMap;
    public static final Map<Ethnicity, String> defaultStringFromMap;

    static {
      Map<String, Ethnicity> tempStringToMap = new HashMap<>();
      EnumMap<Ethnicity, String> tempStringFromMap = new EnumMap<>(Ethnicity.class);
      for (Ethnicity ethnicity : EnumSet.allOf(Ethnicity.class)) {
        tempStringToMap.put(ethnicity.code(), ethnicity);
        tempStringFromMap.put(ethnicity, ethnicity.code());
      }
      defaultStringToMap = Collections.unmodifiableMap(tempStringToMap);
      defaultStringFromMap = Collections.unmodifiableMap(tempStringFromMap);
    }

    private final String code;

    Ethnicity(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }
  }

  public enum Gender {
    Male("M"),
    Female("F"),
    Other("O");

    public static final Map<String, Gender> defaultStringToMap;
    public static final Map<Gender, String> defaultStringFromMap;

    static {
      Map<String, Gender> tempStringToMap = new HashMap<>();
      EnumMap<Gender, String> tempStringFromMap = new EnumMap<>(Gender.class);
      for (Gender gender : EnumSet.allOf(Gender.class)) {
        tempStringToMap.put(gender.code(), gender);
        tempStringFromMap.put(gender, gender.code());
      }
      defaultStringToMap = Collections.unmodifiableMap(tempStringToMap);
      defaultStringFromMap = Collections.unmodifiableMap(tempStringFromMap);
    }

    private final String code;

    Gender(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }
  }

  public enum MaritalStatus {
    Single("S"),
    Married("M"),
    Divorced("D"),
    Other("O");

    public static final Map<String, MaritalStatus> defaultStringToMap;
    public static final Map<MaritalStatus, String> defaultStringFromMap;

    static {
      Map<String, MaritalStatus> tempStringToMap = new HashMap<>();
      EnumMap<MaritalStatus, String> tempStringFromMap = new EnumMap<>(MaritalStatus.class);
      for (MaritalStatus maritalStatus : EnumSet.allOf(MaritalStatus.class)) {
        tempStringToMap.put(maritalStatus.code(), maritalStatus);
        tempStringFromMap.put(maritalStatus, maritalStatus.code());
      }
      defaultStringToMap = Collections.unmodifiableMap(tempStringToMap);
      defaultStringFromMap = Collections.unmodifiableMap(tempStringFromMap);
    }

    private final String code;

    MaritalStatus(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }
  }

  public enum ParamKey {
    pid,
    pname,
    sid,
    sname;
  }
}
