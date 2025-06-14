package com.ssp.geneva.server.report.report.impl.seller.traffic;

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
public class TrafficReport extends AbstractVelocityReport<TrafficRequest, TrafficResponse> {

  @Value("#{trafficQueries}")
  private Map<String, String> queries;

  protected final RestrictedAccessUtil restrictedAccessUtil;

  @Override
  protected Map<String, Object> getParameters(TrafficRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    if (request.getSiteIds() != null) {
      parameters.remove(ReportKeys.COMPANY);
    }
    parameters.put(ReportKeys.PARAMETER_IS_OUTBOUND_REQUEST, isOutboundRequest(request));
    return parameters;
  }

  @Override
  public List<TrafficResponse> getReportData(TrafficRequest request)
      throws ReportException, DataAccessException {
    preProcess(request);
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }

    return postProcess(
        getReportData(request, sql, new BeanPropertyRowMapper<>(TrafficResponse.class)), request);
  }

  private void preProcess(TrafficRequest request) {
    if (!request.getReportUser().isNexageUser()) {
      request.setCompany(request.getReportUser().getCompany());
    }

    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(
            request.getCompany(), request.getReportUser()));
  }

  private List<TrafficResponse> postProcess(
      List<TrafficResponse> reportResults, TrafficRequest reportRequest) {
    clearResultIfContainsNullAggregation(reportResults);
    fillMissingIntervalsWithZeroes(reportResults, reportRequest);
    Boolean isOutboundRequest = isOutboundRequest(reportRequest);
    for (TrafficResponse row : reportResults) {
      row.setOutboundRequest(isOutboundRequest);
    }
    return reportResults;
  }

  private void fillMissingIntervalsWithZeroes(
      List<TrafficResponse> results, TrafficRequest request) {
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
            dateMap.values().stream().map(TrafficResponse::new).collect(Collectors.toList()));
      }
    }
  }

  private void clearResultIfContainsNullAggregation(List<TrafficResponse> results) {
    if (results.size() == 1 && results.get(0).getAdDelivered() == null) {
      results.remove(0);
    }
  }

  /**
   * Returns whether this report request results in report that displays outbound requests.
   *
   * @param request report request
   * @return true if this report request results in report that displays outbound requests, else
   *     false
   */
  private boolean isOutboundRequest(TrafficRequest request) {
    boolean hasOutboundParameters = (request.getAdsource() != null || request.getTag() != null);
    boolean hasOutboundDim =
        request.getDim() != null
            && (request.getDim().equals(TrafficReportDimension.adsource)
                || request.getDim().equals(TrafficReportDimension.tag));
    return hasOutboundParameters || hasOutboundDim;
  }
}
