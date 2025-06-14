package com.ssp.geneva.sdk.dv360.seller.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkGoogleCredentialsBeanFactory;
import com.ssp.geneva.sdk.dv360.seller.repository.AuctionPackageRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.OrderRepository;
import com.ssp.geneva.sdk.dv360.seller.repository.ProductRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.client.RestTemplate;

class Dv360SellerSdkConfigTest {

  @InjectMocks Dv360SellerSdkConfig dv360SellerSdkConfig;

  private String access_token = "b89fba14-24ac-481b-a1e2-664c81af8888";

  private String dv360ExchangeId = "10005";

  private String dv360Endpoint = "http://127.0.0.1:8080";

  private String dv360Credentials =
      "{\n"
          + "\"type\": \"service_account\",\n"
          + "\"project_id\": \"123\",\n"
          + "\"private_key_id\": \"123qwe\",\n"
          + "\"private_key\": \"-----BEGIN PRIVATE KEY-----dummy key-----END PRIVATE KEY-----\",\n"
          + "\"client_email\": \"vzm-dv360@test.com\",\n"
          + "\"client_id\": \"123\",\n"
          + "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n"
          + "\"token_uri\": \"https://oauth2.googleapis.com/token\",\n"
          + "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n"
          + "\"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/vzm-dv360%40premium-odyssey-286314.iam.gserviceaccount.com\"\n"
          + "}";

  @Mock Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties;
  @Mock RestTemplate dv360SellerRestTemplate;
  @Mock GoogleCredentials googleCredentials;
  @Mock DoubleClickBidManager doubleClickBidManager;
  @Mock AuctionPackageRepository auctionPackageRepository;
  @Mock OrderRepository orderRepository;
  @Mock ProductRepository productRepository;
  @Mock SimpleDateFormat dv360DateFormat;

  AccessToken token = new AccessToken(access_token, new Date());

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(googleCredentials.getAccessToken()).thenReturn(token);
    dv360SellerSdkConfig = new Dv360SellerSdkConfig();
    dv360SellerSdkConfig.setDv360Endpoint(dv360Endpoint);
    dv360SellerSdkConfig.setDv360ExchangeId(dv360ExchangeId);
    dv360SellerSdkConfig.setDv360Credentials(dv360Credentials);
  }

  @Test
  void shouldReturnDv360SellerSdkConfigProperties() {
    assertNotNull(dv360SellerSdkConfig.dv360SellerSdkConfigProperties());
  }

  @Test
  void shouldReturnDv360SellerRestTemplate() {
    assertNotNull(dv360SellerSdkConfig.dv360SellerRestTemplate());
  }

  @Test
  void shouldReturnGoogleCredentials() {
    MockedStatic<Dv360SellerSdkGoogleCredentialsBeanFactory> mockedStatic =
        mockStatic(Dv360SellerSdkGoogleCredentialsBeanFactory.class);
    mockedStatic
        .when(() -> Dv360SellerSdkGoogleCredentialsBeanFactory.initGoogleCredentials(anyString()))
        .thenReturn(GoogleCredentials.newBuilder().build());

    GoogleCredentials credentials = dv360SellerSdkConfig.googleCredentials();
    assertNotNull(credentials);

    mockedStatic.close();
  }

  @Test
  void shouldReturnDoubleClickBidManager() {
    assertNotNull(dv360SellerSdkConfig.doubleClickBidManager(googleCredentials));
  }

  @Test
  void shouldReturnAuctionPackageRepository() {
    assertNotNull(
        dv360SellerSdkConfig.auctionPackageRepository(
            dv360SellerRestTemplate, googleCredentials, doubleClickBidManager));
  }

  @Test
  void shouldReturnOrderRepository() {
    assertNotNull(
        dv360SellerSdkConfig.orderRepository(
            dv360SellerRestTemplate, googleCredentials, doubleClickBidManager));
  }

  @Test
  void shouldReturnProductRepository() {
    assertNotNull(
        dv360SellerSdkConfig.productRepository(
            dv360SellerRestTemplate, googleCredentials, doubleClickBidManager));
  }

  @Test
  void shouldReturnDv360SellerSdkClient() {
    assertNotNull(
        dv360SellerSdkConfig.dv360SellerSdkClient(
            dv360SellerSdkConfigProperties,
            dv360SellerRestTemplate,
            googleCredentials,
            doubleClickBidManager,
            auctionPackageRepository,
            orderRepository,
            productRepository,
            dv360DateFormat));
  }

  @Test
  void shouldReturnDv360DateFormat() {
    assertNotNull(dv360SellerSdkConfig.dv360DateFormat());
    assertNotNull(dv360SellerSdkConfig.dv360DateFormat().toPattern());
    assertEquals(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", dv360SellerSdkConfig.dv360DateFormat().toPattern());
  }
}
