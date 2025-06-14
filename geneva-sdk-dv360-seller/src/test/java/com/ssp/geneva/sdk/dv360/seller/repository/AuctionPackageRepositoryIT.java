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
import com.ssp.geneva.sdk.dv360.seller.model.AuctionPackage;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360PagedResponse;
import com.ssp.geneva.sdk.dv360.seller.model.Money;
import com.ssp.geneva.sdk.dv360.seller.model.type.FormatType;
import com.ssp.geneva.sdk.dv360.seller.model.type.MediumType;
import com.ssp.geneva.sdk.dv360.seller.model.type.StatusType;
import java.io.IOException;
import java.math.BigDecimal;
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
class AuctionPackageRepositoryIT {

  private static final int port = 8081;

  @RegisterExtension
  static WireMockExtension wireMockRule =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(port));

  private String updateFields =
      "displayName,description,floorPrice.currencyCode,floorPrice.units,floorPrice.nanos,startTime"
          + ",endTime,mediumType";

  private String successBody =
      "{\n"
          + "  \"name\": \"exchanges/10005/auctionPackages/25459\",\n"
          + "  \"displayName\": \"test deal\",\n"
          + "  \"status\": \"DISCOVERY_OBJECT_STATUS_PAUSED\",\n"
          + "  \"description\": \"test deal\",\n"
          + "  \"format\": \"DEAL_FORMAT_DISPLAY\",\n"
          + "  \"externalDealId\": \"1608828428493829621\",\n"
          + "  \"floorPrice\": {\n"
          + "    \"currencyCode\": \"USD\",\n"
          + "    \"units\": \"0\",\n"
          + "    \"nanos\": 10000000\n"
          + "  },\n"
          + "  \"startTime\": \"2020-12-24T00:00:00Z\",\n"
          + "  \"endTime\": \"2030-12-31T00:00:00Z\",\n"
          + "  \"mediumType\": \"MEDIUM_TYPE_DIGITAL\"\n"
          + "}";

  private String parameterizedSuccessBody =
      "{\n"
          + " \"auctionPackage\" : [ \n"
          + "   {\n"
          + "        \"name\": \"exchanges/10005/auctionPackages/25459\",\n"
          + "        \"status\": \"DISCOVERY_OBJECT_STATUS_PAUSED\",\n"
          + "        \"displayName\": \"test deal\"\n"
          + "    },\n"
          + "    {\n"
          + "         \"name\": \"exchanges/10005/auctionPackages/25460\",\n"
          + "         \"status\": \"DISCOVERY_OBJECT_STATUS_PAUSED\",\n"
          + "         \"displayName\": \"test deal\"\n"
          + "     }\n"
          + "\t]\n"
          + "}";

  @InjectMocks private AuctionPackageRepository auctionPackageRepository;

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
    auctionPackageRepository =
        new AuctionPackageRepository(
            dv360Endpoint + ":" + port,
            dv360ExchangeId,
            dv360SellerRestTemplate,
            doubleClickBidManager,
            googleCredentials);
  }

  private AuctionPackage buildAuctionPackage() {
    return AuctionPackage.builder()
        .status(StatusType.DISCOVERY_OBJECT_STATUS_PAUSED)
        .startTime("2020-12-24T00:00:00Z")
        .mediumType(MediumType.MEDIUM_TYPE_DIGITAL)
        .format(FormatType.DEAL_FORMAT_DISPLAY)
        .floorPrice(Money.buildMoney("USD", BigDecimal.valueOf(0.01D), BigDecimal.valueOf(0.01D)))
        .externalDealId("1608828428493829621")
        .endTime("2030-12-31T00:00:00Z")
        .displayName("test deal")
        .description("test deal")
        .name("exchanges/10005/auctionPackages/25459")
        .build();
  }

  @Test
  void successfulCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        post(urlPathMatching("/exchanges/10005/auctionPackages"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    AuctionPackage auctionPackageReq = buildAuctionPackage();
    ResponseEntity<AuctionPackage> resp = auctionPackageRepository.create(auctionPackageReq);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    AuctionPackage auctionPackageResp = resp.getBody();
    assertEquals("exchanges/10005/auctionPackages/25459", auctionPackageResp.getName());
  }

  @Test
  void successfulReadRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/auctionPackages/25459"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    ResponseEntity<AuctionPackage> resp = auctionPackageRepository.read("25459");
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    AuctionPackage auctionPackageResp = resp.getBody();
    assertEquals("exchanges/10005/auctionPackages/25459", auctionPackageResp.getName());
  }

  @Test
  void successfulGetRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlEqualTo("/exchanges/10005/auctionPackages?pageToken=abc&filter=externalDealId=test"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(parameterizedSuccessBody)));

    ResponseEntity<Dv360PagedResponse<AuctionPackage>> resp =
        auctionPackageRepository.get("abc", "test");
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    Dv360PagedResponse<AuctionPackage> auctionPackageResp = resp.getBody();
    assertEquals(
        "exchanges/10005/auctionPackages/25459", auctionPackageResp.getResponse().get(0).getName());
  }

  @Test
  void shouldThrowExceptionWhenPerformGetWithPageTokenContainingPlusSign() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/auctionPackages"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .withQueryParam("pageToken", containing("+"))
            .willReturn(aResponse().withStatus(400)));
    assertThrows(
        Dv360SellerSdkException.class, () -> auctionPackageRepository.get("page+token", ""));
  }

  @Test
  void shouldReturnSuccessWhenPerformGetWithPageTokenContainingEncodedPlusSign() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        get(urlPathMatching("/exchanges/10005/auctionPackages"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .withQueryParam("pageToken", containing(URLEncoder.encode("+", StandardCharsets.UTF_8)))
            .willReturn(aResponse().withStatus(200)));
    ResponseEntity<Dv360PagedResponse<AuctionPackage>> resp =
        auctionPackageRepository.get("page%2Btoken", "");
    assertNotNull(resp);
    assertEquals(HttpStatus.OK.value(), resp.getStatusCodeValue());
  }

  @Test
  void successfulUpdateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    wireMockRule.stubFor(
        patch(urlPathMatching("/exchanges/10005/auctionPackages/25459"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBody)));

    AuctionPackage auctionPackageReq = buildAuctionPackage();
    ResponseEntity<AuctionPackage> resp =
        auctionPackageRepository.update(
            "exchanges/10005/auctionPackages/25459", auctionPackageReq, updateFields);
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    AuctionPackage auctionPackageResp = resp.getBody();
    assertEquals("exchanges/10005/auctionPackages/25459", auctionPackageResp.getName());
  }

  @Test
  void failedCreateRequest() {
    when(googleCredentials.getAccessToken()).thenReturn(token);
    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          post(urlPathMatching("/exchanges/10005/auctionPackages"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      AuctionPackage auctionPackageReq = buildAuctionPackage();
      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () -> auctionPackageRepository.create(auctionPackageReq));

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
          get(urlPathMatching("/exchanges/10005/auctionPackages/25459"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () -> auctionPackageRepository.read("exchanges/10005/auctionPackages/25459"));

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
          patch(urlPathMatching("/exchanges/10005/auctionPackages/25459"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      AuctionPackage auctionPackageReq = buildAuctionPackage();
      var exception =
          assertThrows(
              Dv360SellerSdkException.class,
              () ->
                  auctionPackageRepository.update(
                      "exchanges/10005/auctionPackages/25459", auctionPackageReq, updateFields));

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
        assertThrows(Dv360SellerSdkException.class, () -> auctionPackageRepository.getHeaders());
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR,
        exception.getErrorCode());
  }
}
