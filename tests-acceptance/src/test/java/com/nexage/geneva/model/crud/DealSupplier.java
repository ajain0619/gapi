package com.nexage.geneva.model.crud;

import java.util.List;

/** Created by seanryan on 23/02/2016. */
public class DealSupplier {

  private String pid;
  private String rtbProfilePid;
  private String description;
  private String auctionType;
  private String defaultReserve;
  private String lowReserve;
  private String pubAlias;
  private String siteAlias;
  private String pubNameAlias;
  private String siteNameAlias;
  private String siteType;
  private String isRealName;
  private String siteId;
  private String siteName;
  private String pubId;
  private String pubName;
  private String tagPid;
  private List<String> categories;
  private List<DealSupplierCountry> countries;
  private String platform;
  private String videoSupport;
  private String height;
  private String width;
  private String placementType;
  private String placementName;

  public String getAuctionType() {
    return auctionType;
  }

  public void setAuctionType(String auctionType) {
    this.auctionType = auctionType;
  }

  public List getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public List getCountries() {
    return countries;
  }

  public void setCountries(List<DealSupplierCountry> countries) {
    this.countries = countries;
  }

  public String getDefaultReserve() {
    return defaultReserve;
  }

  public void setDefaultReserve(String defaultReserve) {
    this.defaultReserve = defaultReserve;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getIsRealName() {
    return isRealName;
  }

  public void setIsRealName(String isRealName) {
    this.isRealName = isRealName;
  }

  public String getLowReserve() {
    return lowReserve;
  }

  public void setLowReserve(String lowReserve) {
    this.lowReserve = lowReserve;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getPlacementType() {
    return placementType;
  }

  public void setPlacementType(String placementType) {
    this.placementType = placementType;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getPubAlias() {
    return pubAlias;
  }

  public void setPubAlias(String pubAlias) {
    this.pubAlias = pubAlias;
  }

  public String getPubId() {
    return pubId;
  }

  public void setPubId(String pubId) {
    this.pubId = pubId;
  }

  public String getPubName() {
    return pubName;
  }

  public void setPubName(String pubName) {
    this.pubName = pubName;
  }

  public String getPubNameAlias() {
    return pubNameAlias;
  }

  public void setPubNameAlias(String pubNameAlias) {
    this.pubNameAlias = pubNameAlias;
  }

  public String getRtbProfilePid() {
    return rtbProfilePid;
  }

  public void setRtbProfilePid(String rtbProfilePid) {
    this.rtbProfilePid = rtbProfilePid;
  }

  public String getSiteAlias() {
    return siteAlias;
  }

  public void setSiteAlias(String siteAlias) {
    this.siteAlias = siteAlias;
  }

  public String getSiteId() {
    return siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public String getSiteNameAlias() {
    return siteNameAlias;
  }

  public void setSiteNameAlias(String siteNameAlias) {
    this.siteNameAlias = siteNameAlias;
  }

  public String getSiteType() {
    return siteType;
  }

  public void setSiteType(String siteType) {
    this.siteType = siteType;
  }

  public String getTagPid() {
    return tagPid;
  }

  public void setTagPid(String tagPid) {
    this.tagPid = tagPid;
  }

  public String getVideoSupport() {
    return videoSupport;
  }

  public void setVideoSupport(String videoSupport) {
    this.videoSupport = videoSupport;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(String width) {
    this.width = width;
  }

  public String getPlacementName() {
    return placementName;
  }

  public void setPlacementName(String placementName) {
    this.placementName = placementName;
  }
}
