package com.ssp.geneva.sdk.messaging.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MessagingSdkJacksonBeanFactoryTest {

  @Test
  void testBuildObjectMapperSuccessfully() {
    final ObjectMapper objectMapper = MessagingSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(objectMapper);
  }

  @Test
  void whenBuildObjectMapper_thenObjectsAreTheSame() {
    final ObjectMapper objectMapper = MessagingSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(objectMapper);
    final ObjectMapper otherBbjectMapper = MessagingSdkJacksonBeanFactory.initObjectMapper();
    assertNotNull(objectMapper);
    assertEquals(objectMapper, otherBbjectMapper);
  }
}
