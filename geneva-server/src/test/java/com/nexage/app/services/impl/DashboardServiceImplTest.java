package com.nexage.app.services.impl;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.google.common.collect.Sets;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.dw.geneva.dashboard.DashboardFacade;
import com.nexage.dw.geneva.dashboard.model.DashboardMetric;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

  @Mock private DashboardFacade dashboardFacade;
  @Mock private UserContext userContext;
  @InjectMocks private DashboardServiceImpl dashboardService;

  @Test
  void getBuyerDashboard_nexageUserDontSpecifyPids() {
    given(userContext.isNexageUser()).willReturn(true);
    Interval interval = aValidTrendInterval();
    final DashboardMetric expectedResult = mock(DashboardMetric.class);
    given(dashboardFacade.getBuyerMetrics(interval.start, interval.stop, emptySet(), true))
        .willReturn(expectedResult);

    DashboardMetric actualResult =
        dashboardService.getBuyerDashboard(interval.start, interval.stop, true);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void getBuyerDashboard_buyerUserSpecifyPids() {
    given(userContext.getCompanyPids()).willReturn(Sets.newHashSet(1L, 2L));
    Interval interval = aValidTrendInterval();
    final DashboardMetric expectedResult = mock(DashboardMetric.class);
    given(
            dashboardFacade.getBuyerMetrics(
                interval.start, interval.stop, userContext.getCompanyPids(), true))
        .willReturn(expectedResult);

    DashboardMetric actualResult =
        dashboardService.getBuyerDashboard(interval.start, interval.stop, true);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void shouldThrowExceptionWhenGetBuyerDashboardWithInvalidTimeInterval() {
    Interval interval = new Interval(200L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getBuyerDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGetBuyerDashboardWithInvalidTrend() {
    Interval interval = new Interval(10L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getBuyerDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_TREND_NOT_VALID, exception.getErrorCode());
  }

  @Test
  void getSellerDashboard_nexageUserDontSpecifyPids() {
    given(userContext.isNexageUser()).willReturn(true);
    Interval interval = aValidTrendInterval();
    final DashboardMetric expectedResult = mock(DashboardMetric.class);
    given(dashboardFacade.getSellerMetrics(interval.start, interval.stop, emptySet(), true))
        .willReturn(expectedResult);

    DashboardMetric actualResult =
        dashboardService.getSellerDashboard(interval.start, interval.stop, true);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void getSellerDashboard_sellerUserSpecifyPids() {
    given(userContext.getCompanyPids()).willReturn(Sets.newHashSet(1L, 2L));
    Interval interval = aValidTrendInterval();
    final DashboardMetric expectedResult = mock(DashboardMetric.class);
    given(
            dashboardFacade.getSellerMetrics(
                interval.start, interval.stop, userContext.getCompanyPids(), true))
        .willReturn(expectedResult);

    DashboardMetric actualResult =
        dashboardService.getSellerDashboard(interval.start, interval.stop, true);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void shouldThrowExceptionWhenGetSellerDashboardWithInvalidTimeInterval() {
    Interval interval = new Interval(200L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getSellerDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGetSellerDashboardWithInvalidTrend() {
    Interval interval = new Interval(10L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getSellerDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_TREND_NOT_VALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGetNexageDashboardWithInvalidTimeInterval() {
    Interval interval = new Interval(200L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getNexageDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_INVALID_TIME_INTERVAL, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenGetNexageDashboardWithInvalidTrend() {
    Interval interval = new Interval(10L);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> dashboardService.getNexageDashboard(interval.start, interval.stop, true));
    assertEquals(ServerErrorCodes.SERVER_TREND_NOT_VALID, exception.getErrorCode());
  }

  private Interval aValidTrendInterval() {
    return new Interval(5L);
  }

  private static class Interval {
    private final Date start;
    private final Date stop;

    public Interval(Long days) {
      LocalDateTime now = LocalDateTime.now();
      start = java.sql.Timestamp.valueOf(now);
      stop = java.sql.Timestamp.valueOf(now.plusDays(days));
    }
  }
}
