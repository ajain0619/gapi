package com.ssp.geneva.sdk.identityb2b.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class IdentityB2bSdkJacksonBeanFactoryTest {
  @Test
  void shouldBuildObjectMapper() {
    ObjectMapper result = IdentityB2bSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
  }

  @Test
  void shouldBuildSingletonObjectMapper() {
    ObjectMapper result = IdentityB2bSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(result);
    assertNotNull(result.getDeserializationConfig());
    assertNotNull(result.getSerializationConfig());
    ObjectMapper otherResult = IdentityB2bSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(otherResult);
    assertNotNull(otherResult.getDeserializationConfig());
    assertNotNull(otherResult.getSerializationConfig());
    assertEquals(result, otherResult);
  }
}
