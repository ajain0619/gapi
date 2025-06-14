package com.ssp.geneva.server.report.performance.pss.dao;

import com.nexage.admin.dw.reports.helper.ReportQueryHelper;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository(value = "biddersPerformance")
public class BiddersPerformancePubSelfServeDaoImpl implements BiddersPerformancePubSelfServeDao {

  private final NamedParameterJdbcTemplate dwNamedTemplate;

  private static final int DEFAULT_SCALING = 8;

  private static final String BIDDERS_PERFORMANCE_QUERY;

  public BiddersPerformancePubSelfServeDaoImpl(
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate) {
    this.dwNamedTemplate = dwNamedTemplate;
  }

  static {
    BIDDERS_PERFORMANCE_QUERY = getBiddersPerformanceQuery();
  }

  @Override
  public List<BiddersPerformanceForPubSelfServe> getBiddersPerformancePss(
      final String start, final String stop, final Set<Long> sitePids, final String loggedInUser) {
    MapSqlParameterSource paramSource =
        new MapSqlParameterSource("sitePids", sitePids)
            .addValue("start", start)
            .addValue("stop", stop);
    ReportQueryHelper queryHelper = new ReportQueryHelper(null, null);

    String queryToRun = queryHelper.addSQLComment(BIDDERS_PERFORMANCE_QUERY, loggedInUser);

    if (log.isDebugEnabled()) {
      log.info("SQL for BiddersPerformanceForPubSelfServe : {}", queryToRun);
    }

    SqlRowSet rowSet = dwNamedTemplate.queryForRowSet(queryToRun, paramSource);
    List<BiddersPerformanceForPubSelfServe> performances = new ArrayList<>();
    while (rowSet.next()) {
      var grossAcquisitionCost = rowSet.getBigDecimal("grossAcquisitionCost");
      var netAcquisitionCost = rowSet.getBigDecimal("netAcquisitionCost");
      if (grossAcquisitionCost != null && netAcquisitionCost != null) {
        performances.add(
            new BiddersPerformanceForPubSelfServe(
                rowSet.getLong("bidRequests"),
                rowSet.getLong("bidReceived"),
                rowSet.getLong("wins"),
                rowSet.getLong("delivered"),
                grossAcquisitionCost.setScale(DEFAULT_SCALING, BigDecimal.ROUND_CEILING),
                netAcquisitionCost.setScale(DEFAULT_SCALING, BigDecimal.ROUND_CEILING),
                rowSet.getLong("bids"),
                rowSet.getLong("bidderId"),
                rowSet.getString("bidderName")));
      }
    }

    return performances;
  }

  private static String getBiddersPerformanceQuery() {
    StringBuilder query = new StringBuilder();
    query
        .append("select sum(vw.bid_requests) as bidRequests, ")
        .append("sum(vw.bid_responses) as bidReceived, ")
        .append("sum(vw.bids) as bids, ")
        .append("sum(vw.bids_won) as wins, ")
        .append("sum(vw.ads_delivered) as delivered, ")
        .append("sum(vw.price_sum) as grossAcquisitionCost, ")
        .append("sum(vw.revenue) as netAcquisitionCost, ")
        .append("vw.bidder_id as bidderId, ")
        .append("d.name as bidderName ")
        .append("FROM (select  ")
        .append("sum(f2.bid_requests) as bid_requests, ")
        .append("sum(f2.bid_responses) as bid_responses, ")
        .append("sum(f2.bids) as bids, ")
        .append("sum(f2.bids_won) as bids_won, ")
        .append("0 as ads_delivered, ")
        .append("cast(0 as decimal(16,8)) as price_sum, ")
        .append("cast(0 as decimal(16,8)) as revenue, ")
        .append("f2.bidder_id ")
        .append("from ( select  ")
        .append("sum(bid_requests) as bid_requests, ")
        .append("sum(bid_responses) as bid_responses, ")
        .append("sum(bids) as bids, ")
        .append("sum(bids_won) as bids_won, ")
        .append("0 as ads_delivered, ")
        .append("cast(0 as decimal(16,8)) as price_sum, ")
        .append("cast(0 as decimal(16,8)) as revenue, ")
        .append("f.bidder_id ")
        .append("from fact_exchange_bidders f ")
        .append("where 1=1 ")
        .append("AND ")
        .append("f.start >= :start ")
        .append("AND ")
        .append("f.start < :stop ")
        .append("AND ")
        .append("f.site_id IN (:sitePids) ")
        .append("GROUP BY f.bidder_id ")
        .append("UNION ALL ")
        .append("select  0  as bid_requests, ")
        .append("0  as bid_responses, ")
        .append("0  as bids, ")
        .append("0  as bids_won, ")
        .append("0 as ads_delivered, ")
        .append("cast(0 as decimal(16,8)) as price_sum, ")
        .append("cast(0 as decimal(16,8)) as revenue, ")
        .append("f.bidder_id ")
        .append("from fact_exchange_seatbids f ")
        .append("where 1=1 ")
        .append("AND ")
        .append("f.start >= :start ")
        .append("AND ")
        .append("f.start < :stop ")
        .append("AND ")
        .append("f.site_id IN (:sitePids) ")
        .append("GROUP BY f.bidder_id ) f2 ")
        .append("where 1=1 ")
        .append("GROUP BY f2.bidder_id ")
        .append("UNION ALL ")
        .append("select 0 as bid_requests, 0 as bid_responses, 0 as bids, 0 as bids_won, ")
        .append(
            "sum(f.ads_delivered) as ads_delivered, sum(f.price_sum) as price_sum, sum(f.revenue) - sum(f.revenue_net) as revenue , f.bidder_id ")
        .append("from fact_exchange_wins f ")
        .append("where 1=1 ")
        .append("AND ")
        .append("f.start >= :start ")
        .append("AND ")
        .append("f.start < :stop ")
        .append("AND ")
        .append("f.site_id IN (:sitePids) ")
        .append("GROUP BY f.bidder_id ) vw  ")
        .append("INNER JOIN dim_bidder d ON d.id = vw.bidder_id  ")
        .append("GROUP BY vw.bidder_id, d.name ")
        .toString();

    return query.toString();
  }
}
