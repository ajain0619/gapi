package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InventoryAttributeDTOMapperTest {

  @Test
  void shouldMap() {
    final long pid = new Random().nextLong();
    final String name = UUID.randomUUID().toString();

    InventoryAttribute source = new InventoryAttribute();
    source.setPid(pid);
    source.setName(name);
    source.setAssignedLevel("1,2,3");
    source.setInventoryAttributeValueCount(1L);
    source.setInventoryAttributeValueCountActive(1L);

    InventoryAttributeDTO result = InventoryAttributeDTOMapper.MAPPER.map(source);

    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getAssignedLevel(), Set.of(1, 2, 3));
  }
}
