package com.ssp.geneva.sdk.onecentral.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkJacksonBeanFactory;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse.OneCentralErrorResponseBody;
import java.util.List;
import org.junit.jupiter.api.Test;

class OneCentralErrorResponseTest {

  ObjectMapper objectMapper = OneCentralSdkJacksonBeanFactory.initObjectMapper();

  @Test
  void shouldMap() throws JsonProcessingException {
    String request =
        "[\n"
            + "  {\n"
            + "    \"code\": 400,\n"
            + "    \"message\": \"First name cannot be empty\",\n"
            + "    \"detail\": \"First name cannot be empty\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"code\": 400,\n"
            + "    \"message\": \"Status cannot be empty.\",\n"
            + "    \"detail\": \"Status cannot be empty.\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"code\": 400,\n"
            + "    \"message\": \"Last name cannot be empty\",\n"
            + "    \"detail\": \"Last name cannot be empty\"\n"
            + "  },\n"
            + "  {\n"
            + "    \"code\": 400,\n"
            + "    \"message\": \"Email address cannot be empty\",\n"
            + "    \"detail\": \"Email address cannot be empty\"\n"
            + "  }\n"
            + "]";
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    List<OneCentralErrorResponseBody> response =
        objectMapper.readValue(request, new TypeReference<>() {});
    assertNotNull(response);
    assertFalse(response.isEmpty());
    assertEquals(4, response.size());
  }
}
