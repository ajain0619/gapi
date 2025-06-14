package com.nexage.app.dto.sellingrule.formula;

import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FormulaGroupingDTO {
  OR(GroupOperator.OR),
  AND(GroupOperator.AND);

  @Getter private GroupOperator groupOperator;
}
