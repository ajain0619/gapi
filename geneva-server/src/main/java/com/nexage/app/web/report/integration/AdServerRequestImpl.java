package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerRequest;
import java.util.Set;

public class AdServerRequestImpl extends BaseReportRequest implements AdServerRequest {

  private AdServerReportDimension dim;
  private Long site;
  private String position;
  private Long tag;
  private Long company;
  private Long advertiser;
  private Long campaignType;
  private Long campaign;
  private Long creative;
  private Set<Long> siteIds;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "AD_SERVER";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  @Override
  public ReportDimension getDim() {
    return dim;
  }

  public void setDim(AdServerReportDimension dim) {
    this.dim = dim;
  }

  @Override
  public Long getSite() {
    return site;
  }

  public void setSite(Long site) {
    this.site = site;
  }

  @Override
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public Long getCompany() {
    return company;
  }

  @Override
  public void setCompany(Long company) {
    this.company = company;
  }

  @Override
  public Long getTag() {
    return tag;
  }

  public void setTag(Long tag) {
    this.tag = tag;
  }

  @Override
  public Long getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(Long advertiser) {
    this.advertiser = advertiser;
  }

  @Override
  public Long getCampaignType() {
    return campaignType;
  }

  public void setCampaignType(Long campaignType) {
    this.campaignType = campaignType;
  }

  @Override
  public Long getCampaign() {
    return campaign;
  }

  public void setCampaign(Long campaign) {
    this.campaign = campaign;
  }

  @Override
  public Long getCreative() {
    return creative;
  }

  public void setCreative(Long creative) {
    this.creative = creative;
  }

  @Override
  public Set<Long> getSiteIds() {
    return this.siteIds;
  }

  @Override
  public void setSiteIds(Set<Long> siteIds) {
    this.siteIds = siteIds;
  }
}
