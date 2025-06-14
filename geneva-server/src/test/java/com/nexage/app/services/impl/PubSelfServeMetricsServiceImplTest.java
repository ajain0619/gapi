package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.pubselfserve.CompanyPubSelfServeView;
import com.nexage.admin.core.repository.CompanyPubSelfServeViewRepository;
import com.nexage.admin.dw.util.ReportDefEnums.Interval;
import com.nexage.app.dto.publisher.PublisherMetricsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.impl.queries.DWQueries;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@ExtendWith(MockitoExtension.class)
class PubSelfServeMetricsServiceImplTest {
  @Mock protected JdbcTemplate jdbcTemplate;
  @Mock DataSource dataSource;
  @Mock Connection connection;
  @Mock private CompanyPubSelfServeViewRepository companyPubSelfServeViewRepository;
  @Mock private DWQueries queries;
  @Mock UserContext userContext;
  @Mock SqlRowSet sqlRowSet;
  @Mock PreparedStatement preparedStatement;
  @InjectMocks private PubSelfServeMetricsServiceImpl pubSelfServeMetricsServiceImpl;

  @BeforeEach
  void setUp() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(DataSourceUtils.getConnection(dataSource)).thenReturn(connection);
  }

  @Test
  void getAdSourceMetricsTest() throws Exception {
    final long pubPid = 10024L;
    CompanyPubSelfServeView companyPubSelfServeView = new CompanyPubSelfServeView();
    companyPubSelfServeView.setSelfServeAllowed(true);
    companyPubSelfServeView.setPid(pubPid);
    String query =
        "select DATE(start) as ts, sum(ads_requested_adnet) "
            + "as requests,sum(ads_served) as served, sum(ads_delivered) as delivered,sum(ads_clicked) "
            + "as clicks, sum(revenue) - sum(revenue_net) as revenue,CASE WHEN sum(ads_requested_adnet) = 0 "
            + "THEN 0 ELSE CASE WHEN coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) > 100 "
            + "THEN 100 ELSE coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) END END as fillrate,CASE WHEN sum(ads_delivered) = 0 "
            + "THEN 0 ELSE CASE WHEN coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) > 100 "
            + "THEN 100 ELSE coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) END END as ctr,CASE WHEN sum(ads_delivered) = 0 "
            + "THEN 0 ELSE CASE WHEN sum(ads_delivered) > 0 "
            + "THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_delivered)) * 1000 ELSE 0 END END as ecpm,CASE WHEN sum(ads_requested_adnet) = 0 "
            + "THEN 0 ELSE CASE WHEN sum(ads_requested_adnet) > 0 "
            + "THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_requested_adnet)) * 1000 "
            + "ELSE 0 END END as rpm from fact_revenue_adnet_vw_daily where publisher_id = 10024 and adnet_id = 4005 and tag_monetization in (1,-1) "
            + "and start >= \'2019-12-20 23:00:00\' and start < \'2019-05-27 14:30:23\' GROUP BY  DATE(start)";
    Date date = new Date(System.currentTimeMillis());
    when(companyPubSelfServeViewRepository.findById(pubPid))
        .thenReturn(Optional.of(companyPubSelfServeView));
    when(userContext.isPublisherSelfServeEnabled(pubPid)).thenReturn(true);
    when(queries.buildSQLforAdSourceMetrics(Interval.DAILY)).thenReturn(new StringBuilder(query));
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    PublisherMetricsDTO publisherMetrics =
        pubSelfServeMetricsServiceImpl.getAdSourceMetrics(
            pubPid, 4005L, null, null, null, "2019-12-21", "2019-12-22", null);
    assertEquals(1, publisherMetrics.getRequests().size());
    assertEquals(1, publisherMetrics.getServed().size());
    assertEquals(1, publisherMetrics.getDelivered().size());
    assertEquals(1, publisherMetrics.getRevenue().size());
    assertEquals(1, publisherMetrics.getFillRate().size());
    assertEquals(1, publisherMetrics.getCtr().size());
  }

  @Test
  void shouldThrowUnauthorizedWhenPublisherSelfServeIsDisabled() {
    when(companyPubSelfServeViewRepository.findById(anyLong()))
        .thenReturn(Optional.of(new CompanyPubSelfServeView()));
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () ->
                pubSelfServeMetricsServiceImpl.getAdSourceMetrics(
                    1L, null, null, null, null, "2019-12-21", "2019-12-22", null));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void getMetricsTest() throws Exception {
    final long pubPid = 10024L;
    CompanyPubSelfServeView companyPubSelfServeView = new CompanyPubSelfServeView();
    companyPubSelfServeView.setSelfServeAllowed(true);
    companyPubSelfServeView.setPid(pubPid);
    String query =
        "select DATE(start) as ts, sum(ads_requested_adnet) "
            + "as requests,sum(ads_served) as served, sum(ads_delivered) as delivered,sum(ads_clicked) "
            + "as clicks, sum(revenue) - sum(revenue_net) as revenue,CASE WHEN sum(ads_requested_adnet) = 0 "
            + "THEN 0 ELSE CASE WHEN coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) > 100 "
            + "THEN 100 ELSE coalesce(ROUND((sum(ads_served) / sum(ads_requested_adnet)) * 100, 4.0), 0) END END as fillrate,CASE WHEN sum(ads_delivered) = 0 "
            + "THEN 0 ELSE CASE WHEN coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) > 100 "
            + "THEN 100 ELSE coalesce(ROUND((sum(ads_clicked) / sum(ads_delivered)) * 100, 4.0), 0) END END as ctr,CASE WHEN sum(ads_delivered) = 0 "
            + "THEN 0 ELSE CASE WHEN sum(ads_delivered) > 0 "
            + "THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_delivered)) * 1000 ELSE 0 END END as ecpm,CASE WHEN sum(ads_requested_adnet) = 0 "
            + "THEN 0 ELSE CASE WHEN sum(ads_requested_adnet) > 0 "
            + "THEN ((sum(revenue) - sum(revenue_net))/ sum(ads_requested_adnet)) * 1000 "
            + "ELSE 0 END END as rpm from fact_revenue_adnet_vw_daily where publisher_id = 10024 and adnet_id = 4005 and tag_monetization in (1,-1) "
            + "and start >= \'2019-12-20 23:00:00\' and start < \'2019-05-27 14:30:23\' GROUP BY  DATE(start)";
    Date date = new Date(System.currentTimeMillis());
    when(companyPubSelfServeViewRepository.findById(pubPid))
        .thenReturn(Optional.of(companyPubSelfServeView));
    when(userContext.isPublisherSelfServeEnabled(pubPid)).thenReturn(true);
    when(queries.buildSQLforMetrics(Interval.DAILY)).thenReturn(new StringBuilder(query));
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
    PublisherMetricsDTO publisherMetrics =
        pubSelfServeMetricsServiceImpl.getMetrics(pubPid, "2019-12-21", "2019-12-22", null);
    assertEquals(1, publisherMetrics.getRequests().size());
    assertEquals(1, publisherMetrics.getServed().size());
    assertEquals(1, publisherMetrics.getDelivered().size());
    assertEquals(1, publisherMetrics.getRevenue().size());
    assertEquals(1, publisherMetrics.getFillRate().size());
    assertEquals(1, publisherMetrics.getCtr().size());
  }

  @Test
  void getAdSourceMetricsTestException() throws Exception {
    final long pubPid = 10024L;
    CompanyPubSelfServeView companyPubSelfServeView = new CompanyPubSelfServeView();
    companyPubSelfServeView.setSelfServeAllowed(true);
    companyPubSelfServeView.setPid(pubPid);
    when(companyPubSelfServeViewRepository.findById(pubPid))
        .thenReturn(Optional.of(companyPubSelfServeView));
    when(userContext.isPublisherSelfServeEnabled(pubPid)).thenReturn(true);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                pubSelfServeMetricsServiceImpl.getAdSourceMetrics(
                    pubPid, null, null, null, null, "2019-12-21", "2019-12-22", null));

    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void getMetricsTestException() throws Exception {
    final long pubPid = 10024L;
    CompanyPubSelfServeView companyPubSelfServeView = new CompanyPubSelfServeView();
    companyPubSelfServeView.setSelfServeAllowed(true);
    companyPubSelfServeView.setPid(pubPid);
    when(companyPubSelfServeViewRepository.findById(pubPid))
        .thenReturn(Optional.of(companyPubSelfServeView));
    when(userContext.isPublisherSelfServeEnabled(pubPid)).thenReturn(true);
    when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
    var ex =
        assertThrows(
            GenevaAppRuntimeException.class,
            () ->
                pubSelfServeMetricsServiceImpl.getMetrics(
                    pubPid, "2019-12-21", "2019-12-22", null));
    assertEquals(ServerErrorCodes.SERVER_PUBLISHER_METRICS_FAILED, ex.getErrorCode());
  }
}
