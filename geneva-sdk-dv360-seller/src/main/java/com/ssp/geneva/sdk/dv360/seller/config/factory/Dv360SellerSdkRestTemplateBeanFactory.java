package com.ssp.geneva.sdk.dv360.seller.config.factory;

import static java.util.Objects.isNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dv360SellerSdkRestTemplateBeanFactory {
  private static RestTemplate restTemplate;

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link RestTemplate}
   */
  public static RestTemplate initRestTemplate() {
    if (isNull(restTemplate)) {
      synchronized (RestTemplate.class) {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
      }
    }
    return restTemplate;
  }
}
