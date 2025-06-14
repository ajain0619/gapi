package com.nexage.app.dto;

import java.io.Serializable;

public class SellerReportRequestDTO extends BaseReportRequestDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer site;
  private String position;
  private Long adsource;
  private Long tag;
  private String country;
  private String make;
  private String model;
  private String advertiser;
  private Integer campaignType;
  private Long campaign;
  private String creative;
  private Long bidder;
  private String seat;
  private String adomain;
  private Long company;
  private String group;
  private Integer sourceTypeId;
  private String deal;
  private String buyer;

  private Long seller;

  public Integer getSite() {
    return site;
  }

  public void setSite(Integer site) {
    this.site = site;
    addToFilterParams(FilterParam.site.getDwAlias(), site);
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
    addToFilterParams(FilterParam.position.getDwAlias(), position);
  }

  public Long getAdsource() {
    return adsource;
  }

  public void setAdsource(Long adsource) {
    this.adsource = adsource;
    addToFilterParams(FilterParam.adsource.getDwAlias(), adsource);
  }

  public Long getTag() {
    return tag;
  }

  public void setTag(Long tag) {
    this.tag = tag;
    addToFilterParams(FilterParam.tag.getDwAlias(), tag);
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
    addToFilterParams(FilterParam.country.getDwAlias(), country);
  }

  public String getMake() {
    return make;
  }

  public void setMake(String make) {
    this.make = make;
    addToFilterParams(FilterParam.make.getDwAlias(), make);
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
    addToFilterParams(FilterParam.model.getDwAlias(), model);
  }

  public String getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(String advertiser) {
    this.advertiser = advertiser;
    addToFilterParams(FilterParam.advertiser.getDwAlias(), advertiser);
  }

  public Integer getCampaignType() {
    return campaignType;
  }

  public void setCampaignType(Integer campaignType) {
    this.campaignType = campaignType;
    addToFilterParams(FilterParam.campaignType.getDwAlias(), campaignType);
  }

  public Long getCampaign() {
    return campaign;
  }

  public void setCampaign(Long campaign) {
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

  public Long getBidder() {
    return bidder;
  }

  public void setBidder(Long bidder) {
    this.bidder = bidder;
    addToFilterParams(FilterParam.bidder.getDwAlias(), bidder);
  }

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
    addToFilterParams(FilterParam.seat.getDwAlias(), seat);
  }

  public String getAdomain() {
    return adomain;
  }

  public void setAdomain(String adomain) {
    this.adomain = adomain;
    addToFilterParams(FilterParam.adomain.getDwAlias(), adomain);
  }

  public Long getCompany() {
    return company;
  }

  public void setCompany(Long company) {
    this.company = company;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
    addToFilterParams(FilterParam.group.getDwAlias(), group);
  }

  public Integer getSourceTypeId() {
    return sourceTypeId;
  }

  public void setSourceTypeId(Integer sourceTypeId) {
    this.sourceTypeId = sourceTypeId;
    addToFilterParams(FilterParam.adSourceTypeId.getDwAlias(), sourceTypeId);
  }

  public Integer getAdSourceTypeId() {
    return sourceTypeId;
  }

  public void setAdSourceTypeId(Integer sourceTypeId) {
    this.sourceTypeId = sourceTypeId;
    addToFilterParams(FilterParam.adSourceTypeId.getDwAlias(), sourceTypeId);
  }

  public String getDeal() {
    return deal;
  }

  public void setDeal(String deal) {
    this.deal = deal;
    addToFilterParams(FilterParam.deal.getDwAlias(), deal);
  }

  public String getBuyer() {
    return buyer;
  }

  public void setBuyer(String buyer) {
    this.buyer = buyer;
    addToFilterParams("buyerBidderId", buyer);
  }

  public Long getSeller() {
    return seller;
  }

  public void setSeller(Long seller) {
    this.seller = seller;
    addToFilterParams(FilterParam.seller.getDwAlias(), seller);
  }

  @Override
  public void setDim(FilterParam dim) {
    // "buyer" drill down in Seller RTB Revenue Performance Report is translated to "buyerBidder"
    if (dim == FilterParam.buyer) {
      dim = FilterParam.buyerBidder;
    }
    super.setDim(dim);
  }
}
