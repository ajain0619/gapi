package com.nexage.app.web.report.integration;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsRequest;
import java.util.Set;

public class ImpressionGroupsRequestImpl extends BaseReportRequest
    implements ImpressionGroupsRequest {
  private String site;
  private String group;
  private ImpressionGroupsReportDimension dim;
  private Long company;
  private Set<Long> siteIds;

  private ReportMetadata reportMetadata =
      new ReportMetadata() {
        @Override
        public String getReportName() {
          return "IMPRESSION_GROUPS";
        }
      };

  @Override
  public ReportMetadata getReportMetadata() {
    return reportMetadata;
  }

  @Override
  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public void setGroup(String group) {
    this.group = group;
  }

  @Override
  public ReportDimension getDim() {
    return dim;
  }

  public void setDim(ImpressionGroupsReportDimension dim) {
    this.dim = dim;
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
  public Set<Long> getSiteIds() {
    return this.siteIds;
  }

  @Override
  public void setSiteIds(Set<Long> siteIds) {
    this.siteIds = siteIds;
  }
}
