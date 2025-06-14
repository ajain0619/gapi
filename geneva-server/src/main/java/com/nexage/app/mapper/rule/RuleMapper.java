package com.nexage.app.mapper.rule;

import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
    uses = {
      PositionAssignmentMapper.class,
      SiteAssignmentMapper.class,
      IntendedActionMapper.class,
      RuleTargetMapper.class,
      RuleFormulaMapper.class
    },
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RuleMapper {

  RuleMapper MAPPER = Mappers.getMapper(RuleMapper.class);

  @InheritInverseConfiguration
  CompanyRule map(SellerRuleDTO dto);

  @Mapping(source = "ruleIntendedActions", target = "intendedActions")
  @Mapping(source = "ruleTargets", target = "targets")
  @Mapping(source = "ruleType", target = "type")
  @Mapping(source = "deployedCompanies", target = "assignments.publishers")
  @Mapping(source = "deployedSites", target = "assignments.sites")
  @Mapping(source = "deployedPositions", target = "assignments.positions")
  SellerRuleDTO map(CompanyRule entity);

  @InheritConfiguration
  CompanyRule apply(@MappingTarget CompanyRule entity, SellerRuleDTO dto);

  @AfterMapping
  default void mapBackReferences(@MappingTarget CompanyRule entity, SellerRuleDTO dto) {
    if (nonNull(entity.getRuleFormula())) {
      entity.getRuleFormula().setRule(entity);
    }
    if (nonNull(entity.getRuleTargets())) {
      entity.getRuleTargets().forEach(target -> target.setRule(entity));
    }
    if (nonNull(entity.getRuleIntendedActions())) {
      entity.getRuleIntendedActions().forEach(action -> action.setRule(entity));
    }
  }
}
