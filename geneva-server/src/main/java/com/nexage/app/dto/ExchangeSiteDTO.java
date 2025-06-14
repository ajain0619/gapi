package com.nexage.app.dto;

public class ExchangeSiteDTO {

  private final Long pid;
  private final String name;
  private final String globalAliasName;
  private final Long publisherPid;

  public ExchangeSiteDTO(Long pid, String name, String globalAliasName, Long publisherPid) {
    this.pid = pid;
    this.name = name;
    this.globalAliasName = globalAliasName;
    this.publisherPid = publisherPid;
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

  public Long getPublisherPid() {
    return publisherPid;
  }
}
