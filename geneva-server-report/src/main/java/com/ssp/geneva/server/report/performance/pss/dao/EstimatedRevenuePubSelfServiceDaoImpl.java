package com.ssp.geneva.server.report.performance.pss.dao;

import com.nexage.admin.core.enums.Owner;
import com.nexage.admin.core.phonecast.PhoneCastConfigService;
import com.nexage.admin.dw.reports.helper.ReportQueryHelper;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdNetworksForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueByAdvertiserForPubSelfServ;
import com.ssp.geneva.server.report.performance.pss.model.EstimatedRevenueForPubSelfServe;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository(value = "estimatedRevenue")
public class EstimatedRevenuePubSelfServiceDaoImpl implements EstimatedRevenuePubSelfServiceDao {

  private final NamedParameterJdbcTemplate dwNamedTemplate;
  private final PhoneCastConfigService phoneCastConfigService;

  private Set<Long> exchangeIds;
  private Set<Long> millennialMediaBuyerPid;

  private static final String ESTIMATED_REVENUE_QUERY = getEstimatedRevenueQuery();
  private static final String ESTIMATED_REVENUE_QUERY_BY_AD_NETWORKS =
      getEstimatedRevenueQueryByAdNetworks();
  private static final String ESTIMATED_REVENUE_QUERY_BY_ADVERTISER =
      getEstimatedRevenueQueryByAdvertiser();

  public EstimatedRevenuePubSelfServiceDaoImpl(
      @Qualifier("dwNamedJdbcTemplate") NamedParameterJdbcTemplate dwNamedTemplate,
      PhoneCastConfigService phoneCastConfigService) {
    this.dwNamedTemplate = dwNamedTemplate;
    this.phoneCastConfigService = phoneCastConfigService;
  }

  @PostConstruct
  void init() {
    exchangeIds = phoneCastConfigService.getValidExchangeIds();
    millennialMediaBuyerPid = phoneCastConfigService.getMMBuyerIdList();
    log.info(
        "exchangeIds: {} , millennialMediaBuyerPid: {} ", exchangeIds, millennialMediaBuyerPid);
  }

  @Override
  public EstimatedRevenueForPubSelfServe getEstimatedRevenueForPss(
      long publisher, final LocalDate start, final LocalDate stop, String loggedInUser) {
    List<Double> revenues = new ArrayList<>(4);
    MapSqlParameterSource paramSource =
        new MapSqlParameterSource("publisher", publisher)
            .addValue("tagOwnerNexage", Owner.Nexage.ordinal())
            .addValue("tagOwnerPublisher", Owner.Publisher.ordinal())
            .addValue("exchangeIds", exchangeIds)
            .addValue("millennialMediaBuyerPid", millennialMediaBuyerPid)
            .addValue("millenialMediaAdNetIds", getMMAdnets())
            .addValue("start", start.toString())
            .addValue("stop", stop.toString());

    ReportQueryHelper queryHelper = new ReportQueryHelper(null, null);
    String queryToRun = queryHelper.addSQLComment(ESTIMATED_REVENUE_QUERY, loggedInUser);

    log.debug("SQL for EstimatedRevenueForPubSelfServe : {}", queryToRun);

    SqlRowSet rs = dwNamedTemplate.queryForRowSet(queryToRun, paramSource);
    while (rs != null && rs.next()) {
      revenues.add(rs.getDouble(1));
    }

    return new EstimatedRevenueForPubSelfServe(
        publisher, revenues.get(0), revenues.get(1), revenues.get(2), revenues.get(3));
  }

  @Override
  public EstimatedRevenueByAdNetworksForPubSelfServ getEstimatedRevenueByAdNetworksForPss(
      long publisher, final LocalDate start, final LocalDate stop, String loggedInUser) {
    MapSqlParameterSource paramSource =
        new MapSqlParameterSource("publisher", publisher)
            .addValue("tagOwnerPublisher", Owner.Publisher.ordinal())
            .addValue("millenialMediaAdNetIds", getMMAdnets())
            .addValue("exchangeIds", exchangeIds)
            .addValue("start", start.toString())
            .addValue("stop", stop.toString());

    ReportQueryHelper queryHelper = new ReportQueryHelper(null, null);
    String queryToRun =
        queryHelper.addSQLComment(ESTIMATED_REVENUE_QUERY_BY_AD_NETWORKS, loggedInUser);

    if (log.isDebugEnabled()) {
      log.info("SQL for EstimatedRevenueByAdNetworksForPubSelfServ : {}", queryToRun);
    }

    List<Map<String, Object>> fromDb = dwNamedTemplate.queryForList(queryToRun, paramSource);
    EstimatedRevenueByAdNetworksForPubSelfServ adNetRevenue =
        new EstimatedRevenueByAdNetworksForPubSelfServ();
    adNetRevenue.setPublisherPid(publisher);

    for (Map<String, Object> r : fromDb) {
      adNetRevenue.setBuyerRevenue(
          new Long(r.get("buyerId").toString()),
          (String) r.get("buyerName"),
          ((BigDecimal) r.get("revenue")).doubleValue());
    }
    return adNetRevenue;
  }

  @Override
  public EstimatedRevenueByAdvertiserForPubSelfServ getEstimatedRevenueByAdvertiserForPss(
      long publisher, final LocalDate start, final LocalDate stop, String loggedInUser) {
    MapSqlParameterSource paramSource =
        new MapSqlParameterSource("publisher", publisher)
            .addValue("tagOwnerNexage", Owner.Nexage)
            .addValue("tagOwnerPublisher", Owner.Publisher)
            .addValue("start", start.toString())
            .addValue("stop", stop.toString());

    ReportQueryHelper queryHelper = new ReportQueryHelper(null, null);
    String queryToRun =
        queryHelper.addSQLComment(ESTIMATED_REVENUE_QUERY_BY_ADVERTISER, loggedInUser);

    if (log.isDebugEnabled()) {
      log.info("SQL for EstimatedRevenueByAdvertiserForPubSelfServ : {}", queryToRun);
    }

    List<Map<String, Object>> fromDb = dwNamedTemplate.queryForList(queryToRun, paramSource);
    EstimatedRevenueByAdvertiserForPubSelfServ advertiserRevenue =
        new EstimatedRevenueByAdvertiserForPubSelfServ();
    advertiserRevenue.setPublisherPid(publisher);

    for (Map<String, Object> r : fromDb) {
      advertiserRevenue.setAdvertiserRevenue(
          new Long(r.get("advertiserId").toString()),
          (String) r.get("advertiserName"),
          ((BigDecimal) r.get("revenue")).doubleValue());
    }
    return advertiserRevenue;
  }

  private static String getEstimatedRevenueQueryByAdNetworks() {
    StringBuilder query = new StringBuilder();
    query
        .append(
            " select f.buyerId as buyerId, f.buyerName as buyerName, SUM(f.mediationRev) as revenue ")
        .append(" FROM ")
        .append(
            " (SELECT d.company_id as buyerId, dc.name as buyerName, IFNULL(SUM(f3.revenue) - SUM(f3.revenue_net), 0) AS mediationRev  ")
        .append(" FROM fact_revenue_adnet_vw_daily AS f3 ")
        .append(" INNER JOIN dim_adnet as d ON f3.adnet_id = d.id ")
        .append(" INNER JOIN dim_company as dc ON d.company_id = dc.id ")
        .append(" WHERE f3.start >= :start AND f3.start < :stop ")
        .append(" AND f3.publisher_id = :publisher ")
        .append(
            " AND f3.revenue > 0 AND f3.tag_monetization = 1 AND f3.tag_owner = :tagOwnerPublisher ")
        .append(" AND f3.adnet_id NOT IN (:millenialMediaAdNetIds, :exchangeIds) ")
        .append(" GROUP BY adnet_id, buyerId, buyerName) as f  GROUP BY buyerId, buyerName ");

    return query.toString();
  }

  private static String getEstimatedRevenueQueryByAdvertiser() {
    StringBuilder query = new StringBuilder();
    query
        .append(
            " SELECT f.advertiserId AS advertiserId, f.advertiserName AS advertiserName, f.directCampaign AS revenue ")
        .append(" FROM ")
        .append(" ( ")
        .append(
            " SELECT IFNULL(SUM(f4.revenue),0) AS directCampaign, da.id AS advertiserId, da.name AS advertiserName ")
        .append(" FROM fact_traffic_adserver AS f4 ")
        .append(" INNER JOIN dim_advertiser as da ON da.id = f4.advertiser_id ")
        .append(" WHERE f4.start >= :start AND f4.start < :stop ")
        .append(" AND f4.site_id IN (SELECT id FROM dim_site WHERE company_id = :publisher) ")
        .append(" GROUP BY advertiserId, advertiserName, revenue ) AS f ")
        .append(" WHERE f.advertiserName NOT LIKE 'HOUSE%' ");

    return query.toString();
  }

  private static String getEstimatedRevenueQuery() {
    StringBuilder query =
        new StringBuilder(
                " SELECT IFNULL(SUM(f1.revenue) - SUM(f1.revenue_net),0) AS nexageRev, 1 as sortKey ")
            .append(" FROM fact_revenue_adnet_vw_daily f1 ")
            .append(" WHERE ")
            .append(" f1.start >= :start AND f1.start < :stop AND f1.publisher_id = :publisher ")
            .append(
                " AND f1.revenue > 0 AND f1.tag_monetization = 1 AND ( f1.adnet_id IN (:exchangeIds) ")
            .append(" OR (f1.tag_owner = :tagOwnerNexage AND f1.adnet_id NOT IN (:exchangeIds))) ")
            .append(" UNION ")
            .append(
                " SELECT IFNULL(SUM(f2.revenue) - SUM(f2.revenue_net),0) AS millenialMediaRev, 2 as sortKey ")
            .append(" FROM fact_revenue_adnet_vw_daily f2 ")
            .append(" WHERE ")
            .append(" f2.start >= :start AND f2.start < :stop AND f2.publisher_id = :publisher ")
            .append(
                " AND f2.revenue > 0 AND f2.tag_monetization = 1 AND f2.tag_owner = :tagOwnerPublisher AND f2.adnet_id IN (:millenialMediaAdNetIds) ")
            .append(" UNION ")
            .append(
                " SELECT IFNULL(SUM(f3.revenue) - SUM(f3.revenue_net),0) AS mediationRev, 3 as sortKey ")
            .append(" FROM fact_revenue_adnet_vw_daily AS f3 ")
            .append(" WHERE ")
            .append(" f3.start >= :start AND f3.start < :stop AND f3.publisher_id = :publisher ")
            .append(
                " AND f3.revenue > 0 AND f3.tag_monetization = 1 AND f3.tag_owner = :tagOwnerPublisher AND f3.adnet_id NOT IN (:millenialMediaAdNetIds, :exchangeIds ) ")
            .append(" UNION ")
            .append(" SELECT IFNULL(SUM(f4.revenue),0) AS directCampaign, 4 as sortKey ")
            .append(" FROM fact_traffic_adserver AS f4 ")
            .append(" WHERE ")
            .append(" f4.start >= :start AND f4.start < :stop AND f4.advertiser_id IN ")
            .append(" (SELECT id FROM dim_advertiser WHERE name NOT LIKE 'HOUSE%') ")
            .append(" AND f4.site_id IN (SELECT id FROM dim_site WHERE company_id = :publisher) ")
            .append(" ORDER BY sortKey ");

    return query.toString();
  }

  private Set<Long> getMMAdnets() {
    Map<String, Object> paramMap = new HashMap<>();
    paramMap.put("millennialMediaBuyerPid", millennialMediaBuyerPid);
    String sql = "SELECT id FROM dim_adnet WHERE company_id IN (:millennialMediaBuyerPid)";
    return dwNamedTemplate.query(
        sql,
        paramMap,
        new ResultSetExtractor<Set<Long>>() {
          @Override
          public Set<Long> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Set<Long> adnetIds = new HashSet<>();
            while (rs.next()) {
              adnetIds.add(rs.getLong(1));
            }
            return adnetIds;
          }
        });
  }
}
