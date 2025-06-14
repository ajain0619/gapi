package com.nexage.app.config.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles({"secure", "test"})
@TestPropertySource(
    properties = {
      "management.endpoints.web.cors.allowed-origins=ssp.yahooinc.com,http://localhost,http://localhost:9000",
      "management.endpoints.web.cors.allowed-methods=GET,POST,DELETE",
      "management.endpoints.web.cors.allowed-headers=Content-Type,X-Requested-With,Accept"
    })
@EnableConfigurationProperties(value = CorsEndpointProperties.class)
@ContextConfiguration(classes = {CorsWebSecurityConfig.class})
class CorsWebSecurityConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    GenevaServerCorsConfig genevaServerCorsConfig =
        (GenevaServerCorsConfig) context.getBean("genevaServerCorsConfig");
    assertNotNull(genevaServerCorsConfig);

    var allowedOrigins = genevaServerCorsConfig.getAllowedOrigins();
    assertNotNull(allowedOrigins);
    assertFalse(allowedOrigins.isEmpty());
    assertEquals(3, allowedOrigins.size());
    assertTrue(allowedOrigins.contains("ssp.yahooinc.com"));
    assertTrue(allowedOrigins.contains("http://localhost"));
    assertTrue(allowedOrigins.contains("http://localhost:9000"));

    var allowedMethods = genevaServerCorsConfig.getAllowedMethods();
    assertNotNull(allowedMethods);
    assertFalse(allowedMethods.isEmpty());
    assertEquals(3, allowedMethods.size());
    assertTrue(allowedMethods.contains("GET"));
    assertTrue(allowedMethods.contains("POST"));
    assertTrue(allowedMethods.contains("DELETE"));

    var allowedHeaders = genevaServerCorsConfig.getAllowedHeaders();
    assertNotNull(allowedHeaders);
    assertFalse(allowedHeaders.isEmpty());
    assertEquals(3, allowedHeaders.size());
    assertTrue(allowedHeaders.contains("Content-Type"));
    assertTrue(allowedHeaders.contains("X-Requested-With"));
    assertTrue(allowedHeaders.contains("Accept"));
  }
}
