package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.MockitoAnnotations.openMocks;

import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.TestConfig;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource("classpath:application-test.properties")
class Dv360SellerSdkGoogleCredentialsBeanFactoryTest {

  private String credentialString =
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

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void testSuccessFullInitMethod() {
    GoogleCredentials createdCreds = GoogleCredentials.newBuilder().build();
    MockedStatic<GoogleCredentials> mockedStatic = mockStatic(GoogleCredentials.class);
    mockedStatic
        .when(() -> GoogleCredentials.fromStream(Mockito.any(InputStream.class)))
        .thenReturn(createdCreds);
    GoogleCredentials creds =
        Dv360SellerSdkGoogleCredentialsBeanFactory.initGoogleCredentials(credentialString);
    assertNotNull(creds);
    assertEquals(createdCreds, creds);

    GoogleCredentials creds2 =
        Dv360SellerSdkGoogleCredentialsBeanFactory.initGoogleCredentials(credentialString);
    assertNotNull(creds2);
    assertEquals(createdCreds, creds2);

    mockedStatic.close();
  }

  @Test
  void testFailingFullInitMethod() {
    var exception =
        assertThrows(
            Dv360SellerSdkException.class,
            () ->
                Dv360SellerSdkGoogleCredentialsBeanFactory.initGoogleCredentials(credentialString));
    assertNotNull(exception);
    assertNotNull(exception.getErrorCode());
    assertEquals(
        Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR,
        exception.getErrorCode());
  }
}
