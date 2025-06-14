package com.nexage.app.services.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.TreeMap;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

@ExtendWith(MockitoExtension.class)
class CsvServiceImplTest {
  @InjectMocks private CsvServiceImpl csvService;

  @Test
  void shouldReturnInputStreamResourceWhenValidTreeMapIsPassed() throws IOException {
    TreeMap<String, String> treeMap = new TreeMap<>();
    treeMap.put("domain1.com", "PENDING");
    treeMap.put("domain2.com", "APPROVED");

    InputStreamResource returnedInputStreamResource = csvService.create(treeMap);

    String stringOfReturnedInputStreamResource =
        IOUtils.toString(returnedInputStreamResource.getInputStream(), UTF_8);

    String[] splitStringOfReturnedInputStreamResource =
        stringOfReturnedInputStreamResource.split("\r\n");

    assertEquals(
        treeMap.get("domain1.com"), splitStringOfReturnedInputStreamResource[0].split(",")[1]);
    assertEquals(
        treeMap.get("domain2.com"), splitStringOfReturnedInputStreamResource[1].split(",")[1]);
  }
}
