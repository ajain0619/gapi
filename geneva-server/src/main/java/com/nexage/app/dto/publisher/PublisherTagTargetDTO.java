package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PublisherTagTargetDTO {

  public enum TargetType {
    Keyword,
    NegKeyword,
    Regex,
    NegRegex;
  }

  public enum RuleType {
    UserAgent,
    Country,
    URL,
    KeywordParam,
    ReqParam,
    DeviceModel,
    DeviceMake,
    ISPCarrier,
    DMA;
  }

  private Long pid;
  private Integer version;
  private String target;
  private String paramName;
  private TargetType targetType;
  private RuleType ruleType;

  private PublisherTagTargetDTO() {}

  private PublisherTagTargetDTO(Builder builder) {
    this.pid = builder.pid;
    this.version = builder.version;
    this.target = builder.target;
    this.paramName = builder.paramName;
    this.targetType = builder.targetType;
    this.ruleType = builder.ruleType;
  }

  public Long getPid() {
    return pid;
  }

  public Integer getVersion() {
    return version;
  }

  public String getTarget() {
    return target;
  }

  public String getParamName() {
    return paramName;
  }

  public TargetType getTargetType() {
    return targetType;
  }

  public RuleType getRuleType() {
    return ruleType;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Long pid;
    private Integer version;
    private String target;
    private String paramName;
    private TargetType targetType;
    private RuleType ruleType;

    public Builder withPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder withVersion(Integer version) {
      this.version = version;
      return this;
    }

    public Builder withTarget(String target) {
      this.target = target;
      return this;
    }

    public Builder withParamName(String paramName) {
      this.paramName = paramName;
      return this;
    }

    public Builder withTargetType(TargetType targetType) {
      this.targetType = targetType;
      return this;
    }

    public Builder withRuleType(RuleType ruleType) {
      this.ruleType = ruleType;
      return this;
    }

    public PublisherTagTargetDTO build() {
      return new PublisherTagTargetDTO(this);
    }
  }
}
