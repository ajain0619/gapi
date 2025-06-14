package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.InventoryAttributeValue;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import org.junit.jupiter.api.Test;

class InventoryAttributeValueDtoMapperTest {

  @Test
  void shouldMapEntityToDto() {
    InventoryAttributeValue value = new InventoryAttributeValue();
    value.setPid(100L);
    value.setName("value name");
    value.setVersion(2);
    value.setEnabled(true);
    value.setLastUpdate();

    InventoryAttributeValueDTO dto = InventoryAttributeValueDTOMapper.MAPPER.map(value);
    assertEquals(value.getPid(), dto.getPid());
    assertEquals(value.getName(), dto.getValue());
    assertEquals(value.getVersion(), dto.getVersion());
    assertEquals(value.isEnabled(), dto.isEnabled());
  }

  @Test
  void shouldMapDtoToEntity() {
    var dto = new InventoryAttributeValueDTO();
    dto.setPid(12L);
    dto.setValue("some value");
    dto.setEnabled(true);
    dto.setVersion(3);

    InventoryAttributeValue value = InventoryAttributeValueDTOMapper.MAPPER.map(dto);

    assertEquals(dto.getPid(), value.getPid());
    assertEquals(dto.getValue(), value.getName());
    assertEquals(dto.isEnabled(), value.isEnabled());
  }
}
