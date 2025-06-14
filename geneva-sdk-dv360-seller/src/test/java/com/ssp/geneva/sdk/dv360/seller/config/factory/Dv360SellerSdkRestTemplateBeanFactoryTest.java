package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class Dv360SellerSdkRestTemplateBeanFactoryTest {
  @Test
  void shouldBuildObjectSuccessfully() {
    final RestTemplate restTemplate = Dv360SellerSdkRestTemplateBeanFactory.initRestTemplate();
    assertNotNull(restTemplate);

    final RestTemplate restTemplate2 = Dv360SellerSdkRestTemplateBeanFactory.initRestTemplate();
    assertNotNull(restTemplate2);
  }
}
