package com.ssp.geneva.server.report.report.impl.seller.traffic;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

/** Interface for Traffic report request. */
public interface TrafficRequest extends ReportRequest {

  ReportDimension getDim();

  Long getCompany();

  Integer getSite();

  String getPosition();

  Long getAdsource();

  Long getTag();

  Long getHeaderBidding();

  Set<Long> getSiteIds();

  Long getS2sHbPartner();

  void setCompany(Long company);

  void setSiteIds(Set<Long> siteIds);

  void setHeaderBidding(Long headerBidding);

  void setS2sHbPartner(Long s2sHbPartner);
}
