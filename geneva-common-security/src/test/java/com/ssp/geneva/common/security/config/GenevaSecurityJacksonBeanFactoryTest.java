package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class GenevaSecurityJacksonBeanFactoryTest {

  @Test
  void shouldBuildObjectMapper() {
    ObjectMapper result = GenevaSecurityJacksonBeanFactory.initObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
  }

  @Test
  void shouldBuildSingletonObjectMapper() {
    ObjectMapper result = GenevaSecurityJacksonBeanFactory.initObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    ObjectMapper otherResult = GenevaSecurityJacksonBeanFactory.initObjectMapper();
    assertNotNull(otherResult);
    assertNotNull(otherResult.getDeserializationConfig());
    assertNotNull(otherResult.getSerializationConfig());
    assertEquals(result, otherResult);
  }
}
