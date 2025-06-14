package com.nexage.app.web.report.integration;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueRequest;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueRestrictedReportDimension;
import java.util.Set;

public class SellerRevenueRestrictedRequestImpl extends BaseReportRequest
    implements SellerRevenueRequest {

  private SellerRevenueRestrictedReportDimension dim;
  private Long site;
  private String position;
  private String country;
  private Long company;
  private Long sellerSeat;
  private Long adSourceTypeId;
  private Set<Long> siteIds;
  private Long headerBidding;
  @JsonIgnore private Set<Long> companies;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "SELLER_NET_REVENUE_RESTRICTED_DD";
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

  @Override
  public Long getSeller() {
    return null;
  }

  public void setDim(SellerRevenueRestrictedReportDimension dim) {
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

  @Override
  public Long getAdsource() {
    return null;
  }

  @Override
  public Long getTag() {
    return null;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Override
  public Long getCompany() {
    return company;
  }

  public void setCompany(Long company) {
    this.company = company;
    setCompanies(singleton(company));
  }

  @Override
  public void setCompanies(Set<Long> companies) {
    this.companies = companies;
  }

  @Override
  public Set<Long> getCompanies() {
    return companies != null ? companies : emptySet();
  }

  public void setSellerSeat(Long sellerSeat) {
    this.sellerSeat = sellerSeat;
  }

  @Override
  public Long getSellerSeat() {
    return sellerSeat;
  }

  @Override
  public Long getAdSourceTypeId() {
    return adSourceTypeId;
  }

  public void setAdSourceTypeId(Long adSourceTypeId) {
    this.adSourceTypeId = adSourceTypeId;
  }

  @Override
  public Set<Long> getSiteIds() {
    return this.siteIds;
  }

  @Override
  public void setSiteIds(Set<Long> siteIds) {
    this.siteIds = siteIds;
  }

  public Long getHeaderBidding() {
    return headerBidding;
  }

  public void setHeaderBidding(Long headerBidding) {
    this.headerBidding = headerBidding;
  }
}
