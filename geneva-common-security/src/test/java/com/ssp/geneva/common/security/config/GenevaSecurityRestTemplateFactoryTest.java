package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class GenevaSecurityRestTemplateFactoryTest {

  @Test
  void shouldBuildRestTemplate() {
    RestTemplate result = GenevaSecurityRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
  }

  @Test
  void shouldBuildSingletonRestTemplate() {
    RestTemplate result = GenevaSecurityRestTemplateFactory.initRestTemplate();
    assertNotNull(result);
    RestTemplate otherResult = GenevaSecurityRestTemplateFactory.initRestTemplate();
    assertNotNull(otherResult);
    assertEquals(result, otherResult);
  }
}
