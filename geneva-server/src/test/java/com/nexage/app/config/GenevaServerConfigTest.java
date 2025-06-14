package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@Log4j2
@ExtendWith(MockitoExtension.class)
class GenevaServerConfigTest {

  private final GenevaServerConfig genevaServerConfig = new GenevaServerConfig();

  static {
    System.setProperty("geneva.features", "file:/tmp");
  }

  @Test
  void shouldConfig() {
    assertAll(
        () -> assertNotNull(genevaServerConfig),
        () -> assertNotNull(genevaServerConfig.featureVisibilityResource()),
        () -> assertNotNull(genevaServerConfig.sessionRegistry()));
  }
}
