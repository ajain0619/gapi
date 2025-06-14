package com.nexage.app.dto.sellingrule.formula;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nexage.app.util.validator.FormulaRuleConstraint;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FormulaRuleConstraint
public class FormulaRuleDTO {
  @NotNull private FormulaAttributeDTO attribute;
  @NotNull private FormulaOperatorDTO operator;
  @NotBlank private String ruleData;
  private Long attributePid;

  @JsonIgnore
  public boolean isInventoryAttribute() {
    return attribute == FormulaAttributeDTO.INVENTORY_ATTRIBUTE;
  }

  @JsonIgnore
  public boolean isDomainAppAttribute() {
    return (FormulaAttributeDTO.DOMAIN == attribute
        || FormulaAttributeDTO.APP_BUNDLE == attribute
        || FormulaAttributeDTO.APP_ALIAS == attribute);
  }
}
