package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nexage.admin.core.model.BaseTarget.RuleType;
import com.nexage.admin.core.model.BaseTarget.TargetType;

@JsonInclude(Include.NON_NULL)
public class DealTargetDTO {

  private Long pid;
  private String data;
  private String paramName;
  private RuleType ruleType;
  private TargetType targetType;

  public DealTargetDTO() {}

  private DealTargetDTO(Builder builder) {
    this.pid = builder.pid;
    this.data = builder.data;
    this.paramName = builder.paramName;
    this.ruleType = builder.ruleType;
    this.targetType = builder.targetType;
  }

  public Long getPid() {
    return pid;
  }

  public String getData() {
    return data;
  }

  public String getParamName() {
    return paramName;
  }

  public RuleType getRuleType() {
    return ruleType;
  }

  public TargetType getTargetType() {
    return targetType;
  }

  public static final class Builder {
    private Long pid;
    private String data;
    private String paramName;
    private RuleType ruleType;
    private TargetType targetType;

    public Builder setPid(long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setData(String target) {
      this.data = target;
      return this;
    }

    public Builder setParamName(String paramName) {
      this.paramName = paramName;
      return this;
    }

    public Builder setRuleType(RuleType ruleType) {
      this.ruleType = ruleType;
      return this;
    }

    public Builder setTargetType(TargetType targetType) {
      this.targetType = targetType;
      return this;
    }

    public DealTargetDTO build() {
      return new DealTargetDTO(this);
    }
  }
}
