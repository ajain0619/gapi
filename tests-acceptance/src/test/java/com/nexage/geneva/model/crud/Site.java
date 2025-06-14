package com.nexage.geneva.model.crud;

public class Site {
  private String domain;
  private String globalAliasName;
  private String id;
  private String integration;
  private String live;
  private String name;
  private String pid;
  private String platform;
  private String sellerName;
  private String sellerPid;
  private String status;
  private String type;
  private String url;
  private boolean adScreening;
  private boolean sendIds;
  private String dcn;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getGlobalAliasName() {
    return globalAliasName;
  }

  public void setGlobalAliasName(String globalAliasName) {
    this.globalAliasName = globalAliasName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIntegration() {
    return integration;
  }

  public void setIntegration(String integration) {
    this.integration = integration;
  }

  public String getLive() {
    return live;
  }

  public void setLive(String live) {
    this.live = live;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPid() {
    return pid;
  }

  public String getDcn() {
    return dcn;
  }

  public void setDcn(String dcn) {
    this.dcn = dcn;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getSellerName() {
    return sellerName;
  }

  public void setSellerName(String sellerName) {
    this.sellerName = sellerName;
  }

  public String getSellerPid() {
    return sellerPid;
  }

  public void setSellerPid(String sellerPid) {
    this.sellerPid = sellerPid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isAdScreening() {
    return adScreening;
  }

  public void setAdScreening(boolean adScreening) {
    this.adScreening = adScreening;
  }

  public boolean isSendIds() {
    return sendIds;
  }

  public void setSendIds(boolean sendIds) {
    this.sendIds = sendIds;
  }
}
