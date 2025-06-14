package com.ssp.geneva.server.report.performance.pss.dao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableSet;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

@ExtendWith(MockitoExtension.class)
class PbSelfServeMetricsDaoTest {

  @Mock private NamedParameterJdbcTemplate dwNamedTemplate;

  @InjectMocks private PubSelfServeMetricsDao pubSelfServeMetricsDao;

  @Test
  void getDashboardSummaryMetricsTest() {
    long pubId = 1l;
    Set<Long> sites = ImmutableSet.of();
    Date start = new Date();
    Date stop = new Date();

    String query = getDashboardSummaryQuery(false);

    Map<Long, List<PubSelfServeMetrics>> metricsMap =
        pubSelfServeMetricsDao.getDashboardSummaryMetrics(pubId, sites, start, stop);
    verify(dwNamedTemplate)
        .query(eq(query), any(SqlParameterSource.class), any(RowCallbackHandler.class));
  }

  @Test
  void getDashboardSummaryMetricsWithSitesTest() throws SQLException {
    long pubId = 1l;
    Set<Long> sites = ImmutableSet.of(1l, 2l, 3l);
    Date start = new Date();
    Date stop = new Date();

    String query = getDashboardSummaryQuery(true);

    Map<Long, List<PubSelfServeMetrics>> metricsMap =
        pubSelfServeMetricsDao.getDashboardSummaryMetrics(pubId, sites, start, stop);
    verify(dwNamedTemplate)
        .query(eq(query), any(SqlParameterSource.class), any(RowCallbackHandler.class));
  }

  @Test
  void getSiteSummaryMetricsTest() {
    long siteId = 1l;
    Date start = new Date();
    Date stop = new Date();

    String query = getSiteSummaryQuery();
    List<PubSelfServeMetrics> metricsMap =
        pubSelfServeMetricsDao.getSiteSummaryMetrics(siteId, start, stop);
    verify(dwNamedTemplate).query(eq(query), any(SqlParameterSource.class), any(RowMapper.class));
  }

  private static String getDashboardSummaryQuery(boolean withSitesParam) {
    return new StringBuilder()
        .append(
            "select t.site_id as site_id, t.adnet_id as adnet_id, t.tag_id as tag_id, t.zone as position, sum(t.current_inbound_reqs) as current_inbound_reqs, sum(t.current_outbound_reqs) as current_outbound_reqs, ")
        .append(
            "sum(t.current_ads_served) as current_ads_served, sum(t.current_ads_delivered) as current_ads_delivered, sum(t.current_ads_clicked) as current_ads_clicked, sum(t.current_revenue) as current_revenue, sum(t.prev_inbound_reqs) as prev_inbound_reqs, ")
        .append(
            "sum(t.prev_outbound_reqs) as prev_outbound_reqs, sum(t.prev_ads_served) as prev_ads_served, sum(t.prev_ads_delivered) as prev_ads_delivered, sum(t.prev_ads_clicked) as prev_ads_clicked, sum(t.prev_revenue) as prev_revenue, max(t.updated) as updated ")
        .append("from (")
        .append(
            "select site_id, zone, adnet_id, tag_id, sum(ads_requested_site) as current_inbound_reqs, sum(ads_requested_adnet) as current_outbound_reqs, sum(ads_served) as current_ads_served, sum(ads_delivered) as current_ads_delivered, ")
        .append(
            "sum(ads_clicked) as current_ads_clicked, sum(revenue - revenue_net) as current_revenue, 0 as "
                + "prev_inbound_reqs, 0 as prev_outbound_reqs, 0 as prev_ads_served, 0 as prev_ads_delivered, 0 as prev_ads_clicked, 0.0 as prev_revenue, max(updated) as updated ")
        .append(
            "from fact_revenue_adnet_vw_daily where start >= :current_start and start < :current_stop and publisher_id = :publisherId and (tag_monetization=1 or tag_monetization=-1) "
                + (withSitesParam ? "and site_id in (:sites)" : "")
                + " group by site_id, zone, adnet_id, tag_id ")
        .append("union all ")
        .append(
            "select site_id, zone, adnet_id, tag_id, 0 as current_inbound_reqs, 0 as current_outbound_reqs, 0 as current_ads_served, 0 as current_ads_delivered, 0 as current_ads_clicked, 0.0 as current_revenue, ")
        .append(
            "sum(ads_requested_site) as prev_inbound_reqs, sum(ads_requested_adnet) as prev_outbound_reqs, sum(ads_served) as prev_ads_served, sum(ads_delivered) as prev_ads_delivered, sum(ads_clicked) as prev_ads_clicked, sum(revenue) as prev_revenue, max(updated) as updated ")
        .append(
            "from fact_revenue_adnet_vw_daily where start >= :prev_start and start < :prev_stop and publisher_id = :publisherId and (tag_monetization=1 or tag_monetization=-1) "
                + (withSitesParam ? "and site_id in (:sites)" : "")
                + " group by site_id, zone, adnet_id, tag_id")
        .append(") as t group by t.site_id, t.zone, t.adnet_id, t.tag_id order by tag_id")
        .toString();
  }

  private static String getSiteSummaryQuery() {
    return new StringBuilder()
        .append(
            "select t.site_id as site_id, t.adnet_id as adnet_id, t.tag_id as tag_id, t.zone as position, sum(t.current_inbound_reqs) as current_inbound_reqs, sum(t.current_outbound_reqs) as current_outbound_reqs, ")
        .append(
            "sum(t.current_ads_served) as current_ads_served, sum(t.current_ads_delivered) as current_ads_delivered, sum(t.current_ads_clicked) as current_ads_clicked, sum(t.current_revenue) as current_revenue, sum(t.prev_revenue) as prev_revenue, max(t.updated) as updated ")
        .append("from (")
        .append(
            "select site_id, zone, adnet_id, tag_id, sum(ads_requested_site) as current_inbound_reqs, sum(ads_requested_adnet) as current_outbound_reqs, sum(ads_served) as current_ads_served, sum(ads_delivered) as current_ads_delivered, ")
        .append(
            "sum(ads_clicked) as current_ads_clicked, sum(revenue) as current_revenue, 0.0 as prev_revenue, max(updated) as updated ")
        .append(
            "from fact_revenue_adnet_vw_daily where start >= :current_start and start < :current_stop and site_id = :siteId group by site_id, zone, adnet_id, tag_id ")
        .append("union all ")
        .append(
            "select site_id, zone, adnet_id, tag_id, 0 as current_inbound_reqs, 0 as current_outbound_reqs, 0 as current_ads_served, 0 as current_ads_delivered, 0 as current_ads_clicked, 0.0 as current_revenue, sum(revenue) as prev_revenue, max(updated) as updated ")
        .append(
            "from fact_revenue_adnet_vw_daily where start >= :prev_start and start < :prev_stop and site_id = :siteId group by site_id, zone, adnet_id, tag_id")
        .append(") as t group by t.site_id, t.zone, t.adnet_id, t.tag_id order by tag_id")
        .toString();
  }
}
