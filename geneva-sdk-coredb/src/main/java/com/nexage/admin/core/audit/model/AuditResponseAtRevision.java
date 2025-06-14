package com.nexage.admin.core.audit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Map;

public class AuditResponseAtRevision implements Serializable {

  private static final long serialVersionUID = 3318148848598370985L;

  @JsonIgnoreProperties(
      value = {"site", "tiers"},
      allowSetters = true)
  private Map<String, Object> before;

  @JsonIgnoreProperties(
      value = {"site", "tiers"},
      allowSetters = true)
  private Map<String, Object> after;

  public Map<String, Object> getBefore() {
    return before;
  }

  public void setBefore(Map<String, Object> before) {
    this.before = before;
  }

  public Map<String, Object> getAfter() {
    return after;
  }

  public void setAfter(Map<String, Object> after) {
    this.after = after;
  }
}
