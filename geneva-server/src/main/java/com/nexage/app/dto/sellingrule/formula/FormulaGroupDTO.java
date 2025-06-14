package com.nexage.app.dto.sellingrule.formula;

import com.nexage.app.util.validator.FormulaGroupConstraint;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FormulaGroupConstraint
public class FormulaGroupDTO {
  @NotEmpty @Valid private List<FormulaRuleDTO> formulaRules = new ArrayList<>();
}
