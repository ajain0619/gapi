package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.core.pubselfserve.CompanyPubSelfServeView;
import com.nexage.admin.core.pubselfserve.SitePubSelfServeView;
import com.nexage.admin.core.repository.CompanyPubSelfServeViewRepository;
import com.nexage.admin.core.repository.SitePubSelfServeViewRepository;
import com.nexage.app.dto.pub.self.serve.PubSelfServeDashboardMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeMediationRuleMetricsDTO;
import com.nexage.app.dto.pub.self.serve.PubSelfServeSiteMetrics;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.DashboardService;
import com.nexage.app.services.PubSelfServeSummaryService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.impl.support.PubSelfServeSummaryContext;
import com.nexage.app.util.FeaturesVisibilityUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.server.report.performance.pss.dao.PubSelfServeMetricsDao;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller()")
@Transactional
public class PubSelfServeSummaryServiceImpl implements PubSelfServeSummaryService {

  private final CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository;
  private final SitePubSelfServeViewRepository sitePubSelfServeViewRepository;
  private final PubSelfServeMetricsDao dw;
  private final DashboardService dashboardService;
  private final PhoneCastConfigService phoneCastConfigService;
  private final FeaturesVisibilityUtil featuresVisibilityUtil;
  private final UserContext userContext;
  private final SellerSiteService sellerSiteService;
  private final GlobalConfigService globalConfigService;

  private final List<Long> exchangeIds = new ArrayList<>();
  private String logoBaseUrl;

  public PubSelfServeSummaryServiceImpl(
      CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository,
      SitePubSelfServeViewRepository sitePubSelfServeViewRepository,
      PubSelfServeMetricsDao dw,
      DashboardService dashboardService,
      PhoneCastConfigService phoneCastConfigService,
      FeaturesVisibilityUtil featuresVisibilityUtil,
      UserContext userContext,
      SellerSiteService sellerSiteService,
      GlobalConfigService globalConfigService) {
    this.companyPubSelfServeViewRepository = companyPubSelfServeViewRepository;
    this.sitePubSelfServeViewRepository = sitePubSelfServeViewRepository;
    this.dw = dw;
    this.dashboardService = dashboardService;
    this.phoneCastConfigService = phoneCastConfigService;
    this.featuresVisibilityUtil = featuresVisibilityUtil;
    this.userContext = userContext;
    this.sellerSiteService = sellerSiteService;
    this.globalConfigService = globalConfigService;
  }

  @PostConstruct
  public void init() {
    logoBaseUrl = globalConfigService.getStringValue(GlobalConfigProperty.BUYER_LOGO_BASE_URL);
  }

  @Override
  public PubSelfServeDashboardMetricsDTO getDashboardSummary(long pubId, Date start, Date stop) {
    checkPrivileges(pubId);
    List<SitePubSelfServeView> sites =
        sitePubSelfServeViewRepository.findAllByPubPidAndStatusNotDeleted(pubId);
    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(
            sellerSiteService.getAllSitesByCompanyPid(pubId), logoBaseUrl);
    Map<Long, List<PubSelfServeMetrics>> dwMetrics =
        getSummaryMetrics(pubId, sites, start, adjustStop(stop));
    PubSelfServeDashboardMetricsDTO metrics =
        new PubSelfServeDashboardMetricsDTO(pubId, sites, pubContext, exchangeIds);
    metrics.aggregate(dwMetrics);
    return metrics;
  }

  @Override
  public PubSelfServeMediationRuleMetricsDTO getTagSummary(long pubId, Date start, Date stop) {
    checkPrivileges(pubId);
    List<SitePubSelfServeView> pubSites =
        sitePubSelfServeViewRepository.findAllByPubPidAndStatusNotDeleted(pubId);
    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(
            sellerSiteService.getAllSitesByCompanyPid(pubId), logoBaseUrl);
    Map<Long, List<PubSelfServeMetrics>> dwMetrics =
        getSummaryMetrics(pubId, pubSites, start, adjustStop(stop));
    PubSelfServeMediationRuleMetricsDTO metrics =
        new PubSelfServeMediationRuleMetricsDTO(pubId, pubSites, pubContext, exchangeIds);
    metrics.aggregate(dwMetrics);
    return metrics;
  }

  @Override
  public PubSelfServeSiteMetrics getTagSummary(long pubId, long siteId, Date start, Date stop) {
    checkPrivileges(pubId);
    SitePubSelfServeView site =
        sitePubSelfServeViewRepository
            .findById(siteId)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_SITE_NOT_FOUND));

    PubSelfServeSummaryContext pubContext =
        new PubSelfServeSummaryContext(sellerSiteService.getSite(siteId), logoBaseUrl);
    List<PubSelfServeMetrics> dwMetrics = dw.getSiteSummaryMetrics(siteId, start, adjustStop(stop));
    PubSelfServeSiteMetrics siteMetric =
        PubSelfServeSiteMetrics.buildSiteWithChildren(site, pubContext);
    if (dwMetrics != null) {
      for (PubSelfServeMetrics metric : dwMetrics) {
        siteMetric.aggregate(metric);
      }
    }
    return siteMetric;
  }

  private Map<Long, List<PubSelfServeMetrics>> getSummaryMetrics(
      long pubId, List<SitePubSelfServeView> sites, Date start, Date stop) {
    return featuresVisibilityUtil.hasDashboardSummaryCaching()
        ? dashboardService.getSummaryMetricsOptimized(pubId, sites, start, stop)
        : dashboardService.getSummaryMetricsDefault(pubId, start, stop);
  }

  private void checkPrivileges(long pubId) {
    CompanyPubSelfServeView pssCompany =
        companyPubSelfServeViewRepository
            .findById(pubId)
            .orElseThrow(
                () -> new GenevaValidationException(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND));

    if (!userContext.isNexageUser()
        && (!userContext.isPublisherSelfServeEnabled(pubId)
            || !userContext.doSameOrNexageAffiliation(pubId))) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    } else if (!pssCompany.isSelfServeAllowed() && !userContext.isNexageUser()) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PSS_NOT_ENABLED);
    }
  }

  private Date adjustStop(Date stop) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(stop);
    if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 59) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      stop = cal.getTime();
    }
    return stop;
  }

  @PostConstruct
  public void postConstruct() {
    for (String exchange : phoneCastConfigService.getExchangeIdsAsList()) {
      exchangeIds.add(Long.valueOf(exchange));
    }
    for (String exchange : phoneCastConfigService.getTestExchangeIdsAsList()) {
      exchangeIds.add(Long.valueOf(exchange));
    }
  }
}
