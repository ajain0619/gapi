package com.nexage.app.mapper.rule;

import com.nexage.admin.core.model.RuleTarget;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RuleTargetMapper {

  RuleTargetMapper MAPPER = Mappers.getMapper(RuleTargetMapper.class);

  @Mapping(target = "ruleTargetType", source = "targetType")
  @Mapping(target = "rule", ignore = true)
  RuleTarget map(RuleTargetDTO dto);

  @InheritInverseConfiguration(name = "map")
  RuleTargetDTO map(RuleTarget entity);

  @InheritConfiguration
  void apply(@MappingTarget RuleTarget entity, RuleTargetDTO dto);
}
