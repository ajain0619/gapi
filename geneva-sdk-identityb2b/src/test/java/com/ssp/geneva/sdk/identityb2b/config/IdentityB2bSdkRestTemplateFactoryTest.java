package com.ssp.geneva.sdk.identityb2b.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class IdentityB2bSdkRestTemplateFactoryTest {

  @Test
  void shouldBuildRestTemplate() {
    RestTemplate result = IdentityB2bSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
  }

  @Test
  void shouldBuildSingletonRestTemplate() {
    RestTemplate result = IdentityB2bSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
    RestTemplate otherResult = IdentityB2bSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(otherResult);
    assertEquals(result, otherResult);
  }
}
