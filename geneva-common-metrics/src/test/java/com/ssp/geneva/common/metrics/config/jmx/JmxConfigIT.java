package com.ssp.geneva.common.metrics.config.jmx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.management.MBeanServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JmxConfig.class})
@TestPropertySource(properties = {"ssp.geneva.jmx.default.domain=ssp.whatever"})
class JmxConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    AnnotationMBeanExporter mbeanExporter =
        (AnnotationMBeanExporter) context.getBean("mbeanExporter");
    assertNotNull(mbeanExporter);
    MetadataNamingStrategy metadataNamingStrategy =
        (MetadataNamingStrategy)
            ReflectionTestUtils.getField(mbeanExporter, "metadataNamingStrategy");
    assertNotNull(metadataNamingStrategy);
    Object defaultDomain = ReflectionTestUtils.getField(metadataNamingStrategy, "defaultDomain");
    assertNotNull(defaultDomain);
    assertEquals("ssp.whatever", defaultDomain);
    MBeanServer mBeanServer = mbeanExporter.getServer();
    assertNotNull(mBeanServer);
  }
}
