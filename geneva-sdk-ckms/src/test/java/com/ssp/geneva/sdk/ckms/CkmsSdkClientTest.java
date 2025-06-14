package com.ssp.geneva.sdk.ckms;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.ssp.geneva.sdk.ckms.config.CkmsSdkConfigProperties;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CkmsSdkClientTest {

  private static CkmsSdkConfigProperties ckmsSdkConfigProperties;
  private static CkmsProvider ckmsProvider;

  @BeforeEach
  void setUp() {
    ckmsSdkConfigProperties = mock(CkmsSdkConfigProperties.class);
    ckmsProvider = mock(CkmsProvider.class);
  }

  @Test
  @DisplayName("Should construct CkmsSdkClient and not be Null")
  void shouldConstructCkmsSdkClientAndNotBeNull() {

    // when
    CkmsSdkClient ckmsSdkClient = new CkmsSdkClient(ckmsSdkConfigProperties, ckmsProvider);

    // then
    assertNotNull(ckmsSdkClient);
    assertNotNull(ckmsSdkClient.getCkmsSdkConfigProperties());
    assertNotNull(ckmsSdkClient.getCkmsProvider());
  }
}
