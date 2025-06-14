package com.ssp.geneva.common.model.report;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ReportTypeTest {

  @Test
  void shouldReturnExpectedType() {
    ReportType reportType = ReportType.getReportTypeFromId("a3d17694f4f711e4b0e5d4bed9f6c105");
    assertAll(
        () -> assertNotNull(reportType), () -> assertEquals(ReportType.BIDDER_SPEND, reportType));
  }

  @Test
  void shouldNotReturnExpectedType() {
    ReportType reportType = ReportType.getReportTypeFromId("banana");
    assertNull(reportType);
  }

  @Test
  void shouldReturnExpectedUrl() {
    ReportType reportType = ReportType.getReportTypeFromURL("selleranalytics");
    assertAll(
        () -> assertNotNull(reportType),
        () -> assertEquals(ReportType.ANALYTICS_BY_SITE, reportType));
  }

  @Test
  void shouldNotReturnExpectedUrl() {
    ReportType reportType = ReportType.getReportTypeFromURL("banana");
    assertNull(reportType);
  }
}
