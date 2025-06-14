package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Site;
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
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.FeaturesVisibilityUtil;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.server.report.performance.pss.dao.PubSelfServeMetricsDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PubSelfServeSummaryServiceImplTest {

  @Mock private CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository;

  @Mock private SitePubSelfServeViewRepository sitePubSelfServeViewRepository;

  @Mock private PubSelfServeMetricsDao dw;

  @Mock private DashboardService dashboardService;

  @Mock private UserContext userContext;

  @Mock private SellerSiteService sellerSiteService;

  @Mock private FeaturesVisibilityUtil featuresVisibilityUtil;

  @InjectMocks PubSelfServeSummaryServiceImpl service;

  @Test
  void getDashboardSummaryTest() {

    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();
    CompanyPubSelfServeView companyPubSelfServeView = setupCompanyPubSelfServeView();
    when(companyPubSelfServeViewRepository.findById(any(Long.class)))
        .thenReturn(Optional.ofNullable(companyPubSelfServeView));
    when(sitePubSelfServeViewRepository.findAllByPubPidAndStatusNotDeleted(any(Long.class)))
        .thenReturn(sitePubSelfServeViews);
    final Long pubId = 1234L;
    when(userContext.doSameOrNexageAffiliation(pubId)).thenReturn(true);
    when(userContext.isPublisherSelfServeEnabled(pubId)).thenReturn(true);
    when(featuresVisibilityUtil.hasDashboardSummaryCaching()).thenReturn(true);
    PubSelfServeDashboardMetricsDTO pubSelfServeDashboardMetrics =
        service.getDashboardSummary(pubId, new Date(), new Date());

    assertNotNull(pubSelfServeDashboardMetrics);
  }

  @Test
  void shouldThrowNotFoundWhenCompanyPubSelfServeViewDoesNotExist() {
    // when
    when(companyPubSelfServeViewRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> service.getDashboardSummary(1L, null, null));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotAuthorizedIfSelfServeIsDisabled() {
    CompanyPubSelfServeView companyPubSelfServeView = setupCompanyPubSelfServeView();
    when(companyPubSelfServeViewRepository.findById(any(Long.class)))
        .thenReturn(Optional.ofNullable(companyPubSelfServeView));
    final Long pubId = 1234L;

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> service.getDashboardSummary(pubId, null, null));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void getTagSummaryTest() {
    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();
    CompanyPubSelfServeView companyPubSelfServeView = setupCompanyPubSelfServeView();
    when(companyPubSelfServeViewRepository.findById(any(Long.class)))
        .thenReturn(Optional.ofNullable(companyPubSelfServeView));
    when(sitePubSelfServeViewRepository.findAllByPubPidAndStatusNotDeleted(any(Long.class)))
        .thenReturn(sitePubSelfServeViews);
    final Long pubId = 1234L;
    when(userContext.doSameOrNexageAffiliation(pubId)).thenReturn(true);
    when(userContext.isPublisherSelfServeEnabled(pubId)).thenReturn(true);
    PubSelfServeMediationRuleMetricsDTO pubSelfServeMediationRuleMetrics =
        service.getTagSummary(pubId, new Date(), new Date());

    assertNotNull(pubSelfServeMediationRuleMetrics);
  }

  @Test
  void getTagSummaryMetricsTest() {
    List<SitePubSelfServeView> sitePubSelfServeViews = setupSitePubSelfServeViewList();
    CompanyPubSelfServeView companyPubSelfServeView = setupCompanyPubSelfServeView();
    Site siteDTO = new Site();
    siteDTO.setName("Test");
    final Long pubId = 1234L;
    siteDTO.setPid(pubId);
    when(companyPubSelfServeViewRepository.findById(any(Long.class)))
        .thenReturn(Optional.ofNullable(companyPubSelfServeView));
    when(sitePubSelfServeViewRepository.findById(any(Long.class)))
        .thenReturn(Optional.ofNullable(sitePubSelfServeViews.get(0)));
    when(userContext.doSameOrNexageAffiliation(pubId)).thenReturn(true);
    when(userContext.isPublisherSelfServeEnabled(pubId)).thenReturn(true);
    when(sellerSiteService.getSite(any(Long.class))).thenReturn(siteDTO);
    PubSelfServeSiteMetrics pubSelfServeMetrics =
        service.getTagSummary(pubId, pubId, new Date(), new Date());

    assertNotNull(pubSelfServeMetrics);
  }

  public List<SitePubSelfServeView> setupSitePubSelfServeViewList() {
    List<SitePubSelfServeView> sitePubSelfServeViews = new ArrayList<>();
    SitePubSelfServeView sitePubSelfServeView = new SitePubSelfServeView();
    sitePubSelfServeView.setName("Test");
    sitePubSelfServeView.setPid(1234L);
    sitePubSelfServeViews.add(sitePubSelfServeView);
    return sitePubSelfServeViews;
  }

  public CompanyPubSelfServeView setupCompanyPubSelfServeView() {
    CompanyPubSelfServeView companyPubSelfServeView = new CompanyPubSelfServeView();
    companyPubSelfServeView.setName("TestName");
    companyPubSelfServeView.setPid(1234L);
    companyPubSelfServeView.setSelfServeAllowed(true);
    return companyPubSelfServeView;
  }
}
