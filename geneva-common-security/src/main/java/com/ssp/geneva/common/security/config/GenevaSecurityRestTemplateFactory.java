package com.ssp.geneva.common.security.config;

import static java.util.Objects.isNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenevaSecurityRestTemplateFactory {

  private static RestTemplate restTemplate;

  /**
   * Initialization of Synchronous client to perform HTTP requests.
   *
   * @return {@link RestTemplate}
   */
  public static RestTemplate initRestTemplate() {
    if (isNull(restTemplate)) {
      synchronized (RestTemplate.class) {
        if (isNull(restTemplate)) {
          restTemplate = new RestTemplate();
        }
      }
    }
    return restTemplate;
  }
}
