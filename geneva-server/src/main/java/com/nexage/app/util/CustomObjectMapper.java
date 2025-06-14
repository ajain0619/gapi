package com.nexage.app.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper extends ObjectMapper {

  private static final long serialVersionUID = 1L;

  public CustomObjectMapper() {
    super();
    setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
        .setSerializationInclusion(Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        .getSerializationConfig();
    var hbModule = new Hibernate5Module();
    hbModule.disable(Feature.USE_TRANSIENT_ANNOTATION);
    registerModules(hbModule, new JavaTimeModule());
  }
}
