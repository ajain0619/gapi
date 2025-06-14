package com.nexage.app.dto;

import java.io.Serializable;

public class NexageReportRequest extends BaseReportRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long company;
  private Long site;
  private Long bidder;
  private Long buyer;
  private String app;
  private Long authority;
  private String campaign;
  private String creative;
  private String country;
  private String position;
  private Long tag;

  public Long getCompany() {
    return company;
  }

  public void setCompany(Long company) {
    this.company = company;
  }

  public Long getSite() {
    return site;
  }

  public void setSite(Long site) {
    this.site = site;
    addToFilterParams(FilterParam.site.getDwAlias(), site);
  }

  public Long getBuyer() {
    return buyer;
  }

  public void setBuyer(Long buyer) {
    this.buyer = buyer;
    addToFilterParams(FilterParam.buyer.getDwAlias(), buyer);
  }

  public Long getBidder() {
    return bidder;
  }

  public void setBidder(Long bidder) {
    this.bidder = bidder;
    addToFilterParams(FilterParam.bidder.getDwAlias(), bidder);
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
    addToFilterParams(FilterParam.app.getDwAlias(), app);
  }

  public Long getAuthority() {
    return authority;
  }

  public void setAuthority(Long authority) {
    this.authority = authority;
    addToFilterParams(FilterParam.authority.getDwAlias(), authority);
  }

  public String getCampaign() {
    return campaign;
  }

  public void setCampaign(String campaign) {
    this.campaign = campaign;
    addToFilterParams(FilterParam.campaign.getDwAlias(), campaign);
  }

  public String getCreative() {
    return creative;
  }

  public void setCreative(String creative) {
    this.creative = creative;
    addToFilterParams(FilterParam.creative.getDwAlias(), creative);
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
    addToFilterParams(FilterParam.country.getDwAlias(), country);
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
    addToFilterParams(FilterParam.position.getDwAlias(), position);
  }

  public Long getTag() {
    return tag;
  }

  public void setTag(Long tag) {
    this.tag = tag;
    addToFilterParams(FilterParam.tag.getDwAlias(), tag);
  }
}
