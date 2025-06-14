package com.ssp.geneva.sdk.messaging.config;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessagingSdkJacksonBeanFactory {

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
          objectMapper.registerModule(new JavaTimeModule());
          objectMapper
              .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
              .setSerializationInclusion(Include.NON_NULL)
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
              .getSerializationConfig();
        }
      }
    }
    return objectMapper;
  }
}
