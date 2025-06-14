package com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance;

import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.countryservice.CountryService;
import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.util.RestrictedAccessUtil;
import com.ssp.geneva.server.report.report.velocity.AbstractVelocityReport;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RtbRevenuePerformanceReport
    extends AbstractVelocityReport<RtbRevenuePerformanceRequest, RtbRevenuePerformanceResponse> {
  private final CountryService countryService;
  private final DirectDealRepository directDealRepository;
  private final RestrictedAccessUtil restrictedAccessUtil;

  @Value("#{rtbRevenuePerformanceQueries}")
  private Map<String, String> queries;

  public RtbRevenuePerformanceReport(
      CountryService countryService,
      DirectDealRepository directDealRepository,
      RestrictedAccessUtil restrictedAccessUtil) {
    this.countryService = countryService;
    this.directDealRepository = directDealRepository;
    this.restrictedAccessUtil = restrictedAccessUtil;
  }

  public List<RtbRevenuePerformanceResponse> postProcess(
      List<RtbRevenuePerformanceResponse> results, RtbRevenuePerformanceRequest request) {
    clearResultIfContainsNullAggregation(results);
    final Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());
    for (RtbRevenuePerformanceResponse rtbRevenuePerformanceResponse : results) {
      updateOutboundCountry(rtbRevenuePerformanceResponse);
      if (rtbRevenuePerformanceResponse.getSeat() != null
          && ReportKeys.UNKNOWN_VALUE.equals(rtbRevenuePerformanceResponse.getSeat())) {
        rtbRevenuePerformanceResponse.setSeat(ReportKeys.UNKNOWN_NAME);
      }
      if (request.getDim() == RtbRevenuePerformanceDimension.deal) {
        if (rtbRevenuePerformanceResponse.getDealID() == null) {
          rtbRevenuePerformanceResponse.setDealID(ReportKeys.UNKNOWN_DEAL_VALUE);
          rtbRevenuePerformanceResponse.setDeal(ReportKeys.UNKNOWN_DEAL_VALUE);
        } else {
          rtbRevenuePerformanceResponse.setDeal(
              initializeDealDescription(rtbRevenuePerformanceResponse));
        }
      }
      if (rtbRevenuePerformanceResponse.getRtbAdvertiserDomain() != null
          && ReportKeys.UNKNOWN_VALUE.equals(
              rtbRevenuePerformanceResponse.getRtbAdvertiserDomain())) {
        rtbRevenuePerformanceResponse.setRtbAdvertiserDomain(ReportKeys.UNKNOWN_NAME);
      }
      if (rtbRevenuePerformanceResponse.getInterval() != null) {
        String removedDate =
            dateMap.remove(rtbRevenuePerformanceResponse.getInterval().split(" ")[0]);
        if (removedDate != null) {
          rtbRevenuePerformanceResponse.setInterval(removedDate);
        }
      }
      rtbRevenuePerformanceResponse.postInitialize();
    }
    results.addAll(
        dateMap.values().stream()
            .map(RtbRevenuePerformanceResponse::new)
            .collect(Collectors.toList()));
    return results;
  }

  private void clearResultIfContainsNullAggregation(List<RtbRevenuePerformanceResponse> results) {
    if (results.size() == 1 && results.get(0).getAdDelivered() == null) {
      results.remove(0);
    }
  }

  private String initializeDealDescription(
      RtbRevenuePerformanceResponse rtbRevenuePerformanceResponse) {
    return directDealRepository
        .findByDealId(rtbRevenuePerformanceResponse.getDealID())
        .map(DirectDeal::getDescription)
        .orElse(ReportKeys.UNKNOWN_DEAL_VALUE);
  }

  private void updateOutboundCountry(RtbRevenuePerformanceResponse row) {
    if (row.getCountry() == null) {
      return;
    }
    if (ReportKeys.UNKNOWN_VALUE.equals(row.getCountry())) {
      row.setCountry(ReportKeys.UNKNOWN_NAME);
    } else {
      row.setCountry(countryService.getName(row.getCountry()));
    }
  }

  @Override
  public List<RtbRevenuePerformanceResponse> getReportData(RtbRevenuePerformanceRequest request)
      throws ReportException, DataAccessException {
    String sql = queries.get(getReportKeyByDimension(request.getDim()));
    if (sql == null) {
      throw new ReportException("unable to find report");
    }
    preProcess(request);
    return postProcess(
        getReportData(
            request, sql, new BeanPropertyRowMapper<>(RtbRevenuePerformanceResponse.class)),
        request);
  }

  private void preProcess(RtbRevenuePerformanceRequest request) {
    if (StringUtils.isBlank(request.getCountry())) {
      request.setCountry(null);
    }

    if (ReportKeys.UNKNOWN_DEAL_VALUE.equalsIgnoreCase(request.getDeal())) {
      request.setDeal(null);
      request.setDealAsNullParam(true);
    }

    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompany(
            request.getCompany(), request.getReportUser()));
  }
}
