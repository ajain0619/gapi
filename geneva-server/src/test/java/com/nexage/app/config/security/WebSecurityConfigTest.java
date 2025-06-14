package com.nexage.app.config.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class WebSecurityConfigTest {

  @Test
  void shouldCreateAutoConfigurationBeans() {
    CorsEndpointProperties corsEndpointProperties = new CorsEndpointProperties();
    corsEndpointProperties.setAllowedOrigins(List.of("www.yahooinc.com"));
    corsEndpointProperties.setAllowedMethods(List.of("POST"));
    corsEndpointProperties.setAllowedHeaders(List.of("Origin"));
    CorsWebSecurityConfig corsWebSecurityConfig = new CorsWebSecurityConfig(corsEndpointProperties);
    GenevaServerCorsConfig corsConfig = corsWebSecurityConfig.genevaServerCorsConfig();

    assertNotNull(corsConfig);
    UrlBasedCorsConfigurationSource configurationSource =
        corsWebSecurityConfig.urlBasedCorsConfigurationSource(corsConfig);
    assertNotNull(configurationSource);
  }
}
