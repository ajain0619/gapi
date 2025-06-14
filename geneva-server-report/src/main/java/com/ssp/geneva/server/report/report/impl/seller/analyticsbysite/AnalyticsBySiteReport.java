package com.ssp.geneva.server.report.report.impl.seller.analyticsbysite;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.util.RestrictedAccessUtil;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AnalyticsBySiteReport
    extends AbstractVelocityReport<AnalyticsBySiteRequest, AnalyticsBySiteResponse> {

  @Value("#{analyticsBySiteQueries}")
  private final Map<String, String> queries;

  private final RestrictedAccessUtil restrictedAccessUtil;

  @Override
  protected Map<String, Object> getParameters(AnalyticsBySiteRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    if (request.getSiteIds() != null) {
      parameters.remove(ReportKeys.COMPANY);
    }
    parameters.put(ReportKeys.PARAMETER_IS_OUTBOUND_REQUEST, isOutboundRequest(request));
    return parameters;
  }

  @Override
  public List<AnalyticsBySiteResponse> getReportData(AnalyticsBySiteRequest request)
      throws ReportException, DataAccessException {
    preProcess(request);
    String sql = queries.get(getReportKeyByDimension(request.getDim()));

    if (sql == null) {
      throw new ReportException("unable to find report");
    }

    return postProcess(
        getReportData(request, sql, new BeanPropertyRowMapper<>(AnalyticsBySiteResponse.class)),
        request);
  }

  private void preProcess(AnalyticsBySiteRequest request) {
    if (!request.getReportUser().isNexageUser()) {
      request.setCompany(request.getReportUser().getCompany());
    }

    String country = request.getCountry();
    if (StringUtils.isBlank(country)) {
      request.setCountry(null);
    }

    if (ReportKeys.UNKNOWN_NAME.equals(request.getMake())) {
      request.setMake(ReportKeys.UNKNOWN_VALUE);
    }

    if (ReportKeys.UNKNOWN_NAME.equals(request.getModel())) {
      request.setModel(ReportKeys.UNKNOWN_VALUE);
    }
    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(
            request.getCompany(), request.getReportUser()));
  }

  private List<AnalyticsBySiteResponse> postProcess(
      List<AnalyticsBySiteResponse> reportResults, AnalyticsBySiteRequest reportRequest) {
    clearResultIfContainsNullAggregation(reportResults);
    fillMissingIntervalsWithZeroes(reportResults, reportRequest);
    Boolean isOutboundRequest = isOutboundRequest(reportRequest);
    for (AnalyticsBySiteResponse row : reportResults) {
      row.setOutboundRequest(isOutboundRequest);
      if (row.getDeviceMake() != null) updateOutboundDeviceMake(row);
      if (row.getDeviceModel() != null) updateOutboundDeviceModel(row);
      if (row.getDeviceOS() != null) updateOutboundDeviceOs(row);
      if (row.getDeviceOSVersion() != null) updateOutboundDeviceOsVersion(row);
      if (row.getCarrier() != null) updateOutboundCarrier(row);
    }
    return reportResults;
  }

  private void fillMissingIntervalsWithZeroes(
      List<AnalyticsBySiteResponse> results, AnalyticsBySiteRequest request) {
    Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());
    for (AnalyticsBySiteResponse analyticsBySiteResponse : results) {
      if (analyticsBySiteResponse.getInterval() != null) {
        if (analyticsBySiteResponse.getInterval().compareTo(request.getStart()) < 0) {
          analyticsBySiteResponse.setInterval(
              request.getStart().split(ReportKeys.DATE_SEPARATOR)[0]);
        }
        String removedDate = dateMap.remove(analyticsBySiteResponse.getInterval().split(" ")[0]);
        if (removedDate != null) {
          analyticsBySiteResponse.setInterval(removedDate);
        }
      }
    }
    results.addAll(
        dateMap.values().stream().map(AnalyticsBySiteResponse::new).collect(Collectors.toList()));
  }

  private void clearResultIfContainsNullAggregation(List<AnalyticsBySiteResponse> results) {
    if (results.size() == 1 && results.get(0).getAdDelivered() == null) {
      results.remove(0);
    }
  }

  private void updateOutboundDeviceMake(AnalyticsBySiteResponse row) {
    if (row.getDeviceMake() == null) return;
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getDeviceMake()))
      row.setDeviceMake(ReportKeys.UNKNOWN_NAME);
  }

  private void updateOutboundDeviceModel(AnalyticsBySiteResponse row) {
    if (row.getDeviceModel() == null) return;
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getDeviceModel()))
      row.setDeviceModel(ReportKeys.UNKNOWN_NAME);
  }

  private void updateOutboundDeviceOs(AnalyticsBySiteResponse row) {
    if (row.getDeviceOS() == null) return;
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getDeviceOS()))
      row.setDeviceOS(ReportKeys.UNKNOWN_NAME);
  }

  private void updateOutboundDeviceOsVersion(AnalyticsBySiteResponse row) {
    if (row.getDeviceOSVersion() == null) return;
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getDeviceOSVersion()))
      row.setDeviceOSVersion(ReportKeys.UNKNOWN_NAME);
  }

  private void updateOutboundCarrier(AnalyticsBySiteResponse row) {
    if (row.getCarrier() == null) return;
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getCarrier())) row.setCarrier(ReportKeys.UNKNOWN_NAME);
  }

  /**
   * Returns whether this report request results in report that displays outbound requests.
   *
   * @param request report request
   * @return true if this report request results in report that displays outbound requests, else
   *     false
   */
  private boolean isOutboundRequest(AnalyticsBySiteRequest request) {
    boolean hasOutboundParameters = (request.getAdsource() != null || request.getTag() != null);
    boolean hasOutboundDim =
        request.getDim() != null
            && (request.getDim().equals(AnalyticsReportDimension.adsource)
                || request.getDim().equals(AnalyticsReportDimension.tag));
    return hasOutboundParameters || hasOutboundDim;
  }
}
