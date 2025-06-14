package com.nexage.admin.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Map;

public class MapDeserializer extends JsonDeserializer<Map> {

  @Override
  public Map deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    TypeReference<Map<Long, String>> mapType = new TypeReference<>() {};
    return jp.readValueAs(mapType);
  }
}
