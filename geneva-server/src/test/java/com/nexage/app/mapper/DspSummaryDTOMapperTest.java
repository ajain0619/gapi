package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.Company;
import com.nexage.app.dto.dsp.DspSummaryDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DspSummaryDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    final String name = UUID.randomUUID().toString();
    Company source = new Company();
    source.setPid(pid);
    source.setId(id);
    source.setName(name);
    DspSummaryDTO result = DspSummaryDTOMapper.MAPPER.map(source);
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getId(), source.getId());
    assertEquals(result.getName(), source.getName());
  }
}
