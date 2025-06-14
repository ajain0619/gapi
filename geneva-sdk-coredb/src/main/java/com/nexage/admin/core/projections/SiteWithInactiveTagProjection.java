package com.nexage.admin.core.projections;

public interface SiteWithInactiveTagProjection {
  Long getSitePid();

  Long getPositionPid();

  String getPositionName();

  Long getTierPid();

  Long getTagPid();
}
