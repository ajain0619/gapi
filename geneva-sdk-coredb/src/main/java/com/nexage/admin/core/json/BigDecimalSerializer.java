package com.nexage.admin.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    if (value == null) {
      value = BigDecimal.ZERO;
    }
    jgen.writeNumber(value.setScale(4));
  }
}
