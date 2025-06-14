package com.nexage.app.dto.deals;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealRuleDTO {

  private Long pid;
  private Long rulePid;

  public DealRuleDTO() {}

  public DealRuleDTO(Builder builder) {
    this.pid = builder.pid;
    this.rulePid = builder.rulePid;
  }

  public Long getPid() {
    return pid;
  }

  public Long getRulePid() {
    return rulePid;
  }

  public static final class Builder {

    private Long pid;
    private Long rulePid;

    public Builder setPid(Long pid) {
      this.pid = pid;
      return this;
    }

    public Builder setRulePid(Long rulePid) {
      this.rulePid = rulePid;
      return this;
    }

    public DealRuleDTO build() {
      return new DealRuleDTO(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rulePid == null) ? 0 : rulePid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DealRuleDTO other = (DealRuleDTO) obj;
    if (rulePid == null) {
      if (other.rulePid != null) return false;
    } else if (!rulePid.equals(other.rulePid)) return false;
    return true;
  }
}
