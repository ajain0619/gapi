package com.ssp.geneva.server.report.report.impl.seller.adserver;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

/** Interface for Ad Server report request. */
public interface AdServerRequest extends ReportRequest {

  ReportDimension getDim();

  Long getSite();

  String getPosition();

  Long getTag();

  Long getCompany();

  Long getAdvertiser();

  Long getCampaignType();

  Long getCampaign();

  Long getCreative();

  Set<Long> getSiteIds();

  void setCompany(Long company);

  void setSiteIds(Set<Long> siteIds);
}
