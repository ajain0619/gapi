package com.nexage.app.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * This custom object mapper extends {@link ObjectMapper}. This is a temporal class to replace
 * programmatically the original mapper definition at app-servlet.xml MVC one.
 *
 * @deprecated This is suppose to be replaced by a single unique object mapper across the app.
 * @see com.nexage.app.util.CustomObjectMapper
 */
@Deprecated
public class CustomViewLayerObjectMapper extends ObjectMapper {

  private static final long serialVersionUID = 1L;

  public CustomViewLayerObjectMapper() {
    super();
    setSerializationInclusion(Include.ALWAYS);
    var hbModule = new Hibernate5Module();
    hbModule.disable(Feature.USE_TRANSIENT_ANNOTATION);
    registerModules(hbModule, new JavaTimeModule());
  }
}
