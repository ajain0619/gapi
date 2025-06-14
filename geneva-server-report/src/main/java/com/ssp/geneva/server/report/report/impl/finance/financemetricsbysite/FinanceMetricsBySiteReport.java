package com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite;

import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceMetricsBySiteReport
    extends AbstractVelocityReport<FinanceMetricsBySiteRequest, FinanceMetricsBySiteResponse> {

  private final Map<String, String> queries;
  private final PhoneCastConfigService phoneCastConfigService;

  public FinanceMetricsBySiteReport(
      @Value("#{financeMetricsBySiteQueries}") Map<String, String> queries,
      PhoneCastConfigService phoneCastConfigService) {
    this.queries = queries;
    this.phoneCastConfigService = phoneCastConfigService;
  }

  @Override
  protected Map<String, Object> getParameters(FinanceMetricsBySiteRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    List<String> exchangeIds = phoneCastConfigService.getExchangeIdsAsList();
    parameters.put(ReportKeys.EXCHANGE_IDS, exchangeIds);
    return parameters;
  }

  @Override
  public List<FinanceMetricsBySiteResponse> getReportData(FinanceMetricsBySiteRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(ReportKeys.DEFAULT);
    if (sql == null) {
      throw new ReportException("Unable to find report");
    }
    return getReportData(
        request, sql.trim(), new BeanPropertyRowMapper<>(FinanceMetricsBySiteResponse.class));
  }
}
