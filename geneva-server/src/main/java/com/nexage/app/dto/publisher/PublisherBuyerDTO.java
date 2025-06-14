package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class PublisherBuyerDTO {

  private Long pid;
  private Integer version;
  private String name;
  private Boolean exchange;
  private String primaryId;
  private String primaryName;
  private String secondaryId;
  private String secondaryName;
  private String primaryIdDefault;
  private String primaryNameDefault;
  private String secondaryIdDefault;
  private String secondaryNameDefault;
  private boolean primaryIdRequired;
  private boolean primaryNameRequired;
  private boolean secondaryIdRequired;
  private boolean secondaryNameRequired;
  private Map<String, String> paramMetadata;
  private AuthenticationType authenticationType;
  private SelfServeEnablement selfServeEnablement;
  private BidEnabled bidEnabled;
  private DecisionMakerEnabled decisionMakerEnabled;
  private String logoUrl;

  public PublisherBuyerDTO() {}

  private PublisherBuyerDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.name = builder.name;
    this.exchange = builder.exchange;
    this.primaryId = builder.primaryId;
    this.primaryName = builder.primaryName;
    this.secondaryId = builder.secondaryId;
    this.secondaryName = builder.secondaryName;

    this.primaryIdDefault = builder.primaryIdDefault;
    this.primaryNameDefault = builder.primaryNameDefault;
    this.secondaryIdDefault = builder.secondaryIdDefault;
    this.secondaryNameDefault = builder.secondaryNameDefault;

    this.primaryIdRequired = builder.primaryIdRequired;
    this.primaryNameRequired = builder.primaryNameRequired;
    this.secondaryIdRequired = builder.secondaryIdRequired;
    this.secondaryNameRequired = builder.secondaryNameRequired;
    this.paramMetadata = builder.paramMetadata;
    this.authenticationType = builder.authenticationType;
    this.selfServeEnablement = builder.selfServeEnablement;
    this.bidEnabled = builder.bidEnabled;
    this.decisionMakerEnabled = builder.decisionMakerEnabled;
    this.logoUrl = builder.logoUrl;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private String name;
    private Boolean exchange;

    private String primaryId;
    private String primaryName;
    private String secondaryId;
    private String secondaryName;

    private String primaryIdDefault;
    private String primaryNameDefault;
    private String secondaryIdDefault;
    private String secondaryNameDefault;

    private boolean primaryIdRequired;
    private boolean primaryNameRequired;
    private boolean secondaryIdRequired;
    private boolean secondaryNameRequired;
    private Map<String, String> paramMetadata;
    private AuthenticationType authenticationType;
    private SelfServeEnablement selfServeEnablement;
    private BidEnabled bidEnabled;
    private DecisionMakerEnabled decisionMakerEnabled;
    private String logoUrl;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withExchange(Boolean exchange) {
      this.exchange = exchange;
      return this;
    }

    public Builder withPrimaryId(String primaryId) {
      this.primaryId = primaryId;
      return this;
    }

    public Builder withPrimaryName(String primaryName) {
      this.primaryName = primaryName;
      return this;
    }

    public Builder withSecondaryId(String secondaryId) {
      this.secondaryId = secondaryId;
      return this;
    }

    public Builder withSecondaryName(String secondaryName) {
      this.secondaryName = secondaryName;
      return this;
    }

    public PublisherBuyerDTO build() {
      return new PublisherBuyerDTO(this);
    }

    public Builder withPrimaryIdDefault(String primaryIdDefault) {
      this.primaryIdDefault = primaryIdDefault;
      return this;
    }

    public Builder withPrimaryNameDefault(String primaryNameDefault) {
      this.primaryNameDefault = primaryNameDefault;
      return this;
    }

    public Builder withSecondaryIdDefault(String secondaryIdDefault) {
      this.secondaryIdDefault = secondaryIdDefault;
      return this;
    }

    public Builder withSecondaryNameDefault(String secondaryNameDefault) {
      this.secondaryNameDefault = secondaryNameDefault;
      return this;
    }

    public Builder withPrimaryIdRequired(boolean primaryIdRequired) {
      this.primaryIdRequired = primaryIdRequired;
      return this;
    }

    public Builder withPrimaryNameRequired(boolean primaryNameRequired) {
      this.primaryNameRequired = primaryNameRequired;
      return this;
    }

    public Builder withSecondaryIdRequired(boolean secondaryIdRequired) {
      this.secondaryIdRequired = secondaryIdRequired;
      return this;
    }

    public Builder withSecondaryNameRequired(boolean secondaryNameRequired) {
      this.secondaryNameRequired = secondaryNameRequired;
      return this;
    }

    public Builder withParamMetadata(Map<String, String> paramMetadata) {
      this.paramMetadata = paramMetadata;
      return this;
    }

    public Builder withAuthenticationType(AuthenticationType authType) {
      this.authenticationType = authType;
      return this;
    }

    public Builder withSelfServeEnablement(SelfServeEnablement selfServeEn) {
      this.selfServeEnablement = selfServeEn;
      return this;
    }

    public Builder withBidEnabled(BidEnabled bidEn) {
      this.bidEnabled = bidEn;
      return this;
    }

    public Builder withDecisonMakerEnabled(DecisionMakerEnabled decisionMakerEn) {
      this.decisionMakerEnabled = decisionMakerEn;
      return this;
    }

    public Builder withLogoUrl(String logoUrl) {
      this.logoUrl = logoUrl;
      return this;
    }
  }

  public enum AuthenticationType {
    NONE(0),
    USERNAME_PASSWORD(1),
    APIKEY(2),
    APITOKEN(3),
    COMBINATION(4);

    int code;

    private AuthenticationType(int code) {
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

  public enum SelfServeEnablement {
    NONE(0),
    ENABLED(1),
    ADDITIONAL(2);

    int code;

    private SelfServeEnablement(int code) {
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

  public enum BidEnabled {
    NO(0),
    YES(1);

    int code;

    BidEnabled(int code) {
      this.code = code;
    }

    public int asInt() {
      return code;
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

    private DecisionMakerEnabled(int code) {
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
}
