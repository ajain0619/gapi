package com.nexage.admin.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Map;

public class MapSerializer extends JsonSerializer<Map> {

  @Override
  public void serialize(Map value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(jgen, value);
  }
}
