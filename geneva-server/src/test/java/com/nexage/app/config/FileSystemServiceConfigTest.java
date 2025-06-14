package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FileSystemServiceConfigTest {

  private FileSystemServiceConfig fileSystemServiceConfig = new FileSystemServiceConfig();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(fileSystemServiceConfig, "genevaDataBucket", "dataBucket");
    ReflectionTestUtils.setField(fileSystemServiceConfig, "accessKey", "localAccessKey");
    ReflectionTestUtils.setField(fileSystemServiceConfig, "secretAccessKey", "localSecretKey");
    ReflectionTestUtils.setField(fileSystemServiceConfig, "region", "localRegion");
    ReflectionTestUtils.setField(fileSystemServiceConfig, "endpointUrl", "localEndpointUrl");
  }

  @Test
  void shouldCreateNonNullPhysicalFileSystemService() {
    assertNotNull(fileSystemServiceConfig.physicalFilesystemService());
  }

  @Test
  void shouldCreateNonNullAwsFileSystemService() {
    assertNotNull(fileSystemServiceConfig.awsGenevaDataFileSystemService());
  }
}
