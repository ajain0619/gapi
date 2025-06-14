package com.nexage.admin.dw.xstream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class ReportDefinitionAdapterTest {

  @Test
  void shouldCreateXmlReportDefinition() throws IOException {
    var reportDefinitionAdapter = new ReportDefinitionAdapter();
    var resource = new ClassPathResource("testreportdef/adserver.xml").getFile();
    var xml = new String(Files.readAllBytes(resource.toPath()));
    var xmlReportDefinition = reportDefinitionAdapter.getReportDefObject(xml);
    assertNotNull(xmlReportDefinition);
  }

  @Test
  void shouldCreateXStream() {
    var reportDefinitionAdapter = new ReportDefinitionAdapter();
    var xStream = reportDefinitionAdapter.getXStream();
    assertNotNull(xStream);
  }
}
