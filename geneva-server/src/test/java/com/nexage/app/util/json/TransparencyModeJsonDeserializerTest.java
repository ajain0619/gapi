/*
 * OATH CONFIDENTIAL INFORMATION.
 *
 * Copyright 2018 OATH, Inc.
 *
 * All Rights Reserved.  Unauthorized reproduction, transmission, or
 * distribution of this software is a violation of applicable laws.
 */
package com.nexage.app.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author empirl - enrique.mora.parraga@oath.com */
@ExtendWith(MockitoExtension.class)
class TransparencyModeJsonDeserializerTest {

  private ObjectMapper mapper;

  private TransparencyModeJsonDeserializer transparencyModeJsonDeserializer =
      new TransparencyModeJsonDeserializer();

  @BeforeEach
  void setup() {
    mapper = new CustomObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(TransparencyMode.class, new TransparencyModeJsonDeserializer());
    mapper.registerModule(module);
  }

  @Test
  void shouldDeserialize() throws IOException {
    JsonParser jsonParser = mock(JsonParser.class);
    when(jsonParser.getValueAsString()).thenReturn(TransparencyMode.None.name());
    DeserializationContext deserializationContext = mock(DeserializationContext.class);
    TransparencyMode transparencyMode =
        transparencyModeJsonDeserializer.deserialize(jsonParser, deserializationContext);
    assertEquals(TransparencyMode.None, transparencyMode);
  }

  @Test
  void shouldThrowExceptionDueToIntegerToken() {
    String message = "Not allowed to deserialize Enum value out of JSON number";
    JsonParser jsonParser = mock(JsonParser.class);
    when(jsonParser.getCurrentToken()).thenReturn(JsonToken.VALUE_NUMBER_INT);
    DeserializationContext deserializationContext = mock(DeserializationContext.class);
    when(deserializationContext.mappingException(anyString()))
        .thenAnswer(
            invocation -> {
              throw new JsonMappingException(message);
            });
    JsonMappingException exception =
        assertThrows(
            JsonMappingException.class,
            () -> transparencyModeJsonDeserializer.deserialize(jsonParser, deserializationContext));

    assertEquals(message, exception.getMessage());
  }
}
