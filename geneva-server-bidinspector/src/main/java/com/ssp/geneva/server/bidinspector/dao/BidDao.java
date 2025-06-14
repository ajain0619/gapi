package com.ssp.geneva.server.bidinspector.dao;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import com.ssp.geneva.server.bidinspector.util.BidInspectorQueries;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/** The type Bid dao. */
@Log4j2
@Component("bidDao")
public class BidDao {

  private final JdbcTemplate dwJdbcTemplate;

  public BidDao(@Qualifier("dwJdbcTemplate") JdbcTemplate dwJdbcTemplate) {
    this.dwJdbcTemplate = dwJdbcTemplate;
  }

  private static final String BIDDER_ID = "bidderId";
  private static final String AUCTION_RUN_HASH_ID = "auctionRunHashId";

  /**
   * Retrieve a page of bid details for given request on bid inspector
   *
   * @param qf {@link List<String>} List of query param keys
   * @param qt {@link List<String>} List of query param values
   * @param pageable Pagination based on {@link Pageable}
   * @return A {@link Page} of {@link BidDTO}
   */
  public Page<BidDTO> getBidDetails(List<String> qf, List<String> qt, Pageable pageable) {

    StringBuilder baseQuery = BidInspectorQueries.buildQueryForBidData(qf, qt);

    // get paged query
    String pagedQuery = BidInspectorQueries.getPagedQuery(baseQuery, pageable);

    RowMapper<BidDTO> rowMapper = (rs, rowNum) -> buildBidDTO(rs);

    List<BidDTO> bidResult = dwJdbcTemplate.query(pagedQuery, rowMapper);
    // build and execute query for count
    StringBuilder countQuery =
        new StringBuilder()
            .append("SELECT count(*) AS bid_count FROM ")
            .append("fact_cowboy_traffic")
            .append(" cq");
    List<Long> bidCountResult =
        dwJdbcTemplate.query(countQuery.toString(), (rs, rowNum) -> rs.getLong("bid_count"));

    Long bidCount = (bidCountResult.isEmpty()) ? 0L : bidCountResult.get(0);
    if (CollectionUtils.isNotEmpty(bidResult)) {
      List<String> auctionRunHashIds = bidResult.stream().map(BidDTO::getAuctionRunHashId).toList();

      String bidCountQuery =
          BidInspectorQueries.buildQueryForAuctionRunHashIdData(auctionRunHashIds).toString();

      RowMapper<Map<String, String>> bidCountRowMapper = (rs, rowNum) -> (buildCount(rs));

      try {
        Map<String, String> auctionRunCount =
            dwJdbcTemplate.queryForObject(bidCountQuery, bidCountRowMapper);

        if (auctionRunCount != null && !auctionRunCount.isEmpty())
          populateBidResultWithBidderCount(bidResult, auctionRunCount);
      } catch (RuntimeException e) {
        return new PageImpl<>(bidResult, pageable, bidCount);
      }
    }
    return new PageImpl<>(bidResult, pageable, bidCount);
  }

  /**
   * Gets auction details.
   *
   * @param qf {@link List<String>} the queryField
   * @param qt {@link List<String>} the qt
   * @param auctionRunId {@link String} the auction run id
   * @param pageable {@link Pageable} the pageable
   * @return {@link Page} of {@link AuctionDetailDTO} auction details
   */
  public Page<AuctionDetailDTO> getAuctionDetails(
      List<String> qf, List<String> qt, String auctionRunId, Pageable pageable) {

    String baseQuery = BidInspectorQueries.buildQueryAuctionDetail(qf, qt);
    List<AuctionDetailDTO> auctionDetails = new ArrayList<>();
    DataSource dataSource = dwJdbcTemplate.getDataSource();
    if (dataSource != null) {
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement stmt = conn.prepareStatement(baseQuery)) {
          stmt.setString(1, auctionRunId);
          ResultSet rs = stmt.executeQuery();
          while (rs.next()) {
            auctionDetails.add(buildAuctionDetailDTO(rs));
          }
        }
      } catch (Exception e) {
        throw new GenevaAppRuntimeException(CommonErrorCodes.COMMON_INTERNAL_SYSTEM_ERROR);
      }
    }
    return new PageImpl<>(auctionDetails, pageable, pageable.getPageSize());
  }

  public BidDTO buildBidDTO(ResultSet rs) throws SQLException {
    return BidDTO.builder()
        .auctionRunHashId(rs.getString(AUCTION_RUN_HASH_ID))
        .recordTime(rs.getString("start"))
        .sellerId(rs.getLong("sellerId"))
        .siteId(rs.getLong("siteId"))
        .placementId(rs.getInt("placementId"))
        .dealId(rs.getString("dealId"))
        .seatId(rs.getString("seatId"))
        .appBundleId(BigInteger.valueOf(rs.getLong("appBundleId")))
        .hbPartnerPid(rs.getInt("hbPartnerPid"))
        .bidderId(rs.getInt(BIDDER_ID))
        .requestUrl(rs.getString("requestUrl"))
        .requestPayload(rs.getString("requestPayload"))
        .responsePayload(rs.getString("responsePayload"))
        .appBundleName(rs.getString("appBundleName"))
        .build();
  }

  private Map<String, String> buildCount(ResultSet rs) throws SQLException {

    HashMap<String, String> hashMap = new HashMap<>();
    do {
      hashMap.put(rs.getString(AUCTION_RUN_HASH_ID), rs.getString("bidCount"));
    } while (rs.next());

    return hashMap;
  }

  private AuctionDetailDTO buildAuctionDetailDTO(ResultSet rs) throws SQLException {
    return AuctionDetailDTO.builder()
        .auctionRunHashId(rs.getString(AUCTION_RUN_HASH_ID))
        .bidderId(rs.getInt(BIDDER_ID))
        .bidderUrl(rs.getString("bidderUrl"))
        .requestPayload(rs.getString("requestPayload"))
        .responsePayload(rs.getString("responsePayload"))
        .responseCode(rs.getInt("responseCode"))
        .build();
  }

  private void populateBidResultWithBidderCount(
      List<BidDTO> bidderResult, Map<String, String> bidderCountResult) {
    if (CollectionUtils.isNotEmpty(bidderResult)) {
      Map<String, List<BidDTO>> bidderResultMap =
          bidderResult.stream().collect(Collectors.groupingBy(BidDTO::getAuctionRunHashId));
      bidderResult.stream()
          .filter(bidDTO -> bidderResultMap.containsKey(bidDTO.getAuctionRunHashId()))
          .forEach(
              bidDTO ->
                  bidDTO.setBidCount(
                      String.valueOf(bidderCountResult.get(bidDTO.getAuctionRunHashId()))));
    }
  }
}
