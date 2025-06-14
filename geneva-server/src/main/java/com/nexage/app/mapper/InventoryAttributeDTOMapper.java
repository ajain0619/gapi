package com.nexage.app.mapper;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.nexage.admin.core.model.InventoryAttribute;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO;
import com.nexage.app.dto.inventory.attributes.InventoryAttributeDTO.InventoryAttributeDTOBuilder;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryAttributeDTOMapper {

  InventoryAttributeDTOMapper MAPPER = Mappers.getMapper(InventoryAttributeDTOMapper.class);

  default InventoryAttributeDTO map(InventoryAttribute source) {
    if (isEmpty(source)) {
      return new InventoryAttributeDTO();
    }
    InventoryAttributeDTOBuilder builder = InventoryAttributeDTO.builder();
    builder
        .name(source.getName())
        .pid(source.getPid())
        .sellerPid(source.getCompanyPid())
        .createdBy(source.getCompanyPid())
        .status(source.getStatus())
        .prefix(source.getPrefix())
        .isRequired(source.isRequired())
        .hasGlobalVisibility(source.isHasGlobalVisibility())
        .isInternalOnly(source.isInternal())
        .lastUpdated(source.getLastUpdate())
        .inventoryAttributeValueCount(source.getInventoryAttributeValueCount())
        .inventoryAttributeValueCountActive(source.getInventoryAttributeValueCountActive())
        .version(source.getVersion());
    if (StringUtils.isNotBlank(source.getAssignedLevel())) {
      Set<Integer> assignLevel =
          Stream.of(source.getAssignedLevel().split(","))
              .map(Integer::parseInt)
              .collect(Collectors.toSet());
      builder.assignedLevel(assignLevel);
    }
    return builder.build();
  }

  default InventoryAttribute mapToEntity(InventoryAttributeDTO source) {
    var target = new InventoryAttribute();
    if (isEmpty(source)) {
      return target;
    }
    target.setAssignedLevel(StringUtils.join(source.getAssignedLevel(), ','));
    target.setDescription(source.getDescription());
    target.setHasGlobalVisibility(source.isHasGlobalVisibility());
    target.setInternal(source.isInternalOnly());
    target.setName(source.getName());
    target.setPid(source.getPid());
    target.setPrefix(source.getPrefix());
    target.setRequired(source.isRequired());
    target.setStatus(source.getStatus());
    target.setCompanyPid(source.getCreatedBy());
    return target;
  }
}
