package com.ssp.geneva.server.report.report.impl.internal.rxperformance;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

/** Repository to retrieve RX_PERFORMANCE report data. */
@Repository
public class RxPerformanceReport
    extends AbstractVelocityReport<RxPerformanceRequest, RxPerformanceResponse> {

  @Value("#{rxPerformanceQueries}")
  private Map<String, String> queries;

  @Override
  public List<RxPerformanceResponse> getReportData(RxPerformanceRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(ReportKeys.DEFAULT);
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    return postProcess(
        getReportData(request, sql, new BeanPropertyRowMapper<>(RxPerformanceResponse.class)),
        request);
  }

  private List<RxPerformanceResponse> postProcess(
      List<RxPerformanceResponse> reportResults, RxPerformanceRequest reportRequest) {
    return reportResults;
  }
}
