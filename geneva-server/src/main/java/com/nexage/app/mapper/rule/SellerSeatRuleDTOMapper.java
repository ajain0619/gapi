package com.nexage.app.mapper.rule;

import com.nexage.admin.core.model.SellerSeatRule;
import com.nexage.app.dto.sellingrule.SellerSeatRuleDTO;
import org.mapstruct.Context;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {IntendedActionDTOMapper.class, RuleTargetDTOMapper.class})
public interface SellerSeatRuleDTOMapper extends RuleDTOMapper {

  SellerSeatRuleDTOMapper MAPPER = Mappers.getMapper(SellerSeatRuleDTOMapper.class);

  @InheritInverseConfiguration
  @Mapping(target = "pid", ignore = true)
  @Mapping(target = "version", ignore = true)
  SellerSeatRule map(SellerSeatRuleDTO dto);

  @Mapping(source = "ruleIntendedActions", target = "intendedActions")
  @Mapping(source = "ruleTargets", target = "targets")
  @Mapping(source = "ruleType", target = "type")
  SellerSeatRuleDTO map(
      SellerSeatRule entity, @Context RuleTargetDataConverter ruleTargetDataConverter);

  @InheritInverseConfiguration
  @Mapping(target = "ruleIntendedActions", source = "intendedActions")
  @Mapping(target = "ruleTargets", source = "targets")
  SellerSeatRule update(SellerSeatRuleDTO dto);
}
