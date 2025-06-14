package com.nexage.app.mapper;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.PlacementFormulaPredicateBuilder;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = FormulaGroupDTOMapper.class)
public interface PlacementFormulaDTOMapper {
  PlacementFormulaDTOMapper MAPPER = Mappers.getMapper(PlacementFormulaDTOMapper.class);

  default Group<RuleFormulaPositionView> map(PlacementFormulaDTO dto) {
    PlacementFormulaPredicateBuilder builder =
        PlacementFormulaPredicateBuilder.betweenGroups(dto.getGroupedBy().getGroupOperator())
            .betweenGroupItems(GroupOperator.AND);

    dto.getFormulaGroups().stream()
        .map(formulaGroupDTO -> FormulaGroupDTOMapper.MAPPER.map(formulaGroupDTO))
        .forEach(builder::addGroup);

    return builder.build();
  }
}
