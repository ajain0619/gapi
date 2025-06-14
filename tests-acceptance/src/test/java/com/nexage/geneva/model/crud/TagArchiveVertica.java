package com.nexage.geneva.model.crud;

public class TagArchiveVertica {

  private Long tagPid;
  private String tagName;
  private String tagStatus;
  private String tagOwner;
  private Long sitePid;
  private Long buyerPid;
  private String siteNameAlias;
  private String tagId;
  private Integer monetization;
  private Long exchSiteTagPid;
  private String positionName;

  public Long getTagPid() {
    return tagPid;
  }

  public void setTagPid(Long tagPid) {
    this.tagPid = tagPid;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public String getTagStatus() {
    return tagStatus;
  }

  public void setTagStatus(String tagStatus) {
    this.tagStatus = tagStatus;
  }

  public String getTagOwner() {
    return tagOwner;
  }

  public void setTagOwner(String tagOwner) {
    this.tagOwner = tagOwner;
  }

  public Long getSitePid() {
    return sitePid;
  }

  public void setSitePid(Long sitePid) {
    this.sitePid = sitePid;
  }

  public Long getBuyerPid() {
    return buyerPid;
  }

  public void setBuyerPid(Long buyerPid) {
    this.buyerPid = buyerPid;
  }

  public String getSiteNameAlias() {
    return siteNameAlias;
  }

  public void setSiteNameAlias(String siteNameAlias) {
    this.siteNameAlias = siteNameAlias;
  }

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }

  public Integer getMonetization() {
    return monetization;
  }

  public void setMonetization(Integer monetization) {
    this.monetization = monetization;
  }

  public Long getExchSiteTagPid() {
    return exchSiteTagPid;
  }

  public void setExchSiteTagPid(Long exchSiteTagPid) {
    this.exchSiteTagPid = exchSiteTagPid;
  }

  public String getPositionName() {
    return positionName;
  }

  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }
}
