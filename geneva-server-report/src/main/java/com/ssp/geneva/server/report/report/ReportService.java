package com.ssp.geneva.server.report.report;

import com.ssp.geneva.server.report.report.exceptions.ReportException;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendReport;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendRequest;
import com.ssp.geneva.server.report.report.impl.buyer.bidderspend.BidderSpendResponse;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageReport;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageRequest;
import com.ssp.geneva.server.report.report.impl.buyer.subscriptiondatausage.SubscriptionDataUsageResponse;
import com.ssp.geneva.server.report.report.impl.finance.adservingrevenue.AdServingRevenueReport;
import com.ssp.geneva.server.report.report.impl.finance.adservingrevenue.AdServingRevenueRequest;
import com.ssp.geneva.server.report.report.impl.finance.adservingrevenue.AdServingRevenueResponse;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource.FinanceMetricsByAdSourceReport;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource.FinanceMetricsByAdSourceRequest;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbyadsource.FinanceMetricsByAdSourceResponse;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite.FinanceMetricsBySiteReport;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite.FinanceMetricsBySiteRequest;
import com.ssp.geneva.server.report.report.impl.finance.financemetricsbysite.FinanceMetricsBySiteResponse;
import com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue.MediationAndRTBRevenueReport;
import com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue.MediationAndRTBRevenueRequest;
import com.ssp.geneva.server.report.report.impl.finance.mediationandrtbrevenue.MediationAndRTBRevenueResponse;
import com.ssp.geneva.server.report.report.impl.finance.publishersettlement.PublisherSettlementReport;
import com.ssp.geneva.server.report.report.impl.finance.publishersettlement.PublisherSettlementRequest;
import com.ssp.geneva.server.report.report.impl.finance.publishersettlement.PublisherSettlementResponse;
import com.ssp.geneva.server.report.report.impl.finance.rtbbidderar.RTBBidderARReport;
import com.ssp.geneva.server.report.report.impl.finance.rtbbidderar.RTBBidderARRequest;
import com.ssp.geneva.server.report.report.impl.finance.rtbbidderar.RTBBidderARResponse;
import com.ssp.geneva.server.report.report.impl.internal.rxperformance.RxPerformanceReport;
import com.ssp.geneva.server.report.report.impl.internal.rxperformance.RxPerformanceRequest;
import com.ssp.geneva.server.report.report.impl.internal.rxperformance.RxPerformanceResponse;
import com.ssp.geneva.server.report.report.impl.internal.sitedistribution.RtbSiteDistributionReport;
import com.ssp.geneva.server.report.report.impl.internal.sitedistribution.RtbSiteDistributionRequest;
import com.ssp.geneva.server.report.report.impl.internal.sitedistribution.RtbSiteDistributionResponse;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerReport;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerRequest;
import com.ssp.geneva.server.report.report.impl.seller.adserver.AdServerResponse;
import com.ssp.geneva.server.report.report.impl.seller.analyticsbysite.AnalyticsBySiteReport;
import com.ssp.geneva.server.report.report.impl.seller.analyticsbysite.AnalyticsBySiteRequest;
import com.ssp.geneva.server.report.report.impl.seller.analyticsbysite.AnalyticsBySiteResponse;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsReport;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsRequest;
import com.ssp.geneva.server.report.report.impl.seller.impressiongroups.ImpressionGroupsResponse;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueReport;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueRequest;
import com.ssp.geneva.server.report.report.impl.seller.revenue.SellerRevenueResponse;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceReport;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceRequest;
import com.ssp.geneva.server.report.report.impl.seller.rtbrevenueperformance.RtbRevenuePerformanceResponse;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficReport;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficRequest;
import com.ssp.geneva.server.report.report.impl.seller.traffic.TrafficResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReportService {

  private final SellerRevenueReport sellerRevenueReport;
  private final RTBBidderARReport rtbBidderARReport;
  private final PublisherSettlementReport publisherSettlementReport;
  private final AnalyticsBySiteReport analyticsBySiteReport;
  private final TrafficReport trafficReport;
  private final BidderSpendReport bidderSpendReport;
  private final AdServerReport adServerReport;
  private final RxPerformanceReport rxPerformanceReport;
  private final AdServingRevenueReport adServingRevenueReport;
  private final MediationAndRTBRevenueReport mediationAndRTBRevenueReport;
  private final ImpressionGroupsReport impressionGroupsReport;
  private final FinanceMetricsByAdSourceReport financeMetricsByAdSourceReport;
  private final FinanceMetricsBySiteReport financeMetricsBySiteReport;
  private final RtbRevenuePerformanceReport rtbRevenuePerformanceReport;
  private final RtbSiteDistributionReport rtbSiteDistributionReport;
  private final SubscriptionDataUsageReport subscriptionDataUsageReport;

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) "
          + "and (#request.getCompany() == null "
          + "or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())) "
          + "or (#request.getSellerSeat() != null and @loginUserContext.hasAccessToSellerSeatOrHasNexageAffiliation(#request.getSellerSeat())))")
  public List<SellerRevenueResponse> getSellerRevenueReport(SellerRevenueRequest request)
      throws DataAccessException, ReportException {
    return sellerRevenueReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<RTBBidderARResponse> getRTBBidderARReport(RTBBidderARRequest request)
      throws DataAccessException, ReportException {
    return rtbBidderARReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<PublisherSettlementResponse> getPublisherSettlementReport(
      PublisherSettlementRequest request) throws DataAccessException, ReportException {
    return publisherSettlementReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()) "
          + "and (#request.getBuyer() == null or (#request.getBuyer() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getBuyer())))")
  public List<BidderSpendResponse> getBidderSpendReport(BidderSpendRequest request)
      throws ReportException {
    return bidderSpendReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) "
          + "and (#request.getCompany() == null or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())))")
  public List<AnalyticsBySiteResponse> getAnalyticsBySiteReport(AnalyticsBySiteRequest request)
      throws DataAccessException, ReportException {
    return analyticsBySiteReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) "
          + "and (#request.getCompany() == null or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())))")
  public List<TrafficResponse> getTrafficReport(TrafficRequest request)
      throws DataAccessException, ReportException {
    return trafficReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()) "
          + "and (#request.getCompany() == null or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())))")
  public List<AdServerResponse> getAdServerReport(AdServerRequest request)
      throws DataAccessException, ReportException {
    return adServerReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<RxPerformanceResponse> getRxPerformanceReport(RxPerformanceRequest request)
      throws ReportException {
    return rxPerformanceReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<AdServingRevenueResponse> getAdServingRevenueReport(AdServingRevenueRequest request)
      throws ReportException {
    return adServingRevenueReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<MediationAndRTBRevenueResponse> getMediationAndRTBRevenueReport(
      MediationAndRTBRevenueRequest request) throws ReportException {
    return mediationAndRTBRevenueReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserSeatHolder()) "
          + "and (#request.getCompany() == null or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())))")
  public List<ImpressionGroupsResponse> getImpressionGroupsReport(ImpressionGroupsRequest request)
      throws ReportException {
    return impressionGroupsReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<FinanceMetricsByAdSourceResponse> getFinanceMetricsByAdSourceReport(
      FinanceMetricsByAdSourceRequest request) throws ReportException {
    return financeMetricsByAdSourceReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<FinanceMetricsBySiteResponse> getFinanceMetricsBySiteReport(
      FinanceMetricsBySiteRequest request) throws ReportException {
    return financeMetricsBySiteReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() or @loginUserContext.isOcUserSeatHolder()) "
          + "and (#request.getCompany() == null or (#request.getCompany() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getCompany())))")
  public List<RtbRevenuePerformanceResponse> getRtbRevenuePerformanceReport(
      RtbRevenuePerformanceRequest request) throws ReportException {
    return rtbRevenuePerformanceReport.getReportData(request);
  }

  @PreAuthorize("@loginUserContext.isOcUserNexage()")
  public List<RtbSiteDistributionResponse> getRtbSiteDistributionReport(
      RtbSiteDistributionRequest request) throws ReportException {
    return rtbSiteDistributionReport.getReportData(request);
  }

  @PreAuthorize(
      "(@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserBuyer()) "
          + "and (#request.getBuyer() == null or (#request.getBuyer() != null and @loginUserContext.doSameOrNexageAffiliation(#request.getBuyer())))")
  public List<SubscriptionDataUsageResponse> getSubscriptionDataUsageReport(
      SubscriptionDataUsageRequest request) throws ReportException {
    return subscriptionDataUsageReport.getReportData(request);
  }
}
