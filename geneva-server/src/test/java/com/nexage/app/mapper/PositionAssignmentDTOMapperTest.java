package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.app.dto.sellingrule.PositionAssignmentDTO;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PositionAssignmentDTOMapperTest {

  @Test
  void shouldDoNothingEntityToDTO() {
    RuleDeployedPosition source = null;
    PositionAssignmentDTO result = PositionAssignmentDTOMapper.MAPPER.map(source);
    assertNull(result);
  }

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();

    RuleDeployedPosition source = new RuleDeployedPosition();
    source.setPid(pid);
    source.setName(name);

    PositionAssignmentDTO result = PositionAssignmentDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getName(), source.getName());
  }
}
