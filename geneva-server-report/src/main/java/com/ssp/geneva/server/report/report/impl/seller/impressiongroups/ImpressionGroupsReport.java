package com.ssp.geneva.server.report.report.impl.seller.impressiongroups;

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
public class ImpressionGroupsReport
    extends AbstractVelocityReport<ImpressionGroupsRequest, ImpressionGroupsResponse> {
  protected final RestrictedAccessUtil restrictedAccessUtil;

  @Value("#{impressionGroupsQueries}")
  private Map<String, String> queries;

  @Override
  protected Map<String, Object> getParameters(ImpressionGroupsRequest request) {
    Map<String, Object> parameters = super.getParameters(request);
    if (request.getSiteIds() != null) {
      parameters.remove(ReportKeys.COMPANY);
    }
    return parameters;
  }

  @Override
  public List<ImpressionGroupsResponse> getReportData(ImpressionGroupsRequest request)
      throws ReportException, DataAccessException {
    preProcess(request);
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    return postProcess(
        getReportData(request, sql, new BeanPropertyRowMapper<>(ImpressionGroupsResponse.class)),
        request);
  }

  private void preProcess(ImpressionGroupsRequest request) {
    if (!request.getReportUser().isNexageUser()) {
      request.setCompany(request.getReportUser().getCompany());
    }

    if (ReportKeys.UNKNOWN_NAME.equalsIgnoreCase(request.getGroup())) {
      request.setGroup(ReportKeys.UNKNOWN_VALUE);
    }

    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(
            request.getCompany(), request.getReportUser()));
  }

  public List<ImpressionGroupsResponse> postProcess(
      List<ImpressionGroupsResponse> results, ImpressionGroupsRequest request) {
    clearResultIfContainsNullAggregation(results);
    final Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());
    Double totalRevenue = 0d;
    Double mmRevenue = 0d;
    Long sumAdServed = 0L;
    Double revenuePerServed = 0d;
    Double mmRevenuePerServed = 0d;
    Double pubRevenuePerServed = 0d;
    Double revenue = 0d;
    Long sumAdServedBySite = 0L;
    if (request.getDim() == ImpressionGroupsReportDimension.group || request.getGroup() != null) {
      for (ImpressionGroupsResponse impressionGroupsResponse : results) {
        sumAdServed +=
            (impressionGroupsResponse.getAdServed() != null)
                ? impressionGroupsResponse.getAdServed()
                : 0L;
        revenue +=
            (impressionGroupsResponse.getRevenue() != null)
                ? impressionGroupsResponse.getRevenue()
                : 0d; // for reference; when dim=group, there will be one row with just revenue and
        // all stats are zero and remaining rows are traffic stats broken down by
        // group.
        sumAdServedBySite +=
            (impressionGroupsResponse.getSiteAdServes() != null)
                ? impressionGroupsResponse.getSiteAdServes()
                : 0L;
        if (impressionGroupsResponse.getGroup() == null) {
          totalRevenue +=
              (impressionGroupsResponse.getTotalRevenue() != null)
                  ? impressionGroupsResponse.getTotalRevenue()
                  : 0d;
          mmRevenue +=
              (impressionGroupsResponse.getMmRevenue() != null)
                  ? impressionGroupsResponse.getMmRevenue()
                  : 0d;
        }
      }
      if (sumAdServedBySite > 0) {
        pubRevenuePerServed = revenue / sumAdServedBySite;
      }
      if (sumAdServed > 0 && request.getReportUser().isNexageUser()) {
        revenuePerServed = totalRevenue / sumAdServed;
        mmRevenuePerServed = mmRevenue / sumAdServed;
      }

      if (request.getDim() == ImpressionGroupsReportDimension.group) {
        results =
            results.stream()
                .filter(impressionGroupsResponse1 -> impressionGroupsResponse1.getGroup() != null)
                .collect(Collectors.toList());
      }
    }
    for (ImpressionGroupsResponse impressionGroupsResponse : results) {
      if (impressionGroupsResponse.getInterval() != null) {
        String removedDate = dateMap.remove(impressionGroupsResponse.getInterval().split(" ")[0]);
        if (removedDate != null) {
          impressionGroupsResponse.setInterval(removedDate);
        }
      }
      sumAdServed += impressionGroupsResponse.getAdServed();
      if (request.getDim() == ImpressionGroupsReportDimension.group || request.getGroup() != null) {
        impressionGroupsResponse.setTotalRevenue(
            revenuePerServed * impressionGroupsResponse.getAdServed());
        impressionGroupsResponse.setMmRevenue(
            mmRevenuePerServed * impressionGroupsResponse.getAdServed());
        impressionGroupsResponse.setRevenue(
            pubRevenuePerServed * impressionGroupsResponse.getAdServed());
      }
      reformatGroup(impressionGroupsResponse);
      impressionGroupsResponse.postInitialize();
    }
    results.addAll(
        dateMap.values().stream().map(ImpressionGroupsResponse::new).collect(Collectors.toList()));
    return results;
  }

  private void reformatGroup(ImpressionGroupsResponse impressionGroupsResponse) {
    if (ReportKeys.UNKNOWN_VALUE.equalsIgnoreCase(impressionGroupsResponse.getGroup())) {
      impressionGroupsResponse.setGroup(ReportKeys.UNKNOWN_NAME);
    }
  }

  private void clearResultIfContainsNullAggregation(List<ImpressionGroupsResponse> results) {
    if (results.size() == 1 && results.get(0).getAdClicks() == null) {
      results.remove(0);
    }
  }
}
