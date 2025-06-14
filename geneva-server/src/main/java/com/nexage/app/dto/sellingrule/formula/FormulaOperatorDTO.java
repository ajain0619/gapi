package com.nexage.app.dto.sellingrule.formula;

import com.nexage.admin.core.model.placementformula.formula.impl.Operator;
import lombok.Getter;

public enum FormulaOperatorDTO {
  EQUALS(Operator.EQUALS),
  NOT_EQUALS(Operator.NOT_EQUALS),
  CONTAINS(Operator.CONTAINS),
  NOT_CONTAINS(Operator.NOT_CONTAINS),
  MEMBER_OF(Operator.MEMBER_OF),
  NOT_MEMBER_OF(Operator.NOT_MEMBER_OF);

  @Getter private Operator operator;

  FormulaOperatorDTO(Operator operator) {
    this.operator = operator;
  }
}
