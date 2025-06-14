package com.ssp.geneva.sdk.xandr.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class XandrSdkConfigTest {

  @InjectMocks XandrSdkConfig xandrSdkConfig;

  private String xandrEndpoint = "http://127.0.0.1:8080";

  private String xandrCredentials =
      """
  {
    "username": "user",
    "password": "pass"
  }
  """;

  private String xandrCredentialsMsRebroadcast =
      """
  {
    "username": "user2",
    "password": "pass2"
  }
  """;

  @Mock XandrSdkConfigProperties xandrSdkConfigProperties;
  @Mock RestTemplate xandrRestTemplate;
  @Mock AuthRepository authRepository;
  @Mock DealRepository dealRepository;

  @BeforeEach
  void setUp() {
    xandrSdkConfig = new XandrSdkConfig();
    xandrSdkConfig.setXandrEndpoint(xandrEndpoint);
    xandrSdkConfig.setXandrCredentials(xandrCredentials);
    xandrSdkConfig.setXandrCredentialsMsRebroadcast(xandrCredentialsMsRebroadcast);
  }

  @Test
  void shouldReturnxandrSdkConfigProperties() {
    assertNotNull(xandrSdkConfig.xandrSdkConfigProperties());
  }

  @Test
  void shouldReturnxandrRestTemplate() {
    assertNotNull(xandrSdkConfig.xandrRestTemplate());
  }

  @Test
  void shouldReturnDealRepository() {
    assertNotNull(xandrSdkConfig.dealRepository(xandrRestTemplate, authRepository));
  }

  @Test
  void shouldReturnAuthRepository() {
    assertNotNull(xandrSdkConfig.authRepository(xandrRestTemplate));
  }

  @Test
  void shouldReturnXandrSdkClient() {
    assertNotNull(
        xandrSdkConfig.xandrSdkClient(
            xandrSdkConfigProperties, xandrRestTemplate, dealRepository, authRepository));
  }
}
