package com.nexage.app.util.validator;

import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import java.util.HashMap;
import java.util.function.BiConsumer;
import javax.validation.ConstraintValidatorContext;

public class FormulaGroupValidator extends BaseValidator<FormulaGroupConstraint, FormulaGroupDTO> {

  @Override
  public boolean isValid(FormulaGroupDTO formulaDto, ConstraintValidatorContext context) {
    if (formulaDto.getFormulaRules() == null) {
      return false;
    }

    if (formulaDto.getFormulaRules().stream()
        .collect(
            HashMap::new,
            (sink, rule) -> {
              if (rule.getOperator() != null && rule.getAttribute() != null) {
                final String key =
                    rule.getAttribute().name()
                        + (FormulaAttributeDTO.INVENTORY_ATTRIBUTE.equals(rule.getAttribute())
                            ? rule.getAttributePid()
                            : "")
                        + rule.getOperator().name();
                if (!sink.containsKey(key)) {
                  sink.put(key, 0);
                }
                sink.put(key, sink.get(key) + 1);
              }
            },
            (BiConsumer<HashMap<String, Integer>, HashMap<String, Integer>>) HashMap::putAll)
        .values()
        .stream()
        .anyMatch(v -> v > 1)) {
      ValidationUtils.addConstraintMessage(
          context,
          "attribute",
          ValidationMessages.PLACEMENT_FORMULA_MORE_THEN_ONE_ATTRIBUTE_OCCURRENCE_IN_GROUP);
      return false;
    }
    return true;
  }
}
