package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.CustomViewLayerObjectMapper;
import org.junit.jupiter.api.Test;

class GenevaServerJacksonBeanFactoryTest {

  @Test
  void shouldBuildCustomViewLayerObjectMapper() {
    ObjectMapper result = GenevaServerJacksonBeanFactory.initCustomViewLayerObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    assertTrue(result instanceof CustomViewLayerObjectMapper);
  }

  @Test
  void shouldBuildSingletonCustomViewLayerObjectMapper() {
    ObjectMapper result = GenevaServerJacksonBeanFactory.initCustomViewLayerObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    ObjectMapper otherResult = GenevaServerJacksonBeanFactory.initCustomViewLayerObjectMapper();
    assertNotNull(otherResult);
    assertNotNull(otherResult.getDeserializationConfig());
    assertNotNull(otherResult.getSerializationConfig());
    assertEquals(result, otherResult);
  }

  @Test
  void shouldBuildCustomViewObjectMapper() {
    ObjectMapper result = GenevaServerJacksonBeanFactory.initCustomObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    assertTrue(result instanceof CustomObjectMapper);
  }

  @Test
  void shouldBuildSingletonCustomViewObjectMapper() {
    ObjectMapper result = GenevaServerJacksonBeanFactory.initCustomObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    ObjectMapper otherResult = GenevaServerJacksonBeanFactory.initCustomObjectMapper();
    assertNotNull(otherResult);
    assertNotNull(otherResult.getDeserializationConfig());
    assertNotNull(otherResult.getSerializationConfig());
    assertEquals(result, otherResult);
  }
}
