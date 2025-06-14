package com.ssp.geneva.server.report.report.impl.finance.adservingrevenue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssp.geneva.server.report.report.ReportResponse;
import com.ssp.geneva.server.report.report.ResponseMetadata;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** DTO for one row of Ad Serving Revenue report. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdServingRevenueResponse implements ReportResponse {

  @JsonIgnore private long companyId;
  @JsonIgnore private long directSold;
  @JsonIgnore private long remnantHouse;
  @JsonIgnore private long premiumHouse;
  @JsonIgnore private BigDecimal directFee;
  @JsonIgnore private BigDecimal houseFee;
  @JsonIgnore private BigDecimal cap;
  @JsonIgnore private BigDecimal houseOverageFee;

  private String company;
  private String type;
  private long requests;
  private long impressions;

  @JsonProperty(value = "cpmfee")
  private BigDecimal cpmFee;

  @JsonProperty(value = "servingfee")
  private BigDecimal servingFee;

  @Override
  public ResponseMetadata getResponseMetadata() {
    // TODO Auto-generated method stub
    return null;
  }

  public AdServingRevenueResponse() {}

  private AdServingRevenueResponse(String company, CampaignType campaignType) {
    this.company = company;
    this.type = campaignType.getCampaignType();
  }

  AdServingRevenueResponse convertToDirectSold() {
    AdServingRevenueResponse response =
        new AdServingRevenueResponse(this.company, CampaignType.DIRECT_SOLD);
    response.setImpressions(this.directSold);
    response.setCpmFee(this.directFee);
    response.setServingFee(calculateServingFee(this.directSold, this.directFee));
    response.setRequests(this.requests);
    return response;
  }

  AdServingRevenueResponse convertToRemnantHouse() {
    AdServingRevenueResponse response =
        new AdServingRevenueResponse(this.company, CampaignType.REMNANT_HOUSE);
    response.setImpressions(this.remnantHouse);
    response.setCpmFee(this.houseFee);
    response.setServingFee(calculateServingFee(this.remnantHouse, this.houseFee));
    response.setRequests(this.requests);
    return response;
  }

  AdServingRevenueResponse convertToPremiumHouseAllowed() {
    AdServingRevenueResponse response =
        new AdServingRevenueResponse(this.company, CampaignType.PREMIUM_HOUSE_WITHIN_ALLOWANCE);
    long impressions = calculateAllowedImpressions();
    response.setImpressions(impressions);
    response.setCpmFee(this.houseFee);
    response.setServingFee(calculateServingFee(impressions, this.houseFee));
    response.setRequests(this.requests);
    return response;
  }

  AdServingRevenueResponse convertToPremiumHouseOver() {
    AdServingRevenueResponse response =
        new AdServingRevenueResponse(this.company, CampaignType.PREMIUM_HOUSE_OVERAGE);
    long impressions = this.premiumHouse - calculateAllowedImpressions();
    response.setImpressions(impressions);
    response.setCpmFee(this.houseOverageFee);
    response.setServingFee(calculateServingFee(impressions, this.houseOverageFee));
    response.setRequests(this.requests);
    return response;
  }

  private BigDecimal calculateServingFee(Long impressions, BigDecimal fee) {
    return fee.multiply(new BigDecimal(impressions))
        .divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP);
  }

  private long calculateAllowedImpressions() {
    return (long)
        Math.min(
            (double) this.premiumHouse,
            this.cap.divide(BigDecimal.valueOf(1000), RoundingMode.HALF_UP).doubleValue()
                * (double) this.requests);
  }

  public long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(long companyId) {
    this.companyId = companyId;
  }

  public long getDirectSold() {
    return directSold;
  }

  public void setDirectSold(long directSold) {
    this.directSold = directSold;
  }

  public long getRemnantHouse() {
    return remnantHouse;
  }

  public void setRemnantHouse(long remnantHouse) {
    this.remnantHouse = remnantHouse;
  }

  public long getPremiumHouse() {
    return premiumHouse;
  }

  public void setPremiumHouse(long premiumHouse) {
    this.premiumHouse = premiumHouse;
  }

  public BigDecimal getDirectFee() {
    return directFee;
  }

  public void setDirectFee(BigDecimal directFee) {
    this.directFee = directFee != null ? directFee : BigDecimal.ZERO;
  }

  public BigDecimal getHouseFee() {
    return houseFee;
  }

  public void setHouseFee(BigDecimal houseFee) {
    this.houseFee = houseFee != null ? houseFee : BigDecimal.ZERO;
  }

  public BigDecimal getCap() {
    return cap;
  }

  public void setCap(BigDecimal cap) {
    this.cap = cap != null ? cap : BigDecimal.ZERO;
  }

  public BigDecimal getHouseOverageFee() {
    return houseOverageFee;
  }

  public void setHouseOverageFee(BigDecimal houseOverageFee) {
    this.houseOverageFee = houseOverageFee != null ? houseOverageFee : BigDecimal.ZERO;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getRequests() {
    return requests;
  }

  public void setRequests(long requests) {
    this.requests = requests;
  }

  public long getImpressions() {
    return impressions;
  }

  public void setImpressions(long impressions) {
    this.impressions = impressions;
  }

  public BigDecimal getCpmFee() {
    return cpmFee;
  }

  public void setCpmFee(BigDecimal cpmFee) {
    this.cpmFee = cpmFee;
  }

  public BigDecimal getServingFee() {
    return servingFee;
  }

  public void setServingFee(BigDecimal servingFee) {
    this.servingFee = servingFee;
  }
}
