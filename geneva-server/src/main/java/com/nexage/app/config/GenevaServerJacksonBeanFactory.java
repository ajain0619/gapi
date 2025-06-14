package com.nexage.app.config;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenevaServerJacksonBeanFactory {

  private static CustomViewLayerObjectMapper customViewLayerObjectMapper;
  private static CustomObjectMapper customObjectMapper;

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link ObjectMapper}
   * @deprecated please use {@link #initCustomObjectMapper()} instead.
   */
  @Deprecated
  public static CustomViewLayerObjectMapper initCustomViewLayerObjectMapper() {
    if (isNull(customViewLayerObjectMapper)) {
      synchronized (ObjectMapper.class) {
        if (isNull(customViewLayerObjectMapper)) {
          customViewLayerObjectMapper = new CustomViewLayerObjectMapper();
        }
      }
    }
    return customViewLayerObjectMapper;
  }

  /**
   * Initialization of the json object mapper.
   *
   * @return {@link ObjectMapper}
   */
  public static CustomObjectMapper initCustomObjectMapper() {
    if (isNull(customObjectMapper)) {
      synchronized (ObjectMapper.class) {
        if (isNull(customObjectMapper)) {
          customObjectMapper = new CustomObjectMapper();
        }
      }
    }
    return customObjectMapper;
  }
}
