package com.ssp.geneva.sdk.onecentral.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class OneCentralSdkRestTemplateFactoryTest {

  @Test
  void shouldBuildRestTemplate() {
    RestTemplate result = OneCentralSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
  }

  @Test
  void shouldBuildSingletonRestTemplate() {
    RestTemplate result = OneCentralSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
    RestTemplate otherResult = OneCentralSdkRestTemplateFactory.initRestTemplate();
    assertNotNull(otherResult);
    assertEquals(result, otherResult);
  }
}
