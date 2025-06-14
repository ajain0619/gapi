package com.nexage.app.dto;

import java.io.Serializable;

public class UserSiteRestrictionsDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private final Long pid;
  private final String url;
  private final String name;
  private final boolean restricted;

  public UserSiteRestrictionsDTO(Long pid, String url, String name, boolean restricted) {
    this.pid = pid;
    this.url = url;
    this.name = name;
    this.restricted = restricted;
  }

  public Long getPid() {
    return pid;
  }

  public String getUrl() {
    return url;
  }

  public String getName() {
    return name;
  }

  public boolean isRestricted() {
    return restricted;
  }
}
