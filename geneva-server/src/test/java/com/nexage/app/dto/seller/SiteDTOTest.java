package com.nexage.app.dto.seller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SiteDTOTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void shouldSerializeWithMisspelledPid() throws Exception {
    // given
    SiteDTO dto = new SiteDTO();
    dto.setPid(123L);

    // when
    String serialized = mapper.writeValueAsString(dto);

    // then
    assertTrue(serialized.contains("\"pid\":123"));
    assertTrue(serialized.contains("\"pId\":123"));
  }

  @Test
  void shouldSerializePidWhenSetUsingMisspelledSetter() throws Exception {
    // given
    SiteDTO dto = new SiteDTO();
    dto.setMisspelledPid(123L);

    // when
    String serialized = mapper.writeValueAsString(dto);

    // then
    assertTrue(serialized.contains("\"pid\":123"));
    assertTrue(serialized.contains("\"pId\":123"));
  }

  @ParameterizedTest
  @CsvSource(
      value = {"{\"pid\":123};123", "{\"pId\":123};123", "{\"pid\":123,\"pId\":456};456"},
      delimiter = ';')
  void shouldDeserializePidsRegardlessOfSpelling(String source, Long expectedPid) throws Exception {
    // when
    SiteDTO deserialized = mapper.readValue(source, SiteDTO.class);

    // then
    assertEquals(expectedPid, deserialized.getPid());
    assertEquals(expectedPid, deserialized.getMisspelledPid());
  }
}
