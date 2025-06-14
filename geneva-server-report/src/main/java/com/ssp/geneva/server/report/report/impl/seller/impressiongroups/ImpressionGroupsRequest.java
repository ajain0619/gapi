package com.ssp.geneva.server.report.report.impl.seller.impressiongroups;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportRequest;
import java.util.Set;

public interface ImpressionGroupsRequest extends ReportRequest {

  String getSite();

  String getGroup();

  ReportDimension getDim();

  Long getCompany();

  Set<Long> getSiteIds();

  void setCompany(Long company);

  void setGroup(String group);

  void setSiteIds(Set<Long> siteIds);
}
