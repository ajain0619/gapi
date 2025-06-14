package com.ssp.geneva.server.report.report.impl.seller.adserver;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.util.RestrictedAccessUtil;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AdServerReport extends AbstractVelocityReport<AdServerRequest, AdServerResponse> {

  @Value("#{adServerQueries}")
  private final Map<String, String> queries;

  private final RestrictedAccessUtil restrictedAccessUtil;

  @Override
  protected Map<String, Object> getParameters(AdServerRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    if (request.getSiteIds() != null) {
      parameters.remove(ReportKeys.COMPANY);
    }
    return parameters;
  }

  @Override
  public List<AdServerResponse> getReportData(AdServerRequest request)
      throws ReportException, DataAccessException {
    preProcess(request);
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    List<AdServerResponse> results =
        getReportData(request, sql, new BeanPropertyRowMapper<>(AdServerResponse.class));
    return postProcess(results, request);
  }

  private void preProcess(AdServerRequest request) {
    if (!request.getReportUser().isNexageUser()) {
      request.setCompany(request.getReportUser().getCompany());
    }

    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(
            request.getCompany(), request.getReportUser()));
  }

  private List<AdServerResponse> postProcess(
      List<AdServerResponse> reportResults, AdServerRequest reportRequest) {
    clearResultIfContainsNullAggregation(reportResults);
    if (reportRequest.getDim() != null) {
      fillZerosForMissedIntervals(reportResults, reportRequest);
    }
    return reportResults;
  }

  private void clearResultIfContainsNullAggregation(List<AdServerResponse> results) {
    if (results.size() == 1 && results.get(0).getAdDelivered() == null) {
      results.remove(0);
    }
  }

  private void fillZerosForMissedIntervals(
      List<AdServerResponse> results, AdServerRequest request) {
    if (request.getDim() != null) {
      String dimension = request.getDim().getName();
      if (ReportKeys.HOUR.equalsIgnoreCase(dimension)
          || ReportKeys.DAY.equalsIgnoreCase(dimension)
          || ReportKeys.WEEK.equals(dimension)
          || ReportKeys.MONTH.equals(dimension)) {
        Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());
        results.stream()
            .filter(reportRow -> reportRow.getInterval() != null)
            .forEach(
                response ->
                    dateMap.remove(
                        ReportKeys.HOUR.equalsIgnoreCase(dimension)
                            ? Integer.valueOf(response.getInterval().split(" ")[1].substring(0, 2))
                                .toString()
                            : response.getInterval().split(" ")[0]));
        results.addAll(
            dateMap.values().stream().map(AdServerResponse::new).collect(Collectors.toList()));
      }
    }
  }
}
