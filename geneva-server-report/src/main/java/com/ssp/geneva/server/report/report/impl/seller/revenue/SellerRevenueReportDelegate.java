package com.ssp.geneva.server.report.report.impl.seller.revenue;

import static java.util.Optional.ofNullable;

import com.nexage.admin.core.repository.CompanyRepository;
import com.ssp.geneva.server.report.report.util.DateUtil;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import com.ssp.geneva.server.report.report.util.RestrictedAccessUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class SellerRevenueReportDelegate {
  private final CompanyRepository companyRepository;
  private final RestrictedAccessUtil restrictedAccessUtil;

  public void preProcess(SellerRevenueRequest request) {
    if (!request.getReportUser().isNexageUser()) {
      request.setCompanies(request.getReportUser().getCompanies());
    } else {
      ofNullable(request.getSellerSeat())
          .map(companyRepository::findAllIdsBySellerSeatPid)
          .ifPresent(request::setCompanies);
    }

    if (ReportKeys.UNKNOWN_NAME.equals(request.getPosition())) {
      request.setPosition(ReportKeys.UNKNOWN_VALUE);
    }

    String country = request.getCountry();
    if (StringUtils.isBlank(country)) {
      request.setCountry(null);
    }

    request.setSiteIds(
        restrictedAccessUtil.getSiteIdsRestrictionForCompanies(
            request.getCompanies(), request.getReportUser()));
  }

  public List<SellerRevenueResponse> postProcess(
      List<SellerRevenueResponse> reportData, SellerRevenueRequest request) {
    fillMissingIntervalsWithZeroes(reportData, request);
    for (SellerRevenueResponse reportRow : reportData) {
      updateOutboundRequest(request, reportRow);
      reportRow.roundValues();
    }
    return reportData;
  }

  private void updateOutboundRequest(
      SellerRevenueRequest request, SellerRevenueResponse reportRow) {
    if (request.getDim() != null) {
      String dimension = request.getDim().getName();
      if (ReportKeys.ADSOURCE.equalsIgnoreCase(dimension)
          || ReportKeys.TAG.equals(dimension)
          || ReportKeys.AD_SOURCE_TYPE_ID.equals(dimension)
          || request.getAdsource() != null
          || request.getAdSourceTypeId() != null
          || request.getTag() != null) {
        reportRow.setOutboundRequest(true);
      }
    }
  }

  private void fillMissingIntervalsWithZeroes(
      List<SellerRevenueResponse> reportData, SellerRevenueRequest request) {
    if (request.getDim() != null) {
      String dimension = request.getDim().getName();
      if (ReportKeys.DAY.equalsIgnoreCase(dimension)
          || ReportKeys.WEEK.equals(dimension)
          || ReportKeys.MONTH.equals(dimension)) {
        Map<String, String> dateMap = DateUtil.getZeroDatesMap(request, request.getDim());
        reportData.stream()
            .filter(reportRow -> reportRow.getInterval() != null)
            .forEach(
                response -> {
                  dateMap.remove(response.getInterval().split(" ")[0]);
                });
        reportData.addAll(
            dateMap.values().stream().map(SellerRevenueResponse::new).collect(Collectors.toList()));
      }
    }
  }
}
