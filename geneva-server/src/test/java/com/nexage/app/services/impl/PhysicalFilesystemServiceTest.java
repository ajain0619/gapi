package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PhysicalFilesystemServiceTest {

  private PhysicalFilesystemService filesystemService;

  @BeforeEach
  public void setUp() {
    filesystemService = new PhysicalFilesystemService();
  }

  @Test
  void shouldWriteFile() throws IOException {
    String fileName = "/1/2450/33/file.txt";
    String dir = "/tmp/";
    String body = "I'm a body";
    filesystemService.write(dir, fileName, body.getBytes());

    String content = new String(Files.readAllBytes(Paths.get(dir + fileName)));
    assertEquals(body, content);
  }

  @Test
  void shouldThrowAnException() throws IOException {
    assertThrows(GenevaAppRuntimeException.class, () -> filesystemService.write("", null, null));
  }
}
