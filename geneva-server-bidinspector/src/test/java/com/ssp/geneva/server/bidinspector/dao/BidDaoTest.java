package com.ssp.geneva.server.bidinspector.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;

import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class BidDaoTest {
  @Mock private JdbcTemplate dwJdbcTemplate;
  @Mock private Connection conn;
  @Mock private DataSource ds;
  @Mock private PreparedStatement preparedStatement;
  @Mock private ResultSet resultSet;
  private BidDao bidDao;

  private final String AUCTION_RUN_HASH_ID = "1";
  private final Integer BIDDER_ID = 1;
  private final String REQUEST_URL = "http://cowboy.edu";
  private final String PAYLOAD = "Sonic Screwdriver";
  private final Integer RESPONSE_CODE = 200;
  private final String START = "123";
  private final Long SELLER_ID = 1L;
  private final Long SITE_ID = 2L;
  private final Integer PLACEMENT_ID = 3;
  private final String DEAL_ID = "4321";
  private final String SEAT_ID = "Seat 1";
  private final Long APP_BUNDLE_ID = 500L;
  private final Integer HB_PARTNER_ID = 54;
  private final String APP_BUNDLE_NAME = "Potato Sack";

  @BeforeEach
  void initMocks() {
    bidDao = new BidDao(dwJdbcTemplate);
  }

  public static Collection<Object[]> data() {
    return List.of(new Object[][] {{5L, 3, "1", "2"}, {2L, 1, "1", "2"}, {2L, 1, "1", "2"}});
  }

  public static Collection<Object[]> dataNull() {
    return List.of(new Object[][] {{5L, 3, "1", "2"}, {2L, 1, "1", "2"}, {2L, 1, "1", "2"}});
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldReturnValidPaginationForGetBidDetails(
      Long outputSize, Integer totalPages, String auctionRunHashId1, String auctionRunHashId2) {
    when(dwJdbcTemplate.query(contains("fact_cowboy_traffic "), any(RowMapper.class)))
        .thenReturn(List.of(getBidDTO(auctionRunHashId1, "1"), getBidDTO(auctionRunHashId2, "3")));
    when(dwJdbcTemplate.query(startsWith("SELECT count(*)"), any(RowMapper.class)))
        .thenReturn(Collections.singletonList(outputSize));
    Page<BidDTO> bids =
        bidDao.getBidDetails(
            List.of("sellerId", "siteId"), List.of("789", "4567"), PageRequest.of(0, 2));

    assertEquals(outputSize, bids.getTotalElements());
    assertEquals(2, bids.getSize());
    assertEquals(totalPages, bids.getTotalPages());
    BidDTO bidDTO1 = bids.getContent().get(0);
    BidDTO bidDTO2 = bids.getContent().get(1);
    assertTrue(StringUtils.equals(bidDTO1.getAuctionRunHashId(), auctionRunHashId1));
    assertTrue(StringUtils.equals(bidDTO2.getAuctionRunHashId(), auctionRunHashId2));
  }

  @ParameterizedTest
  @MethodSource("dataNull")
  void shouldReturnValidPaginationForBidNullBidCount(
      Long outputSize, Integer totalPages, String auctionRunHashId1, String auctionRunHashId2) {
    when(dwJdbcTemplate.query(contains("fact_cowboy_traffic "), any(RowMapper.class)))
        .thenReturn(List.of(getBidDTO(auctionRunHashId1, "1"), getBidDTO(auctionRunHashId2, "3")));
    when(dwJdbcTemplate.query(startsWith("SELECT count(*)"), any(RowMapper.class)))
        .thenReturn(Collections.singletonList(outputSize));
    Page<BidDTO> bids =
        bidDao.getBidDetails(
            List.of("sellerId", "siteId"), List.of("789", "4567"), PageRequest.of(0, 2));

    assertEquals(outputSize, bids.getTotalElements());
    assertEquals(2, bids.getSize());
    assertEquals(totalPages, bids.getTotalPages());
    BidDTO bidDTO1 = bids.getContent().get(0);
    BidDTO bidDTO2 = bids.getContent().get(1);
    assertTrue(StringUtils.equals(bidDTO1.getAuctionRunHashId(), auctionRunHashId1));
    assertTrue(StringUtils.equals(bidDTO2.getAuctionRunHashId(), auctionRunHashId2));
  }

  @Test
  void shouldReturnExpectedResponseForGetBidDetailsWhenResultSetIsEmpty() throws Exception {

    when(dwJdbcTemplate.query(anyString(), any(RowMapper.class)))
        .thenAnswer(I -> Collections.EMPTY_LIST);

    Page<BidDTO> bids =
        bidDao.getBidDetails(Lists.emptyList(), Lists.emptyList(), PageRequest.of(0, 2));

    assertEquals(0, bids.getTotalElements());
    assertEquals(2, bids.getSize());
    assertEquals(0, bids.getTotalPages());
    assertEquals(0, bids.getContent().size());
  }

  @Test
  void shouldReturnExpectedResponseForGetBidDetailsWhenBidders() throws Exception {
    Map<String, String> bidders = new HashMap<>();
    bidders.put("1", "1");

    when(dwJdbcTemplate.query(contains("fact_cowboy_traffic "), any(RowMapper.class)))
        .thenReturn(List.of(getBidDTO("1", "0"), getBidDTO("2", "0")));
    when(dwJdbcTemplate.queryForObject(contains("fact_cowboy_exchange"), any(RowMapper.class)))
        .thenReturn(bidders);
    when(dwJdbcTemplate.query(startsWith("SELECT count(*)"), any(RowMapper.class)))
        .thenReturn(Collections.singletonList(2L));

    Page<BidDTO> bids =
        bidDao.getBidDetails(Lists.emptyList(), Lists.emptyList(), PageRequest.of(0, 10));

    assertEquals(2, bids.getTotalElements());
    assertEquals(10, bids.getSize());
    assertEquals(1, bids.getTotalPages());
    assertEquals(2, bids.getContent().size());
    BidDTO bid1 = bids.getContent().get(0);
  }

  @Test
  void shouldReturnExpectedResponseForGetBidDetailsWhenBiddersCountIsEmpty() throws Exception {

    when(dwJdbcTemplate.query(contains("fact_cowboy_traffic "), any(RowMapper.class)))
        .thenReturn(List.of(getBidDTO("1", "0"), getBidDTO("2", "0")))
        .thenReturn(null);
    when(dwJdbcTemplate.query(startsWith("SELECT count(*)"), any(RowMapper.class)))
        .thenReturn(Collections.singletonList(2L));

    Page<BidDTO> bids =
        bidDao.getBidDetails(Lists.emptyList(), Lists.emptyList(), PageRequest.of(0, 10));

    assertEquals(2, bids.getTotalElements());
    assertEquals(10, bids.getSize());
    assertEquals(1, bids.getTotalPages());
    assertEquals(2, bids.getContent().size());
    BidDTO bid1 = bids.getContent().get(0);
  }

  @Test
  void shouldReturnExpectedResponseForGetBidDetailsWhenBiddersIsNonEmpty() throws Exception {

    when(dwJdbcTemplate.query(contains("fact_cowboy_traffic "), any(RowMapper.class)))
        .thenReturn(List.of(getBidDTO("1", "2"), getBidDTO("2", "1")));
    when(dwJdbcTemplate.query(startsWith("SELECT count(*)"), any(RowMapper.class)))
        .thenReturn(Collections.singletonList(2L));

    Page<BidDTO> bids =
        bidDao.getBidDetails(Lists.emptyList(), Lists.emptyList(), PageRequest.of(0, 10));

    assertEquals(2, bids.getTotalElements());
    assertEquals(10, bids.getSize());
    assertEquals(1, bids.getTotalPages());
    assertEquals(2, bids.getContent().size());
    BidDTO bid1 = bids.getContent().get(0);
    BidDTO bid2 = bids.getContent().get(1);
    assertEquals("1", bid1.getAuctionRunHashId());
    assertEquals("2", bid2.getAuctionRunHashId());
    assertTrue(StringUtils.equals("2", bid1.getBidCount()));
    assertTrue(StringUtils.equals("1", bid2.getBidCount()));
  }

  @Test
  void shouldReturnExpectedResponseForGetAuctionDetailsWhenNoFiltering() throws Exception {

    when(dwJdbcTemplate.getDataSource()).thenReturn(ds);
    when(ds.getConnection()).thenReturn(conn);
    when(conn.prepareStatement(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true).thenReturn(false);
    when(resultSet.getString("auctionRunHashId")).thenReturn(AUCTION_RUN_HASH_ID);
    when(resultSet.getInt("bidderId")).thenReturn(BIDDER_ID);
    when(resultSet.getString("bidderUrl")).thenReturn(REQUEST_URL);
    when(resultSet.getString("requestPayload")).thenReturn(PAYLOAD);
    when(resultSet.getString("responsePayload")).thenReturn(PAYLOAD);
    when(resultSet.getInt("responseCode")).thenReturn(RESPONSE_CODE);

    Page<AuctionDetailDTO> auctionDetailDTOPage =
        bidDao.getAuctionDetails(Lists.emptyList(), Lists.emptyList(), "1", PageRequest.of(0, 10));

    assertEquals(10, auctionDetailDTOPage.getSize());
    assertEquals(1, auctionDetailDTOPage.getTotalPages());
    assertEquals(1, auctionDetailDTOPage.getContent().size());
    AuctionDetailDTO auctionDetailDTO1 = auctionDetailDTOPage.getContent().get(0);
    assertTrue(StringUtils.equals(auctionDetailDTO1.getAuctionRunHashId(), AUCTION_RUN_HASH_ID));
    assertEquals(auctionDetailDTO1.getBidderId(), BIDDER_ID);
    assertTrue(StringUtils.equals(auctionDetailDTO1.getBidderUrl(), REQUEST_URL));
    assertTrue(StringUtils.equals(auctionDetailDTO1.getRequestPayload(), PAYLOAD));
    assertTrue(StringUtils.equals(auctionDetailDTO1.getResponsePayload(), PAYLOAD));
    assertEquals(auctionDetailDTO1.getResponseCode(), RESPONSE_CODE);
  }

  @Test
  void shouldReturnExpectedResponseForBuildingBidDTO() throws Exception {
    when(resultSet.getString("auctionRunHashId")).thenReturn(AUCTION_RUN_HASH_ID);
    when(resultSet.getString("start")).thenReturn("1234");
    when(resultSet.getLong("sellerId")).thenReturn(SELLER_ID);
    when(resultSet.getLong("siteId")).thenReturn(SITE_ID);
    when(resultSet.getInt("placementId")).thenReturn(PLACEMENT_ID);
    when(resultSet.getString("dealId")).thenReturn(DEAL_ID);
    when(resultSet.getString("seatId")).thenReturn(SEAT_ID);
    when(resultSet.getLong("appBundleId")).thenReturn(APP_BUNDLE_ID);
    when(resultSet.getInt("hbPartnerPid")).thenReturn(HB_PARTNER_ID);
    when(resultSet.getInt("bidderId")).thenReturn(BIDDER_ID);
    when(resultSet.getString("requestUrl")).thenReturn(REQUEST_URL);
    when(resultSet.getString("requestPayload")).thenReturn(PAYLOAD);
    when(resultSet.getString("responsePayload")).thenReturn(PAYLOAD);
    when(resultSet.getString("appBundleName")).thenReturn(APP_BUNDLE_NAME);
    BidDTO bidDTO = bidDao.buildBidDTO(resultSet);
    assertTrue(StringUtils.equals(bidDTO.getAuctionRunHashId(), AUCTION_RUN_HASH_ID));
    assertEquals(bidDTO.getSellerId(), SELLER_ID);
    assertEquals(bidDTO.getSiteId(), SITE_ID);
    assertEquals(bidDTO.getPlacementId(), PLACEMENT_ID);
    assertTrue(StringUtils.equals(bidDTO.getDealId(), DEAL_ID));
    assertTrue(StringUtils.equals(bidDTO.getSeatId(), SEAT_ID));
    assertEquals(bidDTO.getAppBundleId(), BigInteger.valueOf(APP_BUNDLE_ID));
    assertEquals(bidDTO.getHbPartnerPid(), HB_PARTNER_ID);
    assertEquals(bidDTO.getBidderId(), BIDDER_ID);
    assertTrue(StringUtils.equals(bidDTO.getRequestPayload(), PAYLOAD));
    assertTrue(StringUtils.equals(bidDTO.getResponsePayload(), PAYLOAD));
    assertTrue(StringUtils.equals(bidDTO.getAppBundleName(), APP_BUNDLE_NAME));
  }

  private BidDTO getBidDTO(String auctionRunHashId, String bidCount) {
    BidDTO bid = new BidDTO();
    bid.setAuctionRunHashId(auctionRunHashId);
    bid.setBidCount(bidCount);
    return bid;
  }

  private AuctionDetailDTO getAuctionDetailDTO(String auctionRunHashId) {
    AuctionDetailDTO auctionDetailDTO = new AuctionDetailDTO();
    auctionDetailDTO.setAuctionRunHashId(auctionRunHashId);
    return auctionDetailDTO;
  }
}
