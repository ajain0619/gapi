package com.ssp.geneva.sdk.xandr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ssp.geneva.sdk.xandr.config.XandrSdkConfigProperties;
import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class XandrSdkClientTest {

  @InjectMocks XandrSdkClient xandrSdkClient;

  @Mock XandrSdkConfigProperties xandrSdkConfigProperties;
  @Mock RestTemplate xandrRestTemplate;
  @Mock DealRepository dealRepository;
  @Mock AuthRepository authRepository;

  @Test
  void shouldReturnXandrSdkClient() {
    xandrSdkClient =
        XandrSdkClient.builder()
            .xandrSdkConfigProperties(xandrSdkConfigProperties)
            .xandrRestTemplate(xandrRestTemplate)
            .dealRepository(dealRepository)
            .authRepository(authRepository)
            .build();
    assertEquals(xandrSdkConfigProperties, xandrSdkClient.getXandrSdkConfigProperties());
    assertEquals(xandrRestTemplate, xandrSdkClient.getXandrRestTemplate());
    assertEquals(dealRepository, xandrSdkClient.getDealRepository());
    assertEquals(authRepository, xandrSdkClient.getAuthRepository());
  }
}
