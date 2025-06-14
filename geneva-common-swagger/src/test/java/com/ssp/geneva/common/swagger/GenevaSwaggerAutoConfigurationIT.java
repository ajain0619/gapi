package com.ssp.geneva.common.swagger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenevaSwaggerAutoConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class GenevaSwaggerAutoConfigurationIT {

  @Autowired GenevaSwaggerAutoConfiguration genevaSwaggerAutoConfiguration;

  @Test
  void shouldTestCustomImplementation() {
    OpenAPI openApi = genevaSwaggerAutoConfiguration.genevaSwaggerAPI();
    assertNotNull(openApi);
    assertNotNull(openApi.getInfo());
    assertNotNull(openApi.getInfo().getTitle());
    assertEquals(openApi.getInfo().getTitle(), "whatever-title");
    assertNotNull(openApi.getInfo().getDescription());
    assertEquals(openApi.getInfo().getDescription(), "whatever-description");
  }
}
