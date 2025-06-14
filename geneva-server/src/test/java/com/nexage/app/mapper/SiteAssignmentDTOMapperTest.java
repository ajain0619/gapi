package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.app.dto.sellingrule.SiteAssignmentDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SiteAssignmentDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();

    RuleDeployedSite source = new RuleDeployedSite();
    source.setPid(pid);
    source.setName(name);

    SiteAssignmentDTO result = SiteAssignmentDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
  }
}
