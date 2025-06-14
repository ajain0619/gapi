package com.nexage.app.mapper;

import com.nexage.admin.core.model.InventoryAttributeValue;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeValueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryAttributeValueDTOMapper {

  InventoryAttributeValueDTOMapper MAPPER =
      Mappers.getMapper(InventoryAttributeValueDTOMapper.class);

  @Mapping(source = "name", target = "value")
  InventoryAttributeValueDTO map(InventoryAttributeValue inventoryAttributeValue);

  @Mapping(source = "value", target = "name")
  InventoryAttributeValue map(InventoryAttributeValueDTO inventoryAttributeValueDto);
}
