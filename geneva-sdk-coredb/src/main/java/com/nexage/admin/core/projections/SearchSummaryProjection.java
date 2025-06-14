package com.nexage.admin.core.projections;

public interface SearchSummaryProjection {
  Long getSitePid();

  String getSiteName();

  Integer getSiteStatus();

  Integer getCompanyPid();

  String getCompanyName();
}
