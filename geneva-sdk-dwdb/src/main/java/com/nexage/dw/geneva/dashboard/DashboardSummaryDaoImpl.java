package com.nexage.dw.geneva.dashboard;

import com.nexage.admin.dw.reports.dao.BaseReportDao;
import com.nexage.dw.geneva.dashboard.model.BuyerDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.NexageDashboardSummary;
import com.nexage.dw.geneva.dashboard.model.SellerDashboardSummary;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository(value = "dashboardSummaryDao")
public class DashboardSummaryDaoImpl extends BaseReportDao implements DashboardSummaryDao {

  private final NamedParameterJdbcTemplate dwNamedTemplate;

  public DashboardSummaryDaoImpl(
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate) {
    this.dwNamedTemplate = dwNamedTemplate;
  }

  private static String sellerMetricsQuery = null;
  private static String sellerMetricsforNexageUserQuery = null;
  private static String buyersMetricsQuery = null;
  private static String buyerMetricsforNexageUserQuery = null;
  // Commenting out the query with both buyers and seller data
  // private static String nexageMetricsQuery = null;

  static {
    sellerMetricsQuery = getSellersMetricsQuery();
    buyersMetricsQuery = getBuyersMetricsQuery();
    sellerMetricsforNexageUserQuery = getSellerMetricsforNexageUserQuery();
    buyerMetricsforNexageUserQuery = getBuyerMetricsforNexageUserQuery();
    // Buyer metrics deferred to phase 2 of dashboard when we roll out buyer functionality with
    // geneva per Eddie Steele discussion 6/2/14
    // Commenting out the query with both buyers and seller data
    // nexageMetricsQuery = getNexageMetricsQuery();
    // Using the query with only seller data

  }

  @Override
  public BuyerDashboardSummary getBuyerMetrics(
      final String start, final String stop, final Set<Long> companyPids) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("companyPids", companyPids)
            .addValue("start", start)
            .addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for BuyerMetrics : " + buyersMetricsQuery);
    }

    return dwNamedTemplate.query(
        buyersMetricsQuery, paramSource, new BuyerMetricsResultExtractor());
  }

  @Override
  public BuyerDashboardSummary getBuyerMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for BuyerMetrics : " + buyerMetricsforNexageUserQuery);
    }

    return dwNamedTemplate.query(
        buyerMetricsforNexageUserQuery, paramSource, new BuyerMetricsResultExtractor());
  }

  @Override
  public SellerDashboardSummary getSellerMetrics(
      final String start, final String stop, final Set<Long> companyPids) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("companyPids", companyPids)
            .addValue("start", start)
            .addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for Summary SellerMetrics : " + sellerMetricsQuery);
    }

    return dwNamedTemplate.query(
        sellerMetricsQuery, paramSource, new SellerMetricsResultExtractor());
  }

  @Override
  public SellerDashboardSummary getSellerMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);
    if (log.isDebugEnabled()) {
      log.debug("SQL for Summary SellerMetrics : " + sellerMetricsforNexageUserQuery);
    }

    return dwNamedTemplate.query(
        sellerMetricsforNexageUserQuery, paramSource, new SellerMetricsResultExtractor());
  }

  @Override
  public NexageDashboardSummary getNexageMetrics(final String start, final String stop) {

    SqlParameterSource paramSource =
        new MapSqlParameterSource("start", start).addValue("stop", stop);

    // Getting only Seller Metrics because of performance issues
    if (log.isDebugEnabled()) {
      log.debug("SQL for Summary NexageMetrics Query : " + sellerMetricsforNexageUserQuery);
    }

    return dwNamedTemplate.query(
        sellerMetricsforNexageUserQuery, paramSource, new NexageMetricsResultExtractor());
  }

  private class BuyerMetricsResultExtractor implements ResultSetExtractor<BuyerDashboardSummary> {
    @Override
    public BuyerDashboardSummary extractData(final ResultSet rs)
        throws SQLException, DataAccessException {
      BuyerDashboardSummary buyerMetrics = null;
      while (rs.next()) {
        buyerMetrics =
            new BuyerDashboardSummary(
                rs.getLong("bidsRequests"),
                rs.getLong("bidsReceived"),
                rs.getLong("bidsWon"),
                rs.getLong("adsDelivered"),
                rs.getBigDecimal("revenue"));
      }
      return buyerMetrics;
    }
  }

  private class SellerMetricsResultExtractor implements ResultSetExtractor<SellerDashboardSummary> {
    @Override
    public SellerDashboardSummary extractData(final ResultSet rs)
        throws SQLException, DataAccessException {
      SellerDashboardSummary sellerMetrics = null;
      while (rs.next()) {
        sellerMetrics =
            new SellerDashboardSummary(
                rs.getLong("numOfRequests"),
                rs.getLong("numOfServed"),
                rs.getLong("numOfClicks"),
                rs.getLong("numOfDelivered"),
                rs.getBigDecimal("revenue"));
      }
      return sellerMetrics;
    }
  }

  private class NexageMetricsResultExtractor implements ResultSetExtractor<NexageDashboardSummary> {
    @Override
    public NexageDashboardSummary extractData(final ResultSet rs)
        throws SQLException, DataAccessException {
      SellerDashboardSummary sellerMetrics = null;
      BuyerDashboardSummary buyerMetrics = null;
      while (rs.next()) {
        // Getting only Seller Metrics for performance reasons.
        sellerMetrics =
            new SellerDashboardSummary(
                rs.getLong("numOfRequests"),
                rs.getLong("numOfServed"),
                rs.getLong("numOfClicks"),
                rs.getLong("numOfDelivered"),
                rs.getBigDecimal("revenue"));
      }
      return new NexageDashboardSummary(sellerMetrics, buyerMetrics);
    }
  }

  private static String getBuyerMetricsforNexageUserQuery() {

    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT SUM(t.auctions) AS numOfAuctions, ")
        .append(" SUM(t.bids_requested) AS bidsRequests,")
        .append(" SUM(t.bids_received) AS bidsReceived,")
        .append(" SUM(t.bids_won) AS bidsWon,")
        .append(" SUM(t.ads_delivered) AS adsDelivered,")
        .append(" SUM(t.revenue) AS revenue")
        .append(" FROM   ( ")
        .append("  SELECT 0 AS auctions,")
        .append("  0 AS bids_requested,")
        .append("  0 AS bids_received,")
        .append("  0 AS bids_won,")
        .append("  SUM(f2.ads_delivered) AS ads_delivered,")
        .append("  CAST(SUM(f2.revenue) AS DECIMAL(18, 8)) AS revenue ")
        .append("  FROM fact_exchange_wins f2 ")
        .append("  WHERE   1 = 1 ")
        .append("  AND f2.start >= :start ")
        .append("  AND f2.start <= :stop ")
        .append("  AND f2.stop <= :stop ")
        .append("  UNION ALL")
        .append("  SELECT 0 AS auctions,")
        .append("  SUM(f3.bid_requests) AS bids_requested,")
        .append("  SUM(f3.bid_responses) AS bids_received,")
        .append("  SUM(f3.bids_won) AS bids_won,")
        .append("  0 AS ads_delivered,")
        .append("  CAST(0 AS DECIMAL(18, 8)) AS revenue ")
        .append("  FROM ")
        .append("  fact_exchange_bidders f3 ")
        .append("  WHERE   1 = 1 ")
        .append("  AND f3.start >= :start ")
        .append("  AND f3.start <= :stop ")
        .append("  AND f3.stop <= :stop ")
        .append(" ) AS t ");

    return sb.toString();
  }

  private static String getBuyersMetricsQuery() {

    StringBuilder sb = new StringBuilder();
    sb.append(" SELECT SUM(t.auctions) AS numOfAuctions, ")
        .append(" SUM(t.bids_requested) AS bidsRequests,")
        .append(" SUM(t.bids_received) AS bidsReceived,")
        .append(" SUM(t.bids_won) AS bidsWon,")
        .append(" SUM(t.ads_delivered) AS adsDelivered,")
        .append(" SUM(t.revenue) AS revenue")
        .append(" FROM   ( ")
        .append("  SELECT 0 AS auctions,")
        .append("  0 AS bids_requested,")
        .append("  0 AS bids_received,")
        .append("  0 AS bids_won,")
        .append("  SUM(f2.ads_delivered) AS ads_delivered,")
        .append("  CAST(SUM(f2.revenue) AS DECIMAL(18, 8)) AS revenue ")
        .append("  FROM fact_exchange_wins f2 ")
        .append("  WHERE   1 = 1 ")
        .append("  AND f2.start >= :start ")
        .append("  AND f2.start <= :stop ")
        .append("  AND f2.stop <= :stop ")
        .append(
            "  AND f2.bidder_id IN (SELECT id FROM dim_bidder WHERE company_id in :companyPids) ")
        .append("  UNION ALL")
        .append("  SELECT 0 AS auctions,")
        .append("  SUM(f3.bid_requests) AS bids_requested,")
        .append("  SUM(f3.bid_responses) AS bids_received,")
        .append("  SUM(f3.bids_won) AS bids_won,")
        .append("  0 AS ads_delivered,")
        .append("  CAST(0 AS DECIMAL(18, 8)) AS revenue ")
        .append("  FROM ")
        .append("  fact_exchange_bidders f3 ")
        .append("  WHERE   1 = 1 ")
        .append("  AND f3.start >= :start ")
        .append("  AND f3.start <= :stop ")
        .append("  AND f3.stop <= :stop ")
        .append(
            "  AND f3.bidder_id IN (SELECT id FROM dim_bidder WHERE company_id in :companyPids) ")
        .append(" ) AS t ");

    return sb.toString();
  }

  private static String getSellersMetricsQuery() {

    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT ")
        .append(" SUM(t.ads_requested) AS  numOfRequests, ")
        .append(" SUM(t.ads_served) AS numOfServed, ")
        .append(" SUM(t.ads_clicked) AS numOfClicks, ")
        .append(" SUM(t.ads_delivered) AS numOfDelivered, ")
        .append(" SUM(t.revenue) AS revenue ")
        .append(" FROM ")
        .append(" ( ")
        .append(" SELECT ")
        .append(" SUM(f1.ads_requested_site) AS ads_requested,")
        .append(" 0 AS ads_served, ")
        .append(" 0 AS ads_clicked, ")
        .append(" 0 AS ads_delivered, ")
        .append(" CAST(0 AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f1 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND DATE(f1.start) >= :start ")
        .append(" AND DATE(f1.start) <= :stop ")
        .append(" AND DATE(f1.stop) <= :stop ")
        .append(" UNION ALL ")
        .append("  SELECT ")
        .append(" 0 AS ads_requested, ")
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
        .append(" AND f2.site_id IN  (SELECT id FROM dim_site WHERE company_id in :companyPids) ")
        .append(" AND f2.tag_id  IN  (SELECT id FROM dim_tag WHERE monetization = 1) ")
        .append(" ) AS t ");
    return sb.toString();
  }

  private static String getSellerMetricsforNexageUserQuery() {

    StringBuilder sb = new StringBuilder();

    sb.append(" SELECT ")
        .append(" SUM(t.ads_requested) AS  numOfRequests, ")
        .append(" SUM(t.ads_served) AS numOfServed, ")
        .append(" SUM(t.ads_clicked) AS numOfClicks, ")
        .append(" SUM(t.ads_delivered) AS numOfDelivered, ")
        .append(" SUM(t.revenue) AS revenue ")
        .append(" FROM ")
        .append(" ( ")
        .append(" SELECT ")
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
        .append(" UNION ALL ")
        .append(" SELECT ")
        .append(" 0 AS ads_requested, ")
        .append(" SUM(f2.ads_served) AS ads_served, ")
        .append(" SUM(f2.ads_clicked)  AS ads_clicked, ")
        .append(" SUM(f2.ads_delivered) AS ads_delivered, ")
        .append(" CAST(SUM(f2.revenue) AS DECIMAL (18 , 8 )) AS revenue ")
        .append(" FROM ")
        .append(" fact_revenue_adnet_vw_daily f2 ")
        .append(" WHERE   1 = 1 ")
        .append(" AND DATE(f2.start) >= :start ")
        .append(" AND DATE(f2.start) <= :stop ")
        .append(" AND DATE(f2.stop) <= :stop ")
        .append(" AND f2.tag_id  IN   (SELECT id FROM dim_tag WHERE monetization = 1) ")
        .append(" ) AS t ");

    return sb.toString();
  }
}
