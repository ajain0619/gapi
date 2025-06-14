package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("default")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FileSystemServiceConfig.class})
@TestPropertySource(
    properties = {
      "ssp.geneva.data.bucket=local-bucket",
      "aws.localstack.access.key=test",
      "aws.localstack.access.secret.key=test",
      "aws.localstack.region=us-east-1"
    })
class FileSystemServiceConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    FileSystemServiceConfig config =
        (FileSystemServiceConfig) context.getBean("fileSystemServiceConfig");
    assertNotNull(config);

    var genevaDataBucket = ReflectionTestUtils.getField(config, "genevaDataBucket");
    assertNotNull(genevaDataBucket);
    assertEquals("local-bucket", genevaDataBucket);

    var accessKey = ReflectionTestUtils.getField(config, "accessKey");
    assertNotNull(accessKey);
    assertEquals("test", accessKey);

    var secretAccessKey = ReflectionTestUtils.getField(config, "secretAccessKey");
    assertNotNull(secretAccessKey);
    assertEquals("test", secretAccessKey);

    var region = ReflectionTestUtils.getField(config, "region");
    assertNotNull(region);
    assertEquals("us-east-1", region);

    var endpointUrl = ReflectionTestUtils.getField(config, "endpointUrl");
    assertNotNull(endpointUrl);
    assertEquals("", endpointUrl);
  }
}
