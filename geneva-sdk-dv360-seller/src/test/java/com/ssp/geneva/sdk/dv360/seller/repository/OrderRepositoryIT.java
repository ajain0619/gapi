package com.ssp.geneva.sdk.dv360.seller.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
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
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import com.ssp.geneva.sdk.dv360.seller.model.Order;
import com.ssp.geneva.sdk.dv360.seller.model.type.OrderStatusType;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class})
@TestPropertySource("classpath:application-test.properties")
class OrderRepositoryIT {

  private static final int port = 8083;

  @RegisterExtension
  static WireMockExtension wireMockRule =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(port));

  private String updateFields = "displayName,partnerId,publisherName,publisherEmail";

  private String successBody =
      "{\n"
          + "  \"name\": \"exchanges/10005/orders/25459\",\n"
          + "  \"displayName\": \"test deal\",\n"
          + "  \"status\": \"PENDING_ACCEPTANCE\",\n"
          + "  \"publisherEmail\": \"dv360@yahooinc.com\",\n"
          + "  \"publisherName\": \"Yahoo SSP\"\n"
          + "}";

  private String parameterizedSuccessBody =
      "{\n"
          + " \"order\" : [ \n"
          + "   {\n"
          + "        \"name\": \"exchanges/10005/orders/25459\",\n"
          + "        \"status\": \"PENDING_ACCEPTANCE\",\n"
          + "        \"displayName\": \"test deal\"\n"
          + "    },\n"
          + "    {\n"
          + "         \"name\": \"exchanges/10005/orders/25460\",\n"
          + "         \"status\": \"PENDING_ACCEPTANCE\",\n"
          + "         \"displayName\": \"test deal\"\n"
          + "     }\n"
          + "\t]\n"
          + "}";

  @InjectMocks private OrderRepository orderRepository;

  @Autowired RestTemplate dv360SellerRestTemplate;

  @Autowired AccessToken token;

  @Mock GoogleCredentials googleCredentials;

  @Mock DoubleClickBidManager doubleClickBidManager;

  @Value("${dv360.seller.endpoint}")
  private String dv360Endpoint;

  @Value("${dv360.seller.exchange.id:25459}")
  private String dv360ExchangeId;

  @BeforeEach
  void setUp() {
    openMocks(this);
    orderRepository =
        new OrderRepository(
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
        .name("exchanges/10005/orders")
        .partnerId(new String[] {"150001"})
        .publisherEmail("dv360@yahooinc.com")
        .publisherName("Yahoo SSP")
        .build();
  }

  @Test
  void successfulCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        post(urlPathMatching("/exchanges/10005/orders"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Order orderReq = buildOrder();
    ResponseEntity<Order> resp = orderRepository.create(orderReq);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Order orderResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459", orderResp.getName());
  }

  @Test
  void successfulReadRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/orders/25459"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    ResponseEntity<Order> resp = orderRepository.read("exchanges/10005/orders/25459");
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Order orderResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459", orderResp.getName());
  }

  @Test
  void successfulGetRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlEqualTo("/exchanges/10005/orders?pageToken=abc&filter=test"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(parameterizedSuccessBody)));

    ResponseEntity<Dv360PagedResponse<Order>> resp = orderRepository.get("abc", "test");
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Dv360PagedResponse<Order> orderResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459", orderResp.getResponse().get(0).getName());
    assertEquals("exchanges/10005/orders/25460", orderResp.getResponse().get(1).getName());
  }

  @Test
  void shouldThrowExceptionWhenPerformGetWithPageTokenContainingPlusSign() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/orders"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .withQueryParam("pageToken", containing("+"))
            .willReturn(aResponse().withStatus(400)));

    assertThrows(Dv360SellerSdkException.class, () -> orderRepository.get("page+token", ""));
  }

  @Test
  void shouldReturnSuccessWhenPerformGetWithPageTokenContainingEncodedPlusSign() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/orders"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .withQueryParam("pageToken", containing(URLEncoder.encode("+", StandardCharsets.UTF_8)))
            .willReturn(aResponse().withStatus(200)));
    ResponseEntity<Dv360PagedResponse<Order>> resp = orderRepository.get("page%2Btoken", "");
    assertNotNull(resp);
    assertEquals(HttpStatus.OK.value(), resp.getStatusCodeValue());
  }

  @Test
  void successfulUpdateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        patch(urlPathMatching("/exchanges/10005/orders/25459"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    Order orderReq = buildOrder();
    ResponseEntity<Order> resp =
        orderRepository.update("exchanges/10005/orders/25459", orderReq, updateFields);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Order orderResp = resp.getBody();
    assertEquals("exchanges/10005/orders/25459", orderResp.getName());
  }

  @Test
  void failedCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          post(urlPathMatching("/exchanges/10005/orders"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      Order orderReq = buildOrder();
      var exception =
          assertThrows(Dv360SellerSdkException.class, () -> orderRepository.create(orderReq));
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
          get(urlPathMatching("/exchanges/10005/orders/25459"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () -> orderRepository.read("exchanges/10005/orders/25459"));
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
          patch(urlPathMatching("/exchanges/10005/orders/25459"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      Order orderReq = buildOrder();
      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () -> orderRepository.update("exchanges/10005/orders/25459", orderReq, updateFields));
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }

  @Test
  void failedCredentialsRefresh() throws IOException {
    doThrow(new IOException()).when(googleCredentials).refreshIfExpired();
    Dv360SellerSdkException exception =
        assertThrows(Dv360SellerSdkException.class, () -> orderRepository.getHeaders());
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR,
        exception.getErrorCode());
  }
}
