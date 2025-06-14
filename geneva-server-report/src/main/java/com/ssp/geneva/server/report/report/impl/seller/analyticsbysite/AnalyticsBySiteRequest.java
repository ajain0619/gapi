package com.ssp.geneva.server.report.report.impl.seller.analyticsbysite;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

/** Interface for AnalyticsBySite report request. */
public interface AnalyticsBySiteRequest extends ReportRequest {

  Long getCompany();

  ReportDimension getDim();

  Long getSite();

  String getPosition();

  Long getAdsource();

  Long getTag();

  String getCountry();

  String getMake();

  String getModel();

  String getCarrier();

  Set<Long> getSiteIds();

  void setCompany(Long company);

  void setCountry(String country);

  void setMake(String make);

  void setModel(String model);

  void setCarrier(String carrier);

  void setSiteIds(Set<Long> siteIds);
}
