package com.nexage.dw.geneva.dashboard;

import com.nexage.admin.dw.reports.dao.BaseReportDao;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.NexageDashboardDetail;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardDetail;
import java.math.BigDecimal;
import java.util.ArrayList;
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
@Component(value = "dashboardDetailDao")
public class DashboardDetailDaoImpl extends BaseReportDao implements DashboardDetailDao {

  private final NamedParameterJdbcTemplate dwNamedTemplate;

  private static String sellerMetricsQuery = null;
  private static String sellerMetricsforNexageUserQuery = null;
  private static String buyersMetricsQuery = null;
  private static String buyerMetricsforNexageUserQuery = null;

  public DashboardDetailDaoImpl(
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate) {
    this.dwNamedTemplate = dwNamedTemplate;
  }

  static {
    sellerMetricsQuery = getSellersMetricsQuery();
    buyersMetricsQuery = getBuyersMetricsQuery();
    sellerMetricsforNexageUserQuery = getSellerMetricsforNexageUserQuery();
    buyerMetricsforNexageUserQuery = getBuyerMetricsforNexageUserQuery();
  }

  @Override
  public List<BuyerDashboardDetail> getBuyerMetrics(
      final String start, final String stop, final Set<Long> companyPids) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("companyPid", companyPids)
            .addValue("start", start)
            .addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for BuyerMetrics : " + buyersMetricsQuery);
    }

    List<Map<String, Object>> fromDb =
        dwNamedTemplate.queryForList(buyersMetricsQuery, paramSource);
    List<BuyerDashboardDetail> metrics = new ArrayList<>();
    for (Map<String, Object> metric : fromDb) {
      metrics.add(
          new BuyerDashboardDetail(
              metric.get("ts").toString(),
              ((BigDecimal) metric.get("bidsRequests")).longValue(),
              ((BigDecimal) metric.get("bidsReceived")).longValue(),
              ((BigDecimal) metric.get("bidsWon")).longValue(),
              ((BigDecimal) metric.get("adsDelivered")).longValue(),
              (BigDecimal) metric.get("revenue")));
    }
    return metrics;
  }

  @Override
  public List<BuyerDashboardDetail> getBuyerMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for BuyerMetrics : " + buyerMetricsforNexageUserQuery);
    }

    List<Map<String, Object>> fromDb =
        dwNamedTemplate.queryForList(buyerMetricsforNexageUserQuery, paramSource);
    List<BuyerDashboardDetail> metrics = new ArrayList<>();
    for (Map<String, Object> metric : fromDb) {
      metrics.add(
          new BuyerDashboardDetail(
              metric.get("ts").toString(),
              ((BigDecimal) metric.get("bidsRequests")).longValue(),
              ((BigDecimal) metric.get("bidsReceived")).longValue(),
              ((BigDecimal) metric.get("bidsWon")).longValue(),
              ((BigDecimal) metric.get("adsDelivered")).longValue(),
              (BigDecimal) metric.get("revenue")));
    }
    return metrics;
  }

  @Override
  public List<SellerDashboardDetail> getSellerMetrics(
      final String start, final String stop, final Set<Long> companyPids) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("companyPids", companyPids)
            .addValue("start", start)
            .addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for SellerMetrics : " + sellerMetricsQuery);
    }

    List<Map<String, Object>> fromDb =
        dwNamedTemplate.queryForList(sellerMetricsQuery, paramSource);
    List<SellerDashboardDetail> metrics = new ArrayList<>();
    for (Map<String, Object> metric : fromDb) {
      metrics.add(
          new SellerDashboardDetail(
              metric.get("ts").toString(),
              ((BigDecimal) metric.get("numOfRequests")).longValue(),
              ((BigDecimal) metric.get("numOfServed")).longValue(),
              ((BigDecimal) metric.get("numOfClicks")).longValue(),
              ((BigDecimal) metric.get("numOfDelivered")).longValue(),
              (BigDecimal) metric.get("revenue")));
    }
    return metrics;
  }

  @Override
  public List<SellerDashboardDetail> getSellerMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for SellerMetrics : " + sellerMetricsforNexageUserQuery);
    }

    List<Map<String, Object>> fromDb =
        dwNamedTemplate.queryForList(sellerMetricsforNexageUserQuery, paramSource);
    List<SellerDashboardDetail> metrics = new ArrayList<>();
    for (Map<String, Object> metric : fromDb) {
      metrics.add(
          new SellerDashboardDetail(
              metric.get("ts").toString(),
              ((BigDecimal) metric.get("numOfRequests")).longValue(),
              ((BigDecimal) metric.get("numOfServed")).longValue(),
              ((BigDecimal) metric.get("numOfClicks")).longValue(),
              ((BigDecimal) metric.get("numOfDelivered")).longValue(),
              (BigDecimal) metric.get("revenue")));
    }
    return metrics;
  }

  @Override
  public NexageDashboardDetail getNexageMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);
    // Getting only Seller Metrics because of performance issues
    if (log.isDebugEnabled()) {
      log.debug("SQL for NexageMetrics Query : " + sellerMetricsforNexageUserQuery);
    }

    List<Map<String, Object>> fromDb =
        dwNamedTemplate.queryForList(sellerMetricsforNexageUserQuery, paramSource);
    List<SellerDashboardDetail> sellerMetrics = new ArrayList<>();
    List<BuyerDashboardDetail> buyerMetrics = new ArrayList<>();
    for (Map<String, Object> metric : fromDb) {
      sellerMetrics.add(
          new SellerDashboardDetail(
              metric.get("ts").toString(),
              ((BigDecimal) metric.get("numOfRequests")).longValue(),
              ((BigDecimal) metric.get("numOfServed")).longValue(),
              ((BigDecimal) metric.get("numOfClicks")).longValue(),
              ((BigDecimal) metric.get("numOfDelivered")).longValue(),
              (BigDecimal) metric.get("revenue")));
    }
    return new NexageDashboardDetail(sellerMetrics, buyerMetrics);
  }

  private static String getBuyersMetricsQuery() {
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT t.ts AS ts, ")
        .append(" SUM(t.auctions) AS numOfAuctions,")
        .append(" SUM(t.bids_requested) AS bidsRequests, ")
        .append(" SUM(t.bids_received) AS bidsReceived, ")
        .append(" SUM(t.bids_won) AS bidsWon, ")
        .append(" SUM(t.ads_delivered) AS adsDelivered,")
        .append(" SUM(t.revenue) AS revenue")
        .append(" FROM   ( ")
        .append(" SELECT DATE(f2.start) AS ts, ")
        .append(" 0 AS auctions,")
        .append(" 0 AS bids_requested,")
        .append(" 0 AS bids_received,")
        .append(" 0 AS bids_won, ")
        .append(" SUM(f2.ads_delivered) AS ads_delivered,")
        .append(" CAST(SUM(f2.revenue) AS DECIMAL(18, 8)) AS revenue")
        .append(" FROM ")
        .append(" fact_exchange_wins f2 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND f2.start >= :start ")
        .append(" AND f2.start <= :stop ")
        .append(" AND f2.stop <= :stop ")
        .append(" AND f2.bidder_id IN (SELECT id FROM dim_bidder ")
        .append(" WHERE company_id = :companyPid) ")
        .append(" GROUP BY ts ")
        .append(" UNION ALL")
        .append(" SELECT DATE(f3.start) AS ts, ")
        .append(" 0 AS auctions,")
        .append(" SUM(f3.bid_requests) AS bids_requested,")
        .append(" SUM(f3.bid_responses) AS bids_received,")
        .append(" SUM(f3.bids_won) AS bids_won,")
        .append(" 0 AS ads_delivered,")
        .append(" CAST(0 AS DECIMAL(18, 8)) AS revenue")
        .append(" FROM ")
        .append(" fact_exchange_bidders f3")
        .append(" WHERE   1 = 1 ")
        .append(" AND f3.start >= :start ")
        .append(" AND f3.start <= :stop ")
        .append(" AND f3.stop <= :stop")
        .append(" AND f3.bidder_id IN (SELECT id FROM dim_bidder")
        .append(" WHERE company_id = :companyPid) ")
        .append(" GROUP BY ts")
        .append(" ) AS t ")
        .append(" GROUP BY t.ts ORDER BY t.ts");

    return sb.toString();
  }

  private static String getBuyerMetricsforNexageUserQuery() {
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT t.ts AS ts, ")
        .append(" SUM(t.auctions) AS numOfAuctions,")
        .append(" SUM(t.bids_requested) AS bidsRequests, ")
        .append(" SUM(t.bids_received) AS bidsReceived, ")
        .append(" SUM(t.bids_won) AS bidsWon, ")
        .append(" SUM(t.ads_delivered) AS adsDelivered,")
        .append(" SUM(t.revenue) AS revenue")
        .append(" FROM   ( ")
        .append(" SELECT DATE(f2.start) AS ts, ")
        .append(" 0 AS auctions,")
        .append(" 0 AS bids_requested,")
        .append(" 0 AS bids_received,")
        .append(" 0 AS bids_won, ")
        .append(" SUM(f2.ads_delivered) AS ads_delivered,")
        .append(" CAST(SUM(f2.revenue) AS DECIMAL(18, 8)) AS revenue")
        .append(" FROM ")
        .append(" fact_exchange_wins f2 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND f2.start >= :start ")
        .append(" AND f2.start <= :stop ")
        .append(" AND f2.stop <= :stop ")
        .append(" GROUP BY ts ")
        .append(" UNION ALL")
        .append(" SELECT DATE(f3.start) AS ts, ")
        .append(" 0 AS auctions,")
        .append(" SUM(f3.bid_requests) AS bids_requested,")
        .append(" SUM(f3.bid_responses) AS bids_received,")
        .append(" SUM(f3.bids_won) AS bids_won,")
        .append(" 0 AS ads_delivered,")
        .append(" CAST(0 AS DECIMAL(18, 8)) AS revenue")
        .append(" FROM ")
        .append(" fact_exchange_bidders f3")
        .append(" WHERE   1 = 1 ")
        .append(" AND f3.start >= :start ")
        .append(" AND f3.start <= :stop ")
        .append(" AND f3.stop <= :stop")
        .append(" GROUP BY ts")
        .append(" ) AS t ")
        .append(" GROUP BY t.ts ORDER BY t.ts");

    return sb.toString();
  }

  private static String getSellersMetricsQuery() {
    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT ")
        .append(" t.ts AS ts, ")
        .append(" SUM(t.ads_requested) AS  numOfRequests, ")
        .append(" SUM(t.ads_served) AS numOfServed, ")
        .append(" SUM(t.ads_clicked) AS numOfClicks, ")
        .append(" SUM(t.ads_delivered)AS numOfDelivered, ")
        .append(" SUM(t.revenue) AS revenue ")
        .append(" FROM ")
        .append(" ( ")
        .append(" SELECT ")
        .append(" DATE(f1.start) AS ts, ")
        .append(" SUM(f1.ads_requested_site) AS ads_requested,")
        .append(" 0 AS ads_served, ")
        .append(" 0 AS ads_clicked, ")
        .append(" 0 AS ads_delivered, ")
        .append(" CAST(0 AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f1 ")
        .append(" WHERE   1= 1 ")
        .append(" AND DATE(f1.start) >= :start ")
        .append(" AND DATE(f1.start) <= :stop ")
        .append(" AND DATE(f1.stop) <= :stop ")
        .append(" AND f1.site_id IN  (SELECT id FROM dim_site WHERE company_id in :companyPids) ")
        .append(" GROUP BY ts ")
        .append(" UNION ALL ")
        .append(" SELECT ")
        .append(" DATE(f2.start) AS ts, ")
        .append(" 0 AS ads_requested, ")
        .append(" SUM(f2.ads_served)  AS ads_served, ")
        .append(" SUM(f2.ads_clicked AS ads_clicked, ")
        .append(" SUM(f2.ads_delivered) AS ads_delivered, ")
        .append(" CAST(SUM(f2.revenue) AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f2 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND f2.start >= :start ")
        .append(" AND f2.start <= :stop ")
        .append(" AND f2.stop <= :stop ")
        .append(" AND f2.site_id IN  (SELECT id FROM dim_site WHERE company_id in :companyPids) ")
        .append(" AND f2.tag_monetization = 1 ")
        .append(" GROUP BY ts ")
        .append(" ) AS t ")
        .append(" GROUP BY t.ts ORDER BY t.ts ");

    return sb.toString();
  }

  private static String getSellerMetricsforNexageUserQuery() {
    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT ")
        .append(" t.ts AS ts, ")
        .append(" SUM(t.ads_requested) AS  numOfRequests, ")
        .append(" SUM(t.ads_served) AS numOfServed, ")
        .append(" SUM(t.ads_clicked) AS numOfClicks, ")
        .append(" SUM(t.ads_delivered) AS numOfDelivered, ")
        .append(" SUM(t.revenue) AS revenue ")
        .append(" FROM ")
        .append(" ( ")
        .append(" SELECT ")
        .append(" DATE(f1.start) AS ts, ")
        .append(" SUM(f1.ads_requested_site) AS ads_requested,")
        .append(" 0 AS ads_served, ")
        .append(" 0 AS ads_clicked, ")
        .append(" 0 AS ads_delivered, ")
        .append(" CAST(0 AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f1 ")
        .append(" WHERE   1= 1 ")
        .append(" AND DATE(f1.start) >= :start ")
        .append(" AND DATE(f1.start) <= :stop ")
        .append(" AND DATE(f1.stop) <= :stop ")
        .append(" GROUP BY ts ")
        .append(" UNION ALL ")
        .append(" SELECT ")
        .append(" DATE(f2.start) AS ts, ")
        .append(" SUM(f2.ads_requested_site) AS ads_requested, ")
        .append(" SUM(f2.ads_served) AS ads_served, ")
        .append(" SUM(f2.ads_clicked) AS ads_clicked, ")
        .append(" SUM(f2.ads_delivered) AS ads_delivered, ")
        .append(" CAST(SUM(f2.revenue) AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f2 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND DATE(f2.start) >= :start ")
        .append(" AND DATE(f2.start) <= :stop ")
        .append(" AND DATE(f2.stop) <= :stop ")
        .append(" AND f2.tag_monetization = 1 ")
        .append(" GROUP BY ts ")
        .append(" ) AS t ")
        .append(" GROUP BY t.ts ORDER BY t.ts ");
    return sb.toString();
  }
}
