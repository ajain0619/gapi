package com.ssp.geneva.server.report.report;

import java.util.Set;

public interface ReportUser {

  Long getCompany();

  Set<Long> getCompanies();

  String getUserName();

  Long getUserPid();

  boolean isNexageUser();
}
