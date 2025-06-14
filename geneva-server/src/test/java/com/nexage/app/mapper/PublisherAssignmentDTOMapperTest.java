package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PublisherAssignmentDTOMapperTest {

  @Test
  void shouldDoNothingEntityToDTO() {
    RuleDeployedCompany source = null;
    PublisherAssignmentDTO result = PublisherAssignmentDTOMapper.MAPPER.map(source);
    assertNull(result);
  }

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();

    RuleDeployedCompany source = new RuleDeployedCompany();
    source.setPid(pid);
    source.setName(name);

    PublisherAssignmentDTO result = PublisherAssignmentDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
  }
}
