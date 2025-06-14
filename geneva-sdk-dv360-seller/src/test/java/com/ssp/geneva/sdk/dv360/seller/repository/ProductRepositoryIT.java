package com.ssp.geneva.sdk.dv360.seller.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.dv360.seller.config.TestRepositoryConfig;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.dv360.seller.model.CreativeConfig;
import com.ssp.geneva.sdk.dv360.seller.model.DimensionCreativeConfig;
import com.ssp.geneva.sdk.dv360.seller.model.Money;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import com.ssp.geneva.sdk.dv360.seller.model.Product;
import com.ssp.geneva.sdk.dv360.seller.model.RateDetails;
import com.ssp.geneva.sdk.dv360.seller.model.type.CreativeType;
import com.ssp.geneva.sdk.dv360.seller.model.type.OrderStatusType;
import com.ssp.geneva.sdk.dv360.seller.model.type.PricingType;
import com.ssp.geneva.sdk.dv360.seller.model.type.RateType;
import com.ssp.geneva.sdk.dv360.seller.model.type.TransactionType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
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
class ProductRepositoryIT {

  private static final int port = 8082;

  @RegisterExtension
  static WireMockExtension wireMockRule =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(port));

  private String updateFields =
      "displayName,startTime,endTime,rateDetails.rateType,rateDetails.rate"
          + ",creativeConfig.creativeType,creativeConfig.dimensionCreativeConfig.width"
          + ",creativeConfig.dimensionCreativeConfig.height";

  private String successBody =
      "{\n"
          + "  \"name\": \"exchanges/10005/orders/25459/products/12345\",\n"
          + "  \"displayName\": \"test deal\",\n"
          + "  \"status\": \"DISCOVERY_OBJECT_STATUS_PAUSED\",\n"
          + "  \"format\": \"DEAL_FORMAT_DISPLAY\",\n"
          + "  \"externalDealId\": \"1608828428493829621\",\n"
          + "  \"rateDetails\": {\n"
          + "    \"currencyCode\": \"USD\",\n"
          + "    \"units\": \"0\",\n"
          + "    \"nanos\": 10000000\n"
          + "  },\n"
          + "  \"startTime\": \"2020-12-24T00:00:00Z\",\n"
          + "  \"endTime\": \"2030-12-31T00:00:00Z\",\n"
          + "  \"pricingType\": \"AUCTION_PRICE\",\n"
          + "  \"transactionType\": \"NON_RESERVED\",\n"
          + "  \"creativeConfig\": [{\n"
          + "    \"dimensionCreativeConfig\": {\n"
          + "      \"height\": 0,\n"
          + "      \"width\": 0\n"
          + "  }}]\n"
          + "}";

  @InjectMocks private ProductRepository productRepository;

  @Autowired RestTemplate dv360SellerRestTemplate;

  @Autowired AccessToken token;

  @Mock GoogleCredentials googleCredentials;

  @Mock DoubleClickBidManager doubleClickBidManager;

  @Value("${dv360.seller.endpoint}")
  private String dv360Endpoint;

  @Value("${dv360.seller.exchange.id}")
  private String dv360ExchangeId;

  @BeforeEach
  void setUp() {
    openMocks(this);
    productRepository =
        new ProductRepository(
            dv360Endpoint + ":" + port,
            dv360ExchangeId,
            dv360SellerRestTemplate,
            doubleClickBidManager,
            googleCredentials);
  }

  private Order buildOrder() {
    return Order.builder()
        .status(OrderStatusType.PENDING_ACCEPTANCE)
        .displayName("test deal")
        .name("exchanges/10005/orders/25459")
        .partnerId(new String[] {"150001"})
        .publisherEmail("dv360@yahooinc.com")
        .publisherName("Yahoo SSP")
        .build();
  }

  private Product buildProduct() {
    return Product.builder()
        .startTime("2020-12-24T00:00:00Z")
        .externalDealId("1608828428493829621")
        .endTime("2030-12-31T00:00:00Z")
        .displayName("test deal")
        .name("exchanges/10005/orders/25459/products/12345")
        .creativeConfig(
            List.of(
                CreativeConfig.builder()
                    .dimensionCreativeConfig(
                        DimensionCreativeConfig.builder().height(0).width(0).build())
                    .creativeType(CreativeType.CREATIVE_TYPE_DISPLAY)
                    .build()))
        .pricingType(PricingType.AUCTION_PRICE)
        .rateDetails(
            RateDetails.builder()
                .rate(Money.buildMoney("USD", BigDecimal.valueOf(0.01D), BigDecimal.valueOf(0.01D)))
                .rateType(RateType.CPM)
                .unitsPurchasedCount(0L)
                .build())
        .transactionType(TransactionType.NON_RESERVED)
        .build();
  }

  @Test
  void successfulCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        post(urlPathMatching("/exchanges/10005/orders/25459/products"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Order order = buildOrder();
    Product productReq = buildProduct();
    ResponseEntity<Product> resp = productRepository.create(order, productReq);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Product productResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459/products/12345", productResp.getName());
  }

  @Test
  void successfulReadRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/orders/25459/products/12345"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    ResponseEntity<Product> resp =
        productRepository.read("exchanges/10005/orders/25459/products/12345");
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Product productResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459/products/12345", productResp.getName());
  }

  @Test
  void successfulUpdateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        patch(urlPathMatching("/exchanges/10005/orders/25459/products/12345"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Product productReq = buildProduct();
    ResponseEntity<Product> resp =
        productRepository.update(
            "exchanges/10005/orders/25459/products/12345", productReq, updateFields);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Product productResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459/products/12345", productResp.getName());
  }

  @Test
  void failedCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          post(urlPathMatching("/exchanges/10005/orders/25459/products"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      Order order = buildOrder();
      Product productReq = buildProduct();
      var exception =
          assertThrows(
              Dv360SellerSdkException.class, () -> productRepository.create(order, productReq));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void failedReadRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          get(urlPathMatching("/exchanges/10005/orders/25459/products/12345"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () -> productRepository.read("exchanges/10005/orders/25459/products/12345"));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void failedUpdateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          patch(urlPathMatching("/exchanges/10005/orders/25459/products/12345"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      Product productReq = buildProduct();
      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () ->
                  productRepository.update(
                      "exchanges/10005/orders/25459/products/12345", productReq, updateFields));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void failedCredentialsRefresh() throws IOException {
    doThrow(new IOException()).when(googleCredentials).refreshIfExpired();
    var exception =
        assertThrows(Dv360SellerSdkException.class, () -> productRepository.getHeaders());
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR,
        exception.getErrorCode());
  }
}
