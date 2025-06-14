package com.ssp.geneva.sdk.xandr.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.xandr.config.TestRepositoryConfig;
import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import com.ssp.geneva.sdk.xandr.model.AuctionType;
import com.ssp.geneva.sdk.xandr.model.BuyerSeat;
import com.ssp.geneva.sdk.xandr.model.Deal;
import com.ssp.geneva.sdk.xandr.model.Seller;
import com.ssp.geneva.sdk.xandr.model.Type;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class})
@TestPropertySource("classpath:application-test.properties")
class DealRepositoryIT {

  private static final int port = 8084;

  @RegisterExtension
  static WireMockExtension wireMockRule =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(port));

  private String successBody =
      """
    {
         "response": {
              "status": "OK",
              "count": 1,
              "id": 123,
              "start_element": 0,
              "num_elements": 100,
              "deal": {
              "id": 123,
              "code": "2468",
              "name": "Private deal with no floor",
              "active": true,
              "start_date": "2013-12-01 00:00:00",
              "end_date": "2013-12-31 23:59:59",
              "floor_price": 0,
              "currency": "USD",
              "use_deal_floor": true,
              "seller": {
                   "id": 2345,
                   "name": "Seller 123"
              },
              "type": {
                   "id": 2,
                   "name": "Private Auction"
              },
              "auction_type": {
                   "id": 1,
                   "name": "first_price"
              },
              "buyer_seats": [{
                        "bidder_id": 99,
                        "bidder_name": "test bidder",
                        "code": "88",
                        "name": "test"
                   }],
                   "ask_price": 0,
                   "version": 1
              }
         }
    }

    """;

  @InjectMocks private DealRepository dealRepository;

  @Autowired RestTemplate xandrSdkRestTemplate;

  @Mock AuthRepository authRepository;

  @Value("${xandr.service.endpoint}")
  private String xandrEndpoint;

  @BeforeEach
  void setUp() {
    dealRepository =
        new DealRepository(xandrEndpoint + ":" + port, xandrSdkRestTemplate, authRepository);
  }

  @Test
  void shouldExecuteCreateRequestForXandr() {
    wireMockRule.stubFor(
        post(urlPathMatching("/deal"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Deal newDeal = buildDeal();
    ResponseEntity<Deal> resp = dealRepository.createForXandr(newDeal);
    assertNotNull(resp);
    Deal dealResp = resp.getBody();
    assertNotNull(dealResp);
    assertEquals(newDeal.getName(), dealResp.getName());
    assertEquals(newDeal.getSeller().getName(), dealResp.getSeller().getName());
    assertEquals(
        newDeal.getBuyerSeats().get(0).getBidderId(),
        dealResp.getBuyerSeats().get(0).getBidderId());
    assertEquals(
        newDeal.getBuyerSeats().get(0).getBidderName(),
        dealResp.getBuyerSeats().get(0).getBidderName());
    assertTrue(dealResp.isUseDealFloor());
  }

  @Test
  void shouldExecuteCreateRequestForXandrMsRebroadcast() {
    wireMockRule.stubFor(
        post(urlPathMatching("/deal"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Deal newDeal = buildDeal();
    ResponseEntity<Deal> resp = dealRepository.createForXandrMsRebroadcast(newDeal);
    assertNotNull(resp);
    Deal dealResp = resp.getBody();
    assertNotNull(dealResp);
    assertEquals(newDeal.getName(), dealResp.getName());
    assertEquals(newDeal.getSeller().getName(), dealResp.getSeller().getName());
    assertEquals(
        newDeal.getBuyerSeats().get(0).getBidderId(),
        dealResp.getBuyerSeats().get(0).getBidderId());
    assertEquals(
        newDeal.getBuyerSeats().get(0).getBidderName(),
        dealResp.getBuyerSeats().get(0).getBidderName());
    assertTrue(dealResp.isUseDealFloor());
  }

  @Test
  void shouldExecuteUpdateRequestForXandr() {
    wireMockRule.stubFor(
        put(urlPathMatching("/deal"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Deal updatedDeal = buildDeal();
    ResponseEntity<Deal> resp = dealRepository.updateForXandr(updatedDeal);
    assertNotNull(resp);
    Deal dealResp = resp.getBody();
    assertNotNull(dealResp);
    assertEquals(updatedDeal.getName(), dealResp.getName());
    assertEquals(updatedDeal.getSeller().getName(), dealResp.getSeller().getName());
    assertEquals(
        updatedDeal.getBuyerSeats().get(0).getBidderId(),
        dealResp.getBuyerSeats().get(0).getBidderId());
    assertEquals(
        updatedDeal.getBuyerSeats().get(0).getBidderName(),
        dealResp.getBuyerSeats().get(0).getBidderName());
    assertTrue(dealResp.isUseDealFloor());
  }

  @Test
  void shouldExecuteUpdateRequestForXandrMsRebroadcast() {
    wireMockRule.stubFor(
        put(urlPathMatching("/deal"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Deal updatedDeal = buildDeal();
    ResponseEntity<Deal> resp = dealRepository.updateForXandrMsRebroadcast(updatedDeal);
    assertNotNull(resp);
    Deal dealResp = resp.getBody();
    assertNotNull(dealResp);
    assertEquals(updatedDeal.getName(), dealResp.getName());
    assertEquals(updatedDeal.getSeller().getName(), dealResp.getSeller().getName());
    assertEquals(
        updatedDeal.getBuyerSeats().get(0).getBidderId(),
        dealResp.getBuyerSeats().get(0).getBidderId());
    assertEquals(
        updatedDeal.getBuyerSeats().get(0).getBidderName(),
        dealResp.getBuyerSeats().get(0).getBidderName());
    assertTrue(dealResp.isUseDealFloor());
  }

  @Test
  void shouldThrowExceptionWithHttpClientErrorOnCreate() {
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          post(urlPathMatching("/deal"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));
      Deal deal = Deal.builder().build();
      var exception =
          assertThrows(XandrSdkException.class, () -> dealRepository.createForXandr(deal));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void shouldThrowExceptionWithHttpClientErrorOnUpdate() {
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          put(urlPathMatching("/deal"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));
      Deal deal = Deal.builder().build();
      var exception =
          assertThrows(XandrSdkException.class, () -> dealRepository.updateForXandr(deal));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void shouldThrowExceptionWithHttpClientError() throws XandrSdkException {
    doThrow(new XandrSdkException(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR))
        .when(authRepository)
        .getAuthHeaderForXandr();
    XandrSdkException exception =
        assertThrows(XandrSdkException.class, () -> authRepository.getAuthHeaderForXandr());
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
  }

  private Deal buildDeal() {
    return Deal.builder()
        .id(123)
        .code("2468")
        .name("Private deal with no floor")
        .active(true)
        .startDate("2013-12-01 00:00:00")
        .endDate("2013-12-31 23:59:59")
        .floorPrice(0)
        .currency("USD")
        .useDealFloor(true)
        .askPrice(0)
        .seller(Seller.builder().id(2345).name("Seller 123").build())
        .buyerSeats(
            Arrays.asList(
                BuyerSeat.builder()
                    .bidderId(99)
                    .bidderName("test bidder")
                    .code("88")
                    .name("test")
                    .build()))
        .auctionType(AuctionType.builder().id(1).name("first_price").build())
        .type(Type.builder().id(2).name("Private Auction").build())
        .version(1)
        .build();
  }
}
