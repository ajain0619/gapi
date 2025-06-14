package com.ssp.geneva.common.security.config;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenevaSecurityJacksonBeanFactory {

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
          objectMapper
              .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
              .setSerializationInclusion(Include.NON_NULL)
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
              .getSerializationConfig();
          var hbModule = new Hibernate5Module();
          hbModule.disable(Feature.USE_TRANSIENT_ANNOTATION);
          objectMapper.registerModules(hbModule, new JavaTimeModule());
        }
      }
    }
    return objectMapper;
  }
}
