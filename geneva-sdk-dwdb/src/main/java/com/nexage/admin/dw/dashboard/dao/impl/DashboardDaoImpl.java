package com.nexage.admin.dw.dashboard.dao.impl;

import com.google.common.base.Joiner;
import com.nexage.admin.dw.dashboard.dao.DashboardDao;
import com.nexage.admin.dw.dashboard.model.BuyerKeyMetrics;
import com.nexage.admin.dw.reports.dao.BaseReportDao;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class DashboardDaoImpl extends BaseReportDao implements DashboardDao {

  public BuyerKeyMetrics getBuyerMetrics(String start, String stop, Long companyId) {
    String bidderQuery = "SELECT id FROM dim_bidder WHERE company_id=?";

    List<Long> bidderPids =
        jdbcTemplate.query(bidderQuery, new Object[] {companyId}, (rs, rowNum) -> rs.getLong(1));

    StringBuilder bidderInClause =
        new StringBuilder().append("(").append(Joiner.on(",").join(bidderPids)).append(")");

    StringBuilder sql1 =
        new StringBuilder()
            .append(
                "SELECT SUM(t.auctions), SUM(t.bids_requested), SUM(t.bids_received), SUM(t.bids_won), SUM(t.ads_delivered), SUM(t.revenue) FROM (");

    StringBuilder sql2 =
        new StringBuilder()
            .append(
                "SELECT SUM(f1.auctions) AS auctions, 0 AS bids_requested, 0 AS bids_received, 0 AS bids_won, 0 AS ads_delivered, CAST(0 AS DECIMAL(18, 8)) AS revenue ")
            .append("FROM fact_exchange_auctions f1 WHERE f1.start >= '")
            .append(start)
            .append("' AND f1.start <= '")
            .append(stop)
            .append("' AND f1.stop <= '")
            .append(stop)
            .append("'");

    StringBuilder sql3 =
        new StringBuilder()
            .append(" union all ")
            .append(
                "SELECT 0 AS auctions, 0 AS bids_requested, 0 AS bids_received, 0 AS bids_won, SUM(f2.ads_delivered) AS ads_delivered, CAST(SUM(f2.revenue) AS DECIMAL(18, 8)) AS revenue ")
            .append("FROM fact_exchange_wins f2 ")
            .append("WHERE f2.bidder_id in ")
            .append(bidderInClause)
            .append(" AND f2.start >= '")
            .append(start)
            .append("' AND f2.start <= '")
            .append(stop)
            .append("' AND f2.stop <= '")
            .append(stop)
            .append("'");

    StringBuilder sql4 =
        new StringBuilder()
            .append(" union all ")
            .append(
                "SELECT 0 AS auctions, SUM(f3.bid_requests) AS bids_requested, SUM(f3.bid_responses) AS bids_received, SUM(f3.bids_won) AS bids_won, 0 AS ads_delivered, CAST(0 AS DECIMAL(18, 8)) AS revenue ")
            .append("FROM fact_exchange_bidders f3 ")
            .append("WHERE f3.bidder_id in ")
            .append(bidderInClause)
            .append(" AND f3.start >= '")
            .append(start)
            .append("' AND f3.start <= '")
            .append(stop)
            .append("' AND f3.stop <= '")
            .append(stop)
            .append("') as t");

    String sql = new StringBuilder().append(sql1).append(sql2).append(sql3).append(sql4).toString();

    log.debug("Query to get buyer key metrics for selected buyers: " + sql);

    return this.jdbcTemplate.queryForObject(
        sql,
        (rs, rowNum) -> {
          BuyerKeyMetrics report =
              new BuyerKeyMetrics(
                  rs.getLong(1),
                  rs.getLong(2),
                  rs.getLong(3),
                  rs.getLong(4),
                  rs.getLong(5),
                  rs.getBigDecimal(6));
          return report;
        });
  }
}
