package com.ssp.geneva.server.bidinspector.util;

import java.util.Iterator;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BidInspectorQueries {

  private static final String ORDER_BY = " ORDER BY ";
  private static final String AUCTION_RUN_HASH_ID = "auctionRunHashId";
  /**
   * Returns the query for a given request with query params
   *
   * @param qf {@link List<String>} List of query param keys
   * @param qt {@link List<String>} List of query param values
   * @return A {@link StringBuilder} Base SQL query
   */
  public static StringBuilder buildQueryForBidData(List<String> qf, List<String> qt) {
    StringBuilder query = queryBodyForBidData();
    if (qf != null && !qf.isEmpty()) {
      for (String field : qf) {
        int idx = qf.indexOf(field);
        if (idx == 0 && StringUtils.isNotBlank(field)) {
          query.append("where");
        } else if (StringUtils.isNotBlank(field)) {
          query.append(" and");
        }
        switch (field) {
          case "sellerId":
            query.append(" seller_id = " + qt.get(idx));
            break;
          case "placementId":
            query.append(" placement_id = " + qt.get(idx));
            break;
          case "siteId":
            query.append(" site_id = " + qt.get(idx));
            break;
          case "appBundleId":
            query.append(" app_bundle_id = " + qt.get(idx));
            break;
          case "dealId":
            query.append(" deal_id = '" + qt.get(idx) + "'");
            break;
          case "bidderId":
            query.append(" bidder_id = " + qt.get(idx));
            break;
          default:
        }
      }
    }
    log.debug("bid details base query: {}", query.toString());
    return query;
  }

  /**
   * Returns the paginated query after applying pagination to base query
   *
   * @param baseQuery {@link StringBuilder} Base query
   * @param pageable Pagination based on {@link Pageable}
   * @return A {@link StringBuilder} Paginated SQL query
   */
  public static String getPagedQuery(StringBuilder baseQuery, Pageable pageable) {
    StringBuilder orderByFields = new StringBuilder();
    Iterator<Sort.Order> it = pageable.getSort().iterator();
    while (it.hasNext()) {
      Sort.Order sortOrder = it.next();
      orderByFields.append(sortOrder.getProperty() + " ");
      orderByFields.append(sortOrder.getDirection() + ", ");
    }
    StringBuilder pagedQuery =
        new StringBuilder()
            .append(baseQuery.toString())
            .append(ORDER_BY + orderByFields + AUCTION_RUN_HASH_ID)
            .append(
                String.format(" OFFSET %d LIMIT %d", pageable.getOffset(), pageable.getPageSize()));

    log.debug(String.format("bid inspector paged query: %s", pagedQuery));

    return pagedQuery.toString();
  }

  /**
   * Returns the query to fetch bidder data
   *
   * @param qf {@link List<String>} List of QueryFields
   * @param qt {@link List<String>} List of QueryTerms
   * @return A {@link StringBuilder} Bidder SQL query
   */
  public static String buildQueryAuctionDetail(List<String> qf, List<String> qt) {

    var bidderQuery = new StringBuilder("");
    bidderQuery
        .append("select auction_run_hash_id as auctionRunHashId, ")
        .append("bidder_id as bidderId, ")
        .append("bidder_url as bidderUrl, ")
        .append("request_payload as requestPayload, ")
        .append("response_payload as responsePayload, ")
        .append("response_code as responseCode ")
        .append("from fact_cowboy_exchange ")
        .append("where auction_run_hash_id = ? ");
    log.debug("bidder details query: {}", bidderQuery.toString());
    if (qf != null && !qf.isEmpty()) {
      for (String field : qf) {
        int idx = qf.indexOf(field);
        switch (field) {
          case "bidderId":
            bidderQuery.append(" and bidder_Id = " + qt.get(idx));
            break;
          case "bidderUrl":
            bidderQuery.append(" and bidder_url = \'" + qt.get(idx) + "\'");
            break;
          default:
        }
      }
      log.debug("Auction Details Query: {}", bidderQuery.toString());
    }
    return bidderQuery.toString();
  }

  /**
   * Build query for auction run hash id data in fact_cowboy_exchange
   *
   * @param auctionRunHashIds {@link List<String>}the auction run hash ids
   * @return {@link StringBuilder}the string builder
   */
  public static StringBuilder buildQueryForAuctionRunHashIdData(List<String> auctionRunHashIds) {
    var bidderQuery = new StringBuilder("");
    bidderQuery
        .append("select auction_run_hash_id as auctionRunHashId, count(bidder_id) as bidCount ")
        .append("FROM fact_cowboy_exchange ")
        .append("WHERE auction_run_hash_id in (")
        .append(StringUtils.join(auctionRunHashIds, ","))
        .append(") ")
        .append("GROUP BY auction_run_hash_id;");
    log.debug("bidder deal details query: {}", bidderQuery.toString());
    return bidderQuery;
  }

  private static StringBuilder queryBodyForBidData() {
    var baseQuery = new StringBuilder("select distinct ");
    baseQuery.append("fct.auction_run_hash_id AS auctionRunHashId, ");
    baseQuery.append("fct.start AS start, ");
    baseQuery.append("fct.site_id AS siteId, ");
    baseQuery
        .append("fct.seller_id AS sellerId, ")
        .append("fct.placement_id AS placementId, ")
        .append("fct.app_bundle_id AS appBundleId, ")
        .append("dab.app_bundle AS appBundleName, ")
        .append("fct.hb_partner_pid AS hbPartnerPid, ")
        .append("fct.deal_id AS dealId, ")
        .append("fct.seat_id as seatId, ")
        .append("fct.bidder_id AS bidderId, ")
        .append("fct.request_url AS requestUrl, ")
        .append("fct.request_payload AS requestPayload, ")
        .append("fct.response_payload AS responsePayload ")
        .append("from fact_cowboy_traffic fct ")
        .append("left join dim_app_bundle dab ON fct.app_bundle_id = dab.id ");

    return baseQuery;
  }
}
