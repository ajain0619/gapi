package com.nexage.app.mapper.rule;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO.RuleTargetDTOBuilder;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RuleTargetDTOMapper {

  RuleTargetDTOMapper MAPPER = Mappers.getMapper(RuleTargetDTOMapper.class);

  @Mapping(target = "ruleTargetType", source = "targetType")
  @Mapping(target = "rule", ignore = true)
  RuleTarget map(RuleTargetDTO dto);

  default RuleTargetDTO map(
      RuleTarget entity, @Context RuleTargetDataConverter ruleTargetDataConverter) {
    if (entity == null) {
      return null;
    }

    RuleTargetDTOBuilder ruleTargetDTO = RuleTargetDTO.builder();

    ruleTargetDTO.targetType(entity.getRuleTargetType());
    ruleTargetDTO.pid(entity.getPid());
    ruleTargetDTO.version(entity.getVersion());
    ruleTargetDTO.status(entity.getStatus());
    ruleTargetDTO.matchType(entity.getMatchType());
    if (entity.getRuleTargetType() == RuleTargetType.BUYER_SEATS) {
      ruleTargetDTO.data(ruleTargetDataConverter.legacyTargetDataToTargetData(entity.getData()));
    } else {
      ruleTargetDTO.data(entity.getData());
    }

    return ruleTargetDTO.build();
  }

  @Mapping(target = "ruleTargetType", source = "targetType")
  @Mapping(target = "rule", ignore = true)
  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  void apply(RuleTargetDTO dto, @MappingTarget RuleTarget entity);
}
