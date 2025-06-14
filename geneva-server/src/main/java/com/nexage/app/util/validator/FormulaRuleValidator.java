package com.nexage.app.util.validator;

import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class FormulaRuleValidator extends BaseValidator<FormulaRuleConstraint, FormulaRuleDTO> {
  private static final String FORMULA_DATA_PATTERN = "^[\\x00-\\x7F]+";

  @Override
  public boolean isValid(FormulaRuleDTO formulaDto, ConstraintValidatorContext context) {
    if (formulaDto.getAttribute() == null || formulaDto.getOperator() == null) {
      return false;
    }

    if (FormulaAttributeDTO.INVENTORY_ATTRIBUTE.equals(formulaDto.getAttribute())
        && formulaDto.getAttributePid() == null) {
      return false;
    }

    AtomicBoolean result = new AtomicBoolean(true);
    Stream.of(formulaDto.getRuleData().split(","))
        .map(String::trim)
        .forEach(
            data -> {
              if (StringUtils.trimToNull(data) == null) {
                ValidationUtils.addConstraintMessage(
                    context, "ruleData", ValidationMessages.PLACEMENT_FORMULA_BAD_DATA);
                result.set(false);
                return;
              }

              if (formulaDto.getAttribute() != null
                  && formulaDto.getAttribute().getValueAsObject(data) == null) {
                ValidationUtils.addConstraintMessage(
                    context, "ruleData", ValidationMessages.PLACEMENT_FORMULA_BAD_DATA);
                result.set(false);
                return;
              }

              if (!data.matches(FORMULA_DATA_PATTERN)) {
                ValidationUtils.addConstraintMessage(
                    context, "ruleData", ValidationMessages.PLACEMENT_FORMULA_BAD_DATA);
                result.set(false);
                return;
              }
            });

    return result.get();
  }
}
