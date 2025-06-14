package com.ssp.geneva.server.report.report;

public interface ReportRequest {

  String getStart();

  String getStop();

  ReportUser getReportUser();

  ReportMetadata getReportMetadata();
}
