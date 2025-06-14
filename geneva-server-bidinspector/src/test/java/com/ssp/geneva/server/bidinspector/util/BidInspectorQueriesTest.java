package com.ssp.geneva.server.bidinspector.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class BidInspectorQueriesTest {

  @Spy @InjectMocks private BidInspectorQueries bidInspectorQueries;

  public static Collection<Object[]> bidQueryData() {
    return Arrays.asList(
        new Object[][] {
          {
            null,
            null,
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id "
          },
          {
            Lists.emptyList(),
            Lists.emptyList(),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id "
          },
          {
            List.of(""),
            List.of(""),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id "
          },
          {
            List.of("sellerId", "dealId"),
            List.of("123", "P567"),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id where seller_id = 123 and deal_id = 'P567'"
          },
          {
            List.of("placementId", "siteId", "appBundleId"),
            List.of("8977", "888", "909"),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id where placement_id = 8977 and site_id = 888 and app_bundle_id = 909"
          },
          {
            List.of("placementId", "siteId", "bidderId"),
            List.of("8977", "444", "678"),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id where placement_id = 8977 and site_id = 444 and bidder_id = 678"
          },
          {
            List.of("sellerId", "appBundleId", "dealId", "placementId", "siteId", "bidderId"),
            List.of("222", "8643", "P75444", "8977", "444", "678"),
            "select distinct fct.auction_run_hash_id AS auctionRunHashId, fct.start AS start, fct.site_id AS siteId, fct.seller_id AS sellerId, fct.placement_id AS placementId, fct.app_bundle_id AS appBundleId, dab.app_bundle AS appBundleName, fct.hb_partner_pid AS hbPartnerPid, fct.deal_id AS dealId, fct.seat_id as seatId, fct.bidder_id AS bidderId, fct.request_url AS requestUrl, fct.request_payload AS requestPayload, fct.response_payload AS responsePayload from fact_cowboy_traffic fct left join dim_app_bundle dab ON fct.app_bundle_id = dab.id where seller_id = 222 and app_bundle_id = 8643 and deal_id = 'P75444' and placement_id = 8977 and site_id = 444 and bidder_id = 678"
          },
        });
  }

  public static Collection<Object[]> AuctionDetailQueryData() {
    return Arrays.asList(
        new Object[][] {
          {
            null,
            null,
            "select auction_run_hash_id as auctionRunHashId, bidder_id as bidderId, bidder_url as bidderUrl, request_payload as requestPayload, response_payload as responsePayload, response_code as responseCode from fact_cowboy_exchange where auction_run_hash_id = ? "
          },
          {
            List.of("bidderId", "bidderUrl"),
            List.of("12345", "potato"),
            "select auction_run_hash_id as auctionRunHashId, bidder_id as bidderId, bidder_url as bidderUrl, request_payload as requestPayload, response_payload as responsePayload, response_code as responseCode from fact_cowboy_exchange where auction_run_hash_id = ?  and bidder_Id = 12345 and bidder_url = \'potato\'"
          }
        });
  }

  public static Collection<Object[]> bidderCountQuery() {
    return Arrays.asList(
        new Object[][] {
          {
            null,
            "select auction_run_hash_id as auctionRunHashId, count(bidder_id) as bidCount FROM fact_cowboy_exchange WHERE auction_run_hash_id in (null) GROUP BY auction_run_hash_id;"
          },
          {
            Lists.emptyList(),
            "select auction_run_hash_id as auctionRunHashId, count(bidder_id) as bidCount FROM fact_cowboy_exchange WHERE auction_run_hash_id in () GROUP BY auction_run_hash_id;"
          },
          {
            List.of(2, 3, 68),
            "select auction_run_hash_id as auctionRunHashId, count(bidder_id) as bidCount FROM fact_cowboy_exchange WHERE auction_run_hash_id in (2,3,68) GROUP BY auction_run_hash_id;"
          }
        });
  }

  public static Collection<Object[]> pagedQueryData() {
    return Arrays.asList(
        new Object[][] {
          {
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1",
            PageRequest.of(1, 3),
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1 ORDER BY auctionRunHashId OFFSET 3 LIMIT 3"
          },
          {
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, dab.app_bundle AS appBundleName, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1",
            PageRequest.of(1, 1, ASC, "siteId"),
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, dab.app_bundle AS appBundleName, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1 ORDER BY siteId ASC, auctionRunHashId OFFSET 1 LIMIT 1"
          },
          {
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, dab.app_bundle AS appBundleName, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1",
            PageRequest.of(2, 3, DESC, "siteId", "placementId"),
            "select auction_run_hash_id as auctionRunHashId, start as start, site_id as siteId, seller_id as sellerId, placement_id as placementId, app_bundle_id as appBundleId, dab.app_bundle AS appBundleName, hb_partner_pid as hbPartnerPid, deal_id as dealId, seat_id as seatId, bidder_id as bidderId, request_url as requestUrl, request_payload as requestPayload, response_payload as responsePayload from fact_cowboy_traffic where 1 ORDER BY siteId DESC, placementId DESC, auctionRunHashId OFFSET 6 LIMIT 3"
          }
        });
  }

  @ParameterizedTest
  @MethodSource("pagedQueryData")
  void shouldReturnExpectedPagedQueryForGivenInputParams(
      String baseQuery, Pageable pageable, String expectedQuery) {
    assertEquals(
        expectedQuery, bidInspectorQueries.getPagedQuery(new StringBuilder(baseQuery), pageable));
  }

  @ParameterizedTest
  @MethodSource("bidQueryData")
  void shouldReturnExpectedBidQueryForGivenInputParams(
      List<String> qf, List<String> qt, String expectedQuery) {
    assertEquals(expectedQuery, bidInspectorQueries.buildQueryForBidData(qf, qt).toString());
  }

  @ParameterizedTest
  @MethodSource("bidderCountQuery")
  void shouldReturnExpectedBidCountQueryForGivenInputParams(
      List<String> ids, String expectedQuery) {
    assertEquals(
        expectedQuery, bidInspectorQueries.buildQueryForAuctionRunHashIdData(ids).toString());
  }

  @ParameterizedTest
  @MethodSource("AuctionDetailQueryData")
  void shouldReturnExpectedAuctionDealQueryForGivenInputParams(
      List<String> qf, List<String> qt, String expectedQuery) {
    assertEquals(expectedQuery, bidInspectorQueries.buildQueryAuctionDetail(qf, qt));
  }
}
