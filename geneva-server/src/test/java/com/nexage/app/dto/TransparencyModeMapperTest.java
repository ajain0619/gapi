/*
 * OATH CONFIDENTIAL INFORMATION.
 *
 * Copyright 2018 OATH, Inc.
 *
 * All Rights Reserved.  Unauthorized reproduction, transmission, or
 * distribution of this software is a violation of applicable laws.
 */
package com.nexage.app.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.util.CustomObjectMapper;
import com.nexage.app.util.json.TransparencyModeJsonDeserializer;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** @author empirl - enrique.mora.parraga@oath.com */
class TransparencyModeMapperTest {

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mapper = new CustomObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(TransparencyMode.class, new TransparencyModeJsonDeserializer());
    mapper.registerModule(module);
  }

  @Test
  void shouldSerialize() throws JsonProcessingException {

    assertTrue(mapper.writeValueAsString(TransparencyMode.None).contains("None"));
    assertTrue(mapper.writeValueAsString(TransparencyMode.RealName).contains("RealName"));
    assertTrue(mapper.writeValueAsString(TransparencyMode.Aliases).contains("Aliases"));
  }

  @Test
  void shouldDeserialize() throws IOException {
    String jsonInput = "\"None\"";
    TransparencyMode deserializedValue = mapper.readValue(jsonInput, TransparencyMode.class);
    assertNotNull(deserializedValue);
    mapper.writeValueAsString(deserializedValue);
  }

  @Test
  void shouldComplainDeserialization() throws IOException {
    String jsonInput = "1";
    JsonMappingException exception =
        assertThrows(
            JsonMappingException.class, () -> mapper.readValue(jsonInput, TransparencyMode.class));
    assertEquals(
        "Not allowed to deserialize Enum value out of JSON number\n"
            + " at [Source: (String)\"1\"; line: 1, column: 1]",
        exception.getMessage());
  }
}
