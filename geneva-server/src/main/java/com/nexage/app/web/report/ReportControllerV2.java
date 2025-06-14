package com.nexage.app.web.report;

import com.nexage.app.security.UserContext;
import com.nexage.app.web.report.integration.AdServerRequestImpl;
import com.nexage.app.web.report.integration.AdServingRevenueRequestImpl;
import com.nexage.app.web.report.integration.BaseReportRequest;
import com.nexage.app.web.report.integration.BaseReportRequest.ReportUser;
import com.nexage.app.web.report.integration.BidderSpendRequestImpl;
import com.nexage.app.web.report.integration.FinanceMetricsByAdSourceRequestImpl;
import com.nexage.app.web.report.integration.FinanceMetricsBySiteRequestImpl;
import com.nexage.app.web.report.integration.ImpressionGroupsRequestImpl;
import com.nexage.app.web.report.integration.MediationAndRTBRevenueRequestImpl;
import com.nexage.app.web.report.integration.PublisherSettlementRequestImpl;
import com.nexage.app.web.report.integration.RTBBidderARRequestImpl;
import com.nexage.app.web.report.integration.RtbRevenuePerformanceRequestImpl;
import com.nexage.app.web.report.integration.RtbSiteDistributionRequestImpl;
import com.nexage.app.web.report.integration.RxPerformanceRequestImpl;
import com.nexage.app.web.report.integration.SellerRevenueInternalRequestImpl;
import com.nexage.app.web.report.integration.SellerRevenueRequestImpl;
import com.nexage.app.web.report.integration.SellerRevenueRestrictedRequestImpl;
import com.nexage.app.web.report.integration.SubscriptionDataUsageRequestImpl;
import com.nexage.app.web.report.integration.TrafficRequestImpl;
import com.nexage.app.web.report.integration.TrafficRestrictedRequestImpl;
import com.nexage.countryservice.CountryService;
import com.ssp.geneva.common.base.annotation.Legacy;
import com.ssp.geneva.server.report.report.ReportService;
import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendResponse;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageReportDimension;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageResponse;
import com.ssp.geneva.server.report.report.impl.finance.adservingrevenue.AdServingRevenueResponse;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource.FinanceMetricsByAdSourceResponse;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite.FinanceMetricsBySiteResponse;
import com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue.MediationAndRTBRevenueResponse;
import com.ssp.geneva.server.report.report.impl.finance.publishersettlement.PublisherSettlementResponse;
import com.ssp.geneva.server.report.report.impl.finance.rtbbidderar.RTBBidderARResponse;
import com.ssp.geneva.server.report.report.impl.internal.rxperformance.RxPerformanceResponse;
import com.ssp.geneva.server.report.report.impl.internal.sitedistribution.RtbSiteDistributionResponse;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerResponse;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsResponse;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueInternalReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueRequest;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueResponse;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueRestrictedReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceDimension;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceResponse;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficReportDimension;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficResponse;
import com.ssp.geneva.server.report.report.util.ReportKeys;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Legacy
@Log4j2
@Tag(name = "/reports/")
@RestController
@Profile({"default", "aws"})
@RequestMapping(value = "/reports/")
public class ReportControllerV2 {

  private final ReportService reportService;

  private final UserContext userContext;

  private final CountryService countryService;

  public ReportControllerV2(
      ReportService reportService, UserContext userContext, CountryService countryService) {
    this.reportService = reportService;
    this.userContext = userContext;
    this.countryService = countryService;
  }

  @InitBinder
  public void requestDataBinding(WebDataBinder binder) {
    if (binder.getTarget() instanceof BaseReportRequest) {
      BaseReportRequest request = (BaseReportRequest) binder.getTarget();
      request.setReportUser(new ReportUser(userContext));
    }
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532cd/nexage")
  @ResponseBody
  public List<PublisherSettlementResponse> getPublisherSettlementReport(
      @Valid @ModelAttribute PublisherSettlementRequestImpl request) throws ReportException {
    return reportService.getPublisherSettlementReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532cc/nexage")
  @ResponseBody
  public List<RTBBidderARResponse> getRTBBidderARReport(
      @Valid @ModelAttribute RTBBidderARRequestImpl request) throws ReportException {
    return reportService.getRTBBidderARReport(request);
  }

  @GetMapping(value = "6e09bad2fffb11e49d5f47bfe53f8af7/seller")
  @ResponseBody
  public List<SellerRevenueResponse> getSellerRevenueReport(
      @Valid @ModelAttribute SellerRevenueRequestImpl request,
      @RequestParam(value = "dim", required = false) SellerRevenueReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return mapSellerRevenueResponseCountryCode(
        reportService.getSellerRevenueReport(mapSellerRevenueRequestCountryCode(request)));
  }

  @GetMapping(value = "fed87f460dfa11e598a100da44c6171d/seller")
  @ResponseBody
  public List<SellerRevenueResponse> getSellerRevenueInternalReport(
      @ModelAttribute SellerRevenueInternalRequestImpl request,
      @RequestParam(value = "dim", required = false) SellerRevenueInternalReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return mapSellerRevenueResponseCountryCode(
        reportService.getSellerRevenueReport(mapSellerRevenueRequestCountryCode(request)));
  }

  @GetMapping(value = "f5c960fe0df611e598a100da44c6171d/seller")
  @ResponseBody
  public List<SellerRevenueResponse> getSellerRevenueRestrictedReport(
      @Valid @ModelAttribute SellerRevenueRestrictedRequestImpl request,
      @RequestParam(value = "dim", required = false) SellerRevenueRestrictedReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return mapSellerRevenueResponseCountryCode(
        reportService.getSellerRevenueReport(mapSellerRevenueRequestCountryCode(request)));
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532c2/seller")
  @ResponseBody
  public List<TrafficResponse> getTrafficReport(
      @Valid @ModelAttribute TrafficRequestImpl request,
      @RequestParam(value = "dim", required = false) TrafficReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return reportService.getTrafficReport(request);
  }

  @GetMapping(value = "8e57524d6c614ef681e2753ca1186a22/seller")
  @ResponseBody
  public List<TrafficResponse> getTrafficRestrictedReport(
      @Valid @ModelAttribute TrafficRestrictedRequestImpl request) throws ReportException {
    return reportService.getTrafficReport(request);
  }

  @GetMapping(value = "a3d17694f4f711e4b0e5d4bed9f6c105/buyer")
  @ResponseBody
  public List<BidderSpendResponse> getBidderSpendReport(
      @Valid @ModelAttribute BidderSpendRequestImpl request,
      @RequestParam(value = "dim", required = false) BidderSpendReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return reportService.getBidderSpendReport(request);
  }

  @GetMapping(value = "8a858a0401363683264383264ac302b6/seller")
  @ResponseBody
  public List<AdServerResponse> getAdServerReport(
      @Valid AdServerRequestImpl request,
      @RequestParam(value = "dim", required = false) AdServerReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return reportService.getAdServerReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532c8/nexage")
  @ResponseBody
  public List<RxPerformanceResponse> getRxPerformanceReport(
      @Valid @ModelAttribute RxPerformanceRequestImpl request) throws ReportException {
    return reportService.getRxPerformanceReport(request);
  }

  @GetMapping(
      value = {
        "f876ee3203da11e5856d465f09123f08/seller",
        "6a4bd96403c711e5856d465f09123f08/seller"
      })
  @ResponseBody
  public List<ImpressionGroupsResponse> getImpressionGroupsReport(
      @Valid ImpressionGroupsRequestImpl request,
      @RequestParam(value = "dim", required = false) ImpressionGroupsReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return reportService.getImpressionGroupsReport(request);
  }

  @GetMapping(value = "3e5412d3504b44259eed8436c2d79b00/nexage")
  @ResponseBody
  public List<AdServingRevenueResponse> getAdServingRevenueReport(
      @Valid @ModelAttribute AdServingRevenueRequestImpl request) throws ReportException {
    return reportService.getAdServingRevenueReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532cb/nexage")
  @ResponseBody
  public List<MediationAndRTBRevenueResponse> getMediationAndRTBRevenueReport(
      @Valid @ModelAttribute MediationAndRTBRevenueRequestImpl request) throws ReportException {
    return reportService.getMediationAndRTBRevenueReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532cf/nexage")
  @ResponseBody
  public List<FinanceMetricsByAdSourceResponse> getFinanceMetricsByAdSourceReport(
      @Valid @ModelAttribute FinanceMetricsByAdSourceRequestImpl request) throws ReportException {
    return reportService.getFinanceMetricsByAdSourceReport(request);
  }

  @GetMapping(
      value = {
        "67f2196c1f3811e5b1288c261701bfbc/seller",
        "b08b5a681ea111e5b1288c261701bfbc/seller"
      })
  @ResponseBody
  public List<RtbRevenuePerformanceResponse> getRtbRevenuePerformanceReport(
      @Valid @ModelAttribute RtbRevenuePerformanceRequestImpl request,
      @RequestParam(value = "dim", required = false) RtbRevenuePerformanceDimension dim)
      throws ReportException {
    request.setCountry(this.getInboundCountryCode(request.getCountry()));
    request.setDim(dim);
    return reportService.getRtbRevenuePerformanceReport(request);
  }

  @GetMapping(value = "5FCD2CDEFC3811E18A26DA416188709B/buyer")
  @ResponseBody
  public List<SubscriptionDataUsageResponse> getSubscriptionDataUsageReport(
      @Valid @ModelAttribute SubscriptionDataUsageRequestImpl request,
      @RequestParam(value = "dim", required = false) SubscriptionDataUsageReportDimension dim)
      throws ReportException {
    request.setDim(dim);
    return reportService.getSubscriptionDataUsageReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532c7/nexage")
  @ResponseBody
  public List<RtbSiteDistributionResponse> getRtbSiteDistributionReport(
      @Valid @ModelAttribute RtbSiteDistributionRequestImpl request) throws ReportException {
    return reportService.getRtbSiteDistributionReport(request);
  }

  @GetMapping(value = "8a858a47012d2d14b2bd14b2c41532cq/nexage")
  @ResponseBody
  public List<FinanceMetricsBySiteResponse> getFinanceMetricsBySiteReport(
      @Valid @ModelAttribute FinanceMetricsBySiteRequestImpl request) throws ReportException {
    return reportService.getFinanceMetricsBySiteReport(request);
  }

  private List<SellerRevenueResponse> mapSellerRevenueResponseCountryCode(
      List<SellerRevenueResponse> responses) {

    responses.forEach(
        response -> {
          if (!StringUtils.isBlank(response.getCountry())) {
            if (response.getCountry() != null) {
              if (ReportKeys.UNKNOWN_VALUE.equals(response.getCountry())) {
                response.setCountry(ReportKeys.UNKNOWN_NAME);
              } else {
                response.setCountry(countryService.getName(response.getCountry()));
              }
            }
          }
        });

    return responses;
  }

  private SellerRevenueRequest mapSellerRevenueRequestCountryCode(SellerRevenueRequest request) {

    String country = request.getCountry();
    if (!StringUtils.isBlank(country)) {
      if (ReportKeys.UNKNOWN_NAME.equals(country)) {
        request.setCountry(ReportKeys.UNKNOWN_VALUE);
      } else {
        request.setCountry(countryService.getIso3ByName(country));
      }
    } else {
      request.setCountry(null);
    }
    return request;
  }

  private String getInboundCountryCode(String countryName) {
    if (StringUtils.isBlank(countryName)) {
      return countryName;
    }

    if (ReportKeys.UNKNOWN_NAME.equals(countryName)) {
      return ReportKeys.UNKNOWN_VALUE;
    } else {
      return countryService.getIso3ByName(countryName);
    }
  }
}
