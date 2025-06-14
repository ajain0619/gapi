package com.ssp.geneva.server.bidinspector.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import com.ssp.geneva.server.bidinspector.dao.BidDao;
import com.ssp.geneva.server.bidinspector.dto.AuctionDetailDTO;
import com.ssp.geneva.server.bidinspector.dto.BidDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
class BidServiceImplTest {
  @Mock private BidDao bidDao;

  @InjectMocks private BidServiceImpl bidService;

  private static Long SELLER_ID = 10l;
  private static Integer PLACEMENT_ID = 22;
  private static Long SITE_ID = 100l;
  private static String DEAL_ID = "P123";
  private static String REQUEST_URL =
      "https://reserved.v.ssp.yahoo.com/reserved?ext_id=SSPMigration";
  private static String REQUEST_PAYLOAD =
      "{\"bidderId\":13807,\"url\":\"https://yahoo.com\",\"auction\":\"e7d09415c18041b7b5d3ccc5cc9e9d6d\"}";
  private static String RESPONSE_PAYLOAD =
      "{\"bidderId\":13807,\"url\":\"null\",\"auction\":\"e7d09415c18041b7b5d3ccc5cc9e9d6d\"}";
  private static Integer ID = 1;
  private static String BIDDER_URL = "http://3lift.com/";
  private static String AUCTION_RUN_HASH_ID = "-1243487573824";

  @Test
  void shouldReturnNoBidsWhenGetBidDetailsWithIncorrectInputParamKey() {

    // given
    when(bidDao.getBidDetails(
            List.of("siteId", "sellerId"), List.of("100", "10"), PageRequest.of(0, 20)))
        .thenReturn(getBids());
    // when
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("siteIdIncorrect", "100");
    map.add("sellerId", "10");
    MultiValueQueryParams inputParams = new MultiValueQueryParams(map, SearchQueryOperator.AND);
    Page<BidDTO> bids = bidService.getBidDetails(inputParams, PageRequest.of(0, 20));
    assertNull(bids);
  }

  @Test
  void shouldReturnNoBidsWhenGetBidDetailsWithIncorrectInputParamValue() {

    // given
    when(bidDao.getBidDetails(
            List.of("siteId", "sellerId"), List.of("1000", "1022"), PageRequest.of(0, 20)))
        .thenReturn(getBids());
    // when
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("siteId", "abc1000");
    map.add("sellerId", "1022");
    MultiValueQueryParams inputParams = new MultiValueQueryParams(map, SearchQueryOperator.AND);
    Page<BidDTO> bids = bidService.getBidDetails(inputParams, PageRequest.of(0, 20));
    assertNull(bids);
  }

  @Test
  void shouldReturnExpectedBidsWhenGetBidDetailsWithRightInputParams() {

    // given
    when(bidDao.getBidDetails(
            List.of("siteId", "sellerId"), List.of("100", "10"), PageRequest.of(0, 20)))
        .thenReturn(getBids());
    // when
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("siteId", "100");
    map.add("sellerId", "10");
    MultiValueQueryParams inputParams = new MultiValueQueryParams(map, SearchQueryOperator.AND);
    Page<BidDTO> bids = bidService.getBidDetails(inputParams, PageRequest.of(0, 20));
    assertEquals(3l, bids.getTotalElements());
    assertEquals(1, bids.getTotalPages());
    assertEquals(20, bids.getSize());
    assertEquals(getBids().getContent(), bids.getContent());
  }

  @Test
  @SneakyThrows
  void shouldReturnExpectedAuctionDetailsWhenPassedWithRightInputParams() {

    // given
    when(bidDao.getAuctionDetails(
            List.of("bidderId"), List.of("1"), "-1243487573824", PageRequest.of(0, 20)))
        .thenReturn(getAuctionDetails());
    // when
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("bidderId", "1");
    MultiValueQueryParams inputParams = new MultiValueQueryParams(map, SearchQueryOperator.AND);
    Page<AuctionDetailDTO> auctionDetails =
        bidService.getAuctionDetails(inputParams, "-1243487573824", PageRequest.of(0, 20));
    assertEquals(3l, auctionDetails.getTotalElements());
    assertEquals(1, auctionDetails.getTotalPages());
    assertEquals(20, auctionDetails.getSize());
    assertEquals(getAuctionDetails().getContent(), auctionDetails.getContent());
  }

  private Page<BidDTO> getBids() {
    BidDTO bid1, bid2, bid3;
    bid1 = getBaseBid(1);
    bid2 = getBaseBid(2);
    bid3 = getBaseBid(3);
    List<BidDTO> bids = new ArrayList<>();
    bids.add(bid1);
    bids.add(bid2);
    bids.add(bid3);
    return new PageImpl<>(bids, PageRequest.of(0, 20), 1L);
  }

  private BidDTO getBaseBid(Integer bidderId) {
    return BidDTO.builder()
        .siteId(SITE_ID)
        .sellerId(SELLER_ID)
        .placementId(PLACEMENT_ID)
        .siteId(SITE_ID)
        .dealId(DEAL_ID)
        .requestUrl(REQUEST_URL)
        .requestPayload(REQUEST_PAYLOAD)
        .responsePayload(RESPONSE_PAYLOAD)
        .bidderId(bidderId)
        .build();
  }

  private Page<AuctionDetailDTO> getAuctionDetails() {
    AuctionDetailDTO auctionDetailDTO1, auctionDetailDTO2, auctionDetailDTO3;
    auctionDetailDTO1 = getBaseAuctionDetail(1);
    auctionDetailDTO2 = getBaseAuctionDetail(2);
    auctionDetailDTO3 = getBaseAuctionDetail(3);
    List<AuctionDetailDTO> auctionDetails = new ArrayList<>();
    auctionDetails.add(auctionDetailDTO1);
    auctionDetails.add(auctionDetailDTO2);
    auctionDetails.add(auctionDetailDTO3);
    return new PageImpl<>(auctionDetails, PageRequest.of(0, 20), 1L);
  }

  private AuctionDetailDTO getBaseAuctionDetail(Integer bidderId) {
    return AuctionDetailDTO.builder()
        .id(ID)
        .auctionRunHashId(AUCTION_RUN_HASH_ID)
        .requestPayload(REQUEST_PAYLOAD)
        .responsePayload(RESPONSE_PAYLOAD)
        .bidderId(bidderId)
        .bidderUrl(BIDDER_URL)
        .build();
  }
}
