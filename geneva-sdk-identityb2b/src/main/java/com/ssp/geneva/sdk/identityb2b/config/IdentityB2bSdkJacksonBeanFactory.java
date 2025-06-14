package com.ssp.geneva.sdk.identityb2b.config;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityB2bSdkJacksonBeanFactory {

  private static ObjectMapper objectMapper;

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link ObjectMapper}
   */
  public static ObjectMapper initObjectMapper() {
    if (isNull(objectMapper)) {
      synchronized (ObjectMapper.class) {
        if (isNull(objectMapper)) {
          objectMapper = new ObjectMapper();
          objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
      }
    }
    return objectMapper;
  }
}
