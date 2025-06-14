package com.ssp.geneva.server.report.report.impl.finance.adservingrevenue;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

/** Repository to retrieve Ad Serving Revenue Report data. */
@Repository
public class AdServingRevenueReport
    extends AbstractVelocityReport<AdServingRevenueRequest, AdServingRevenueResponse> {

  @Value("#{adServingRevenueQueries}")
  private Map<String, String> queries;

  @Override
  public List<AdServingRevenueResponse> getReportData(AdServingRevenueRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(ReportKeys.DEFAULT);
    if (sql == null) {
      throw new ReportException("Unable to find report");
    }
    List<AdServingRevenueResponse> baseReportData =
        getReportData(request, sql, new BeanPropertyRowMapper<>(AdServingRevenueResponse.class));
    return postProcess(baseReportData);
  }

  private List<AdServingRevenueResponse> postProcess(List<AdServingRevenueResponse> baseReportData)
      throws ReportException {
    String feesSql = queries.get(ReportKeys.COMPANY_FEES_DATA);
    if (feesSql == null) {
      throw new ReportException("Unable to find report");
    }
    Map<String, Object> parameters = new HashMap<>();
    List<AdServingRevenueResponse> reportData = new ArrayList<>();
    for (AdServingRevenueResponse baseReportRow : baseReportData) {
      getCompanyFeesData(feesSql, parameters, baseReportRow);
      buildReportData(reportData, baseReportRow);
    }
    return reportData;
  }

  @SuppressWarnings("unchecked")
  private void getCompanyFeesData(
      String feesSql, Map<String, Object> parameters, AdServingRevenueResponse baseReportRow) {
    parameters.put(ReportKeys.COMPANY, baseReportRow.getCompanyId());
    coreNamedJdbcTemplate.queryForObject(
        feesSql,
        parameters,
        (rs, rowNumber) -> {
          baseReportRow.setDirectFee(rs.getBigDecimal(ReportKeys.DIRECT_FEE));
          baseReportRow.setHouseFee(rs.getBigDecimal(ReportKeys.HOUSE_FEE));
          baseReportRow.setHouseOverageFee(rs.getBigDecimal(ReportKeys.HOUSE_OVERAGE_FEE));
          baseReportRow.setCap(rs.getBigDecimal(ReportKeys.CAP));
          return baseReportRow;
        });
  }

  private void buildReportData(
      List<AdServingRevenueResponse> reportData, AdServingRevenueResponse reportRow) {
    reportData.add(reportRow.convertToDirectSold());
    reportData.add(reportRow.convertToPremiumHouseAllowed());
    reportData.add(reportRow.convertToRemnantHouse());
    reportData.add(reportRow.convertToPremiumHouseOver());
  }
}
