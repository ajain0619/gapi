package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

public interface SellerRevenueRequest extends ReportRequest {

  ReportDimension getDim();

  Long getSeller();

  Long getSite();

  String getPosition();

  Long getAdsource();

  Long getTag();

  String getCountry();

  Long getCompany();

  Long getAdSourceTypeId();

  Set<Long> getSiteIds();

  Long getHeaderBidding();

  void setCompany(Long company);

  void setCompanies(Set<Long> companies);

  Set<Long> getCompanies();

  Long getSellerSeat();

  void setPosition(String position);

  void setCountry(String country);

  void setSiteIds(Set<Long> siteIds);

  void setHeaderBidding(Long headerBidding);
}
