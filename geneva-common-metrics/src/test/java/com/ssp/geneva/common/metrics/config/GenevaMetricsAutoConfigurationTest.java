package com.ssp.geneva.common.metrics.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ssp.geneva.common.metrics.config.jmx.JmxConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class GenevaMetricsAutoConfigurationTest {

  private AnnotationConfigApplicationContext applicationContext;

  @BeforeEach
  public void before() {
    applicationContext = new AnnotationConfigApplicationContext();
  }

  @AfterEach
  public void after() {
    if (applicationContext != null) {
      applicationContext.close();
    }
  }

  @Test
  void shouldAutoconfigure() {
    applicationContext.register(GenevaMetricsAutoConfiguration.class);
    applicationContext.refresh();
    assertEquals(1, applicationContext.getBeanNamesForType(JmxConfig.class).length);
  }
}
