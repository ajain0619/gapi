package com.nexage.app.util;

import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceLoader {

  public static InputStream getResourceAsStream(String resource) {
    return org.springframework.core.io.ResourceLoader.class.getResourceAsStream(resource);
  }

  public static String getResource(final Class<?> clazz, final String resource) throws IOException {
    return Resources.toString(Resources.getResource(clazz, resource), StandardCharsets.UTF_8);
  }
}
