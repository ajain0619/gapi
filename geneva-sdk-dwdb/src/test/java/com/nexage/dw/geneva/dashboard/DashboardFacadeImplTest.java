package com.nexage.dw.geneva.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.dw.util.DateUtil;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardSummary;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardFacadeImplTest {
  @Mock private DashboardDetailDao dashboardDetailDao;
  @Mock private DashboardSummaryDao dashboardSummaryDao;
  @InjectMocks private DashboardFacadeImpl dashboardFacade;
  private Date start;
  private Date stop;

  @BeforeEach
  void setUp() {
    start = DateUtil.getNow();
    stop = DateUtil.getNow();
  }

  @Test
  void getSellerMetrics_givenCompanyPids() {
    // given
    Set<Long> companyPids = ImmutableSet.of(1L, 2L);
    given(dashboardDetailDao.getSellerMetrics(any(), any(), eq(companyPids)))
        .willReturn(Collections.singletonList(aSellerMetricWith1000Requests()));
    given(dashboardSummaryDao.getSellerMetrics(any(), any(), eq(companyPids)))
        .willReturn(anEmptyHistoricalData());

    // when
    DashboardMetric sellerMetrics =
        dashboardFacade.getSellerMetrics(start, stop, companyPids, true);

    // then
    assertEquals(1000L, sellerMetrics.getSellerDashboardMetrics().getRequests().getSummary());
  }

  private SellerDashboardSummary anEmptyHistoricalData() {
    return new SellerDashboardSummary(0, 0, 0, 0, BigDecimal.ZERO);
  }

  private SellerDashboardDetail aSellerMetricWith1000Requests() {
    return new SellerDashboardDetail(
        DateUtil.format(DateUtil.getNow()), 1000, 1, 1, 1, BigDecimal.ONE);
  }

  @Test
  void getSellerMetrics_noCompanyPidsGiven() {
    // given
    Set<Long> companyPids = Collections.emptySet();
    given(dashboardDetailDao.getSellerMetrics(any(), any()))
        .willReturn(Collections.singletonList(aSellerMetricWith1000Requests()));
    given(dashboardSummaryDao.getSellerMetrics(any(), any())).willReturn(anEmptyHistoricalData());

    // when
    DashboardMetric sellerMetrics =
        dashboardFacade.getSellerMetrics(start, stop, companyPids, true);

    // then
    assertEquals(1000L, sellerMetrics.getSellerDashboardMetrics().getRequests().getSummary());
  }

  @Test
  void getBuyerMetrics_givenCompanyPids() {
    // given
    Set<Long> companyPids = ImmutableSet.of(1L, 2L);
    given(dashboardDetailDao.getBuyerMetrics(any(), any(), eq(companyPids)))
        .willReturn(Collections.singletonList(aBuyerMetricWith1000Requests()));
    given(dashboardSummaryDao.getBuyerMetrics(any(), any(), eq(companyPids)))
        .willReturn(anEmptyHistoricalBuyerData());

    // when
    DashboardMetric buyerMetrics = dashboardFacade.getBuyerMetrics(start, stop, companyPids, true);

    // then
    assertEquals(1000L, buyerMetrics.getBuyerDashboardMetrics().getRequests().getSummary());
  }

  @Test
  void getBuyerMetrics_noCompanyPidsGiven() {
    // given
    Set<Long> companyPids = Collections.emptySet();
    given(dashboardDetailDao.getBuyerMetrics(any(), any()))
        .willReturn(Collections.singletonList(aBuyerMetricWith1000Requests()));
    given(dashboardSummaryDao.getBuyerMetrics(any(), any()))
        .willReturn(anEmptyHistoricalBuyerData());

    // when
    DashboardMetric buyerMetrics = dashboardFacade.getBuyerMetrics(start, stop, companyPids, true);

    // then
    assertEquals(1000L, buyerMetrics.getBuyerDashboardMetrics().getRequests().getSummary());
  }

  private BuyerDashboardSummary anEmptyHistoricalBuyerData() {
    return new BuyerDashboardSummary(0, 0, 0, 0, BigDecimal.ZERO);
  }

  private BuyerDashboardDetail aBuyerMetricWith1000Requests() {
    return new BuyerDashboardDetail(
        DateUtil.format(DateUtil.getNow()), 1000L, 1000L, 1000L, 1000L, BigDecimal.ONE);
  }
}
