package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.s3.model.S3Object;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled(
    "These tests need docker to work which I'm not sure if the legacy SD job has it installed")
class AWSFilesystemServiceTest {

  private AWSFilesystemService filesystemService;
  private String bucketName = "localstack-bucket";

  @BeforeEach
  void setUp() {
    filesystemService =
        new AWSFilesystemService(
            bucketName, "test", "test", "us-east-1", "http://localstack:4566/");
    filesystemService.getS3().createBucket(bucketName);
  }

  @Test
  void shouldWriteFile() {
    String fileName = "1/2450/33/file.txt";
    String body = "I'm a body";
    filesystemService.write("", fileName, body.getBytes());

    S3Object s3Object = filesystemService.getS3().getObject(bucketName, fileName);
    assertEquals(fileName, s3Object.getKey());
    assertEquals("text/plain", s3Object.getObjectMetadata().getContentType());
  }

  @Test
  void shouldThrowAnException() {
    assertThrows(GenevaAppRuntimeException.class, () -> filesystemService.write("", null, null));
  }
}
