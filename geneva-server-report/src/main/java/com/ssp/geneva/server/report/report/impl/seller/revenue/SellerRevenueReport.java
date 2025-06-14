package com.ssp.geneva.server.report.report.impl.seller.revenue;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SellerRevenueReport
    extends AbstractVelocityReport<SellerRevenueRequest, SellerRevenueResponse> {

  private final SellerRevenueReportDelegate sellerRevenueReportDelegate;

  @Value("#{sellerRevenueQueries}")
  private Map<String, String> queries;

  @Override
  protected Map<String, Object> getParameters(SellerRevenueRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    if (request.getSiteIds() != null) {
      parameters.remove(ReportKeys.COMPANY);
    }
    return parameters;
  }

  @Override
  public List<SellerRevenueResponse> getReportData(SellerRevenueRequest request)
      throws ReportException, DataAccessException {
    sellerRevenueReportDelegate.preProcess(request);
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("Unable to find report");
    }

    List<SellerRevenueResponse> reportData =
        getReportData(request, sql, new BeanPropertyRowMapper<>(SellerRevenueResponse.class));
    return sellerRevenueReportDelegate.postProcess(reportData, request);
  }
}
