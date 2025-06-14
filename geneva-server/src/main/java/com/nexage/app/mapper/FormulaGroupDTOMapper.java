package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = FormulaRuleDTOMapper.class)
public interface FormulaGroupDTOMapper {

  FormulaGroupDTOMapper MAPPER = Mappers.getMapper(FormulaGroupDTOMapper.class);

  default List<PredicateBuilder<RuleFormulaPositionView>> map(FormulaGroupDTO formulaGroupDTO) {
    List<PredicateBuilder<RuleFormulaPositionView>> predicateBuilders = new ArrayList<>();
    formulaGroupDTO.getFormulaRules().stream()
        .map(formulaRuleDTO -> FormulaRuleDTOMapper.MAPPER.map(formulaRuleDTO))
        .forEach(predicateBuilders::add);
    return predicateBuilders;
  }
}
