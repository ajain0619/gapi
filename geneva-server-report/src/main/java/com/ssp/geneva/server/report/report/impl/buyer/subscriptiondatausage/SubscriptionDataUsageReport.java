package com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionDataUsageReport
    extends AbstractVelocityReport<SubscriptionDataUsageRequest, SubscriptionDataUsageResponse> {

  @Value("#{subscriptionDataUsageQueries}")
  private Map<String, String> queries;

  private List<SubscriptionDataUsageResponse> postProcess(
      List<SubscriptionDataUsageResponse> results) {
    clearResultIfContainsNullAggregation(results);
    results.stream().forEach(SubscriptionDataUsageResponse::postInitialize);
    return results;
  }

  private void clearResultIfContainsNullAggregation(List<SubscriptionDataUsageResponse> results) {
    if (results.size() == 1 && results.get(0).getOverallBidRequests() == null) {
      results.remove(0);
    }
  }

  @Override
  public List<SubscriptionDataUsageResponse> getReportData(SubscriptionDataUsageRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    return postProcess(
        getReportData(
            request, sql, new BeanPropertyRowMapper<>(SubscriptionDataUsageResponse.class)));
  }
}
