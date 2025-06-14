package com.nexage.app.mapper.rule;

import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.RuleActionType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IntendedActionDTOMapper {

  IntendedActionDTOMapper MAPPER = Mappers.getMapper(IntendedActionDTOMapper.class);

  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "rule", ignore = true)
  RuleIntendedAction map(IntendedActionDTO dto);

  IntendedActionDTO map(RuleIntendedAction entity);

  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "rule", ignore = true)
  void apply(IntendedActionDTO dto, @MappingTarget RuleIntendedAction action);

  @AfterMapping
  default void translateDataFromDtoToEntity(
      IntendedActionDTO dto, @MappingTarget RuleIntendedAction action) {
    action.setActionData(dto.getActionType().translateDataFromDtoToEntity(dto.getActionData()));
  }

  @AfterMapping
  default void translateDataFromEntityToDto(
      @MappingTarget IntendedActionDTO.IntendedActionDTOBuilder builder,
      RuleIntendedAction action) {
    RuleActionType ruleActionType = RuleActionType.valueOf(action.getActionType().name());
    builder.actionData(ruleActionType.translateDataFromEntityToDto(action.getActionData()));
  }
}
