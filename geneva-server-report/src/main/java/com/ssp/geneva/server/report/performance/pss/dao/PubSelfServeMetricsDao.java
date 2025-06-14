package com.ssp.geneva.server.report.performance.pss.dao;

import com.google.common.base.Joiner;
import com.nexage.admin.dw.util.DateUtil;
import com.ssp.geneva.server.report.performance.pss.model.PubSelfServeMetrics;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class PubSelfServeMetricsDao {

  private final NamedParameterJdbcTemplate dwNamedTemplate;

  public PubSelfServeMetricsDao(
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate) {
    this.dwNamedTemplate = dwNamedTemplate;
  }

  private static String dashboardSummaryQuery = getDashboardSummaryQuery(false);
  private static String dashboardSummaryQueryWithSites = getDashboardSummaryQuery(true);
  private static String siteSummaryQuery = getSiteSummaryQuery();

  public Map<Long, List<PubSelfServeMetrics>> getDashboardSummaryMetrics(
      long pubId, Set<Long> sites, Date start, Date stop) {

    String query = sites.isEmpty() ? dashboardSummaryQuery : dashboardSummaryQueryWithSites;
    String[] prevdates = DateUtil.getPreviousRelativeDateRange(start, stop);
    SqlParameterSource paramSource =
        new MapSqlParameterSource("current_start", start)
            .addValue("current_stop", stop)
            .addValue("prev_start", prevdates[0])
            .addValue("prev_stop", prevdates[1])
            .addValue("publisherId", pubId)
            .addValue("sites", sites);

    log.debug(
        "Query parameters: current start {}, current stop {}, prev start {}, prev stop {}, pub {}, sites {}",
        start,
        stop,
        prevdates[0],
        prevdates[1],
        pubId,
        Joiner.on(", ").join(sites));
    log.debug(query);

    final Map<Long, List<PubSelfServeMetrics>> metrics = new HashMap<>();

    dwNamedTemplate.query(
        query,
        paramSource,
        rs -> {
          List<PubSelfServeMetrics> list = metrics.get(rs.getLong(1));
          if (list == null) {
            list = new ArrayList<>();
            metrics.put(rs.getLong(1), list);
          }

          list.add(
              new PubSelfServeMetrics(
                  rs.getLong(1),
                  rs.getLong(2),
                  rs.getLong(3),
                  rs.getString(4),
                  rs.getLong(5),
                  rs.getLong(6),
                  rs.getLong(7),
                  rs.getLong(8),
                  rs.getLong(9),
                  rs.getDouble(10),
                  rs.getLong(11),
                  rs.getLong(12),
                  rs.getLong(13),
                  rs.getLong(14),
                  rs.getLong(15),
                  rs.getDouble(16),
                  rs.getTimestamp(17)));
        });

    return metrics;
  }

  public List<PubSelfServeMetrics> getSiteSummaryMetrics(long siteId, Date start, Date stop) {
    String[] prevdates = DateUtil.getPreviousRelativeDateRange(start, stop);
    SqlParameterSource paramSource =
        new MapSqlParameterSource("current_start", start)
            .addValue("current_stop", stop)
            .addValue("prev_start", prevdates[0])
            .addValue("prev_stop", prevdates[1])
            .addValue("siteId", siteId);
    if (log.isDebugEnabled())
      log.debug(
          "Query Parameters: current start {}, current stop {}, prev start {}, prev stop {}, site {}",
          start,
          stop,
          prevdates[0],
          prevdates[1],
          siteId);
    return dwNamedTemplate.query(
        siteSummaryQuery,
        paramSource,
        (rs, rowNum) ->
            new PubSelfServeMetrics(
                rs.getLong(1),
                rs.getLong(2),
                rs.getLong(3),
                rs.getString(4),
                rs.getLong(5),
                rs.getLong(6),
                rs.getLong(7),
                rs.getLong(8),
                rs.getLong(9),
                rs.getDouble(10),
                0,
                0,
                0,
                0,
                0,
                rs.getDouble(11),
                rs.getTimestamp(12)));
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
