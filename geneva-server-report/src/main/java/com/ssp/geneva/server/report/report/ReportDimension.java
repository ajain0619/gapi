package com.ssp.geneva.server.report.report;

/** Interface for report dimensions */
public interface ReportDimension {

  /**
   * Get dimension instance
   *
   * @return dimension instance
   */
  ReportDimension getDimension();

  /**
   * Get dimension name
   *
   * @return dimension name
   */
  String getName();
}
