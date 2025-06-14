package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.InventoryAttribute;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FormulaRuleDTOMapper {

  FormulaRuleDTOMapper MAPPER = Mappers.getMapper(FormulaRuleDTOMapper.class);

  @Mappings({
    @Mapping(source = "attributePid", target = "pid"),
    @Mapping(source = "operator.operator", target = "operator"),
    @Mapping(source = "ruleData", target = "value"),
  })
  InventoryAttribute mapToInventoryAttribute(FormulaRuleDTO dto);

  @Mappings({
    @Mapping(source = "operator.operator", target = "operator"),
    @Mapping(source = "ruleData", target = "value"),
    @Mapping(source = "attribute.attributeInfo", target = "attribute")
  })
  SimpleAttribute mapToSimpleAttribute(FormulaRuleDTO dto);

  default PredicateBuilder<RuleFormulaPositionView> map(FormulaRuleDTO formulaRuleDTO) {
    if (formulaRuleDTO.isInventoryAttribute()) {
      return mapToInventoryAttribute(formulaRuleDTO);
    } else if (formulaRuleDTO.isDomainAppAttribute()) {
      return null;
    } else {
      return mapToSimpleAttribute(formulaRuleDTO);
    }
  }
}
