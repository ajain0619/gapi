package com.nexage.admin.core.projections;

import com.ssp.geneva.common.model.inventory.CompanyType;

public interface CompanySearchSummaryProjection {
  long getPid();

  String getName();

  CompanyType getType();

  Boolean getDefaultRtbProfilesEnabled();

  String getCurrency();

  Integer getSellerSeat_Pid();
}
