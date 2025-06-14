package com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue;

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
public class MediationAndRTBRevenueReport
    extends AbstractVelocityReport<MediationAndRTBRevenueRequest, MediationAndRTBRevenueResponse> {

  @Value("#{mediationAndRTBRevenueQueries}")
  private Map<String, String> queries;

  @Override
  public List<MediationAndRTBRevenueResponse> getReportData(MediationAndRTBRevenueRequest request)
      throws ReportException, DataAccessException {
    List<MediationAndRTBRevenueResponse> reportData =
        getReportData(
            request,
            queries.get(ReportKeys.DEFAULT).trim(),
            new BeanPropertyRowMapper<>(MediationAndRTBRevenueResponse.class));
    return postProcess(reportData);
  }

  private List<MediationAndRTBRevenueResponse> postProcess(
      List<MediationAndRTBRevenueResponse> reportData) {
    reportData.forEach(MediationAndRTBRevenueResponse::roundValues);
    return reportData;
  }
}
