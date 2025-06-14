package com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance;

import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

public interface RtbRevenuePerformanceRequest extends ReportRequest {

  RtbRevenuePerformanceDimension getDim();

  void setDim(RtbRevenuePerformanceDimension dim);

  Long getSite();

  void setSite(Long site);

  Set<Long> getSiteIds();

  void setSiteIds(Set<Long> siteIds);

  String getPosition();

  void setPosition(String position);

  Long getTag();

  void setTag(Long tag);

  String getCountry();

  void setCountry(String country);

  Long getBidder();

  void setBidder(Long bidder);

  Long getBuyer();

  void setBuyer(Long buyer);

  String getAdomain();

  void setAdomain(String adomain);

  String getSeat();

  void setSeat(String seat);

  String getDeal();

  void setDeal(String deal);

  Long getCompany();

  void setCompany(Long company);

  boolean isDealAsNullParam();

  void setDealAsNullParam(boolean dealAsNullParam);

  Long getS2sHbPartner();

  void setS2sHbPartner(Long s2sHbPartner);
}
