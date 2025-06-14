package com.ssp.geneva.server.report.report.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ssp.geneva.server.report.report.ReportDimension;
import com.ssp.geneva.server.report.report.ReportMetadata;
import com.ssp.geneva.server.report.report.ReportRequest;
import com.ssp.geneva.server.report.report.ReportUser;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateUtilTest {

  @Test
  void testGetZeroDatesMap() {
    runGetZeroDatesMapTest(
        "2016-11-24 16:43:05",
        "2017-01-13 11:01:56",
        new String[] {"2016-11-01", "2016-12-01", "2017-01-01"});
    runGetZeroDatesMapTest(
        "2016-10-10 10:15:20", "2016-10-29 14:03:22", new String[] {"2016-10-01"});
  }

  @Test
  void checkDateInThePast() {
    Date pastDate = Date.from(ZonedDateTime.now().minusDays(5).toInstant());
    Date futureDate = Date.from(ZonedDateTime.now().plusDays(5).toInstant());

    Assertions.assertTrue(DateUtil.isInPast(pastDate));
    assertFalse(DateUtil.isInPast(futureDate));
    assertFalse(DateUtil.isInPast(null));
  }

  private void runGetZeroDatesMapTest(String start, String end, String[] expectedDates) {
    ReportRequest request = createReportRequest(start, end);
    ReportDimension dimension = TestReportDimension.month;

    Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, dimension);

    assertEquals(expectedDates.length, dateMap.size());
    for (String expectedDate : expectedDates) {
      assertTrue(dateMap.containsKey(expectedDate));
    }
  }

  private ReportRequest createReportRequest(String start, String end) {
    return new ReportRequest() {
      @Override
      public String getStart() {
        return start;
      }

      @Override
      public String getStop() {
        return end;
      }

      @Override
      public ReportUser getReportUser() {
        return null;
      }

      @Override
      public ReportMetadata getReportMetadata() {
        return null;
      }
    };
  }

  private enum TestReportDimension implements ReportDimension {
    month;

    @Override
    public TestReportDimension getDimension() {
      return this;
    }

    @Override
    public String getName() {
      return this.name();
    }
  }
}
