package com.ssp.geneva.sdk.identityb2b.config;

import static java.util.Objects.isNull;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityB2bSdkRestTemplateFactory {

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
          restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        }
      }
    }
    return restTemplate;
  }
}
