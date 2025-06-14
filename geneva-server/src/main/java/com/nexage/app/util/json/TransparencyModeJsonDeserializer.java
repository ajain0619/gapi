package com.nexage.app.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.nexage.app.dto.transparency.TransparencyMode;
import java.io.IOException;

public class TransparencyModeJsonDeserializer extends JsonDeserializer<TransparencyMode> {

  @Override
  public TransparencyMode deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
      throw ctxt.mappingException("Not allowed to deserialize Enum value out of JSON number");
    }
    try {
      String stringValue = jp.getValueAsString();
      return Enum.valueOf(TransparencyMode.class, stringValue);
    } catch (IllegalArgumentException e) {
      return Enum.valueOf(TransparencyMode.class, "NONE");
    }
  }
}
