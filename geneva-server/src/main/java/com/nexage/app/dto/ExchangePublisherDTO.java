package com.nexage.app.dto;

public class ExchangePublisherDTO {

  private final Long pid;
  private final String name;
  private final String globalAliasName;

  public ExchangePublisherDTO(Long pid, String name, String globalAliasName) {
    this.pid = pid;
    this.name = name;
    this.globalAliasName = globalAliasName;
  }

  public Long getPid() {
    return pid;
  }

  public String getName() {
    return name;
  }

  public String getGlobalAliasName() {
    return globalAliasName;
  }
}
