package com.nexage.app.util.validator.rule;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.FieldError;
import com.nexage.app.util.validator.BaseValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class RuleFormulaValidator extends BaseValidator<RuleFormulaConstraint, RuleFormulaDTO> {

  private static final String FORMULA_DATA_PATTERN = "^[\\x00-\\x7F]+";

  /**
   * Validates correctness of provided {@link RuleFormulaDTO}
   *
   * @param ruleFormula rule formula to be validated
   * @param context constraint validator context
   * @return true if formula is well-formed, false otherwise
   */
  @Override
  public boolean isValid(RuleFormulaDTO ruleFormula, ConstraintValidatorContext context) {
    if (isNull(ruleFormula)
        || isNull(ruleFormula.getPlacementFormula())
        || isEmpty(ruleFormula.getPlacementFormula().getFormulaGroups())
        || ruleFormula.getPlacementFormula().getFormulaGroups().stream()
            .anyMatch(g -> isEmpty(g.getFormulaRules()))) {
      return true;
    }
    PlacementFormulaDTO formula = ruleFormula.getPlacementFormula();
    List<FieldError> validationErrors = new ArrayList<>();

    formula
        .getFormulaGroups()
        .forEach(
            group -> {
              validateGroup(group, validationErrors);
              validateSingleAttributeAndOperator(group, validationErrors);
            });

    return validationErrors.isEmpty() || addConstraintMessage(context, validationErrors);
  }

  private void validateGroup(FormulaGroupDTO group, List<FieldError> validationErrors) {
    group
        .getFormulaRules()
        .forEach(
            rule -> {
              if (rule.isInventoryAttribute() && isNull(rule.getAttributePid())) {
                validationErrors.add(
                    new FieldError("attributePid", getAnnotation().messageNullAttributePid()));
                return;
              }

              Stream.of(rule.getRuleData().split(","))
                  .map(StringUtils::trimToNull)
                  .forEach(
                      data -> {
                        if (isNull(data)
                            || !data.matches(FORMULA_DATA_PATTERN)
                            || isNull(rule.getAttribute().getValueAsObject(data))) {
                          validationErrors.add(
                              new FieldError("ruleData", getAnnotation().message()));
                        }
                      });
            });
  }

  private void validateSingleAttributeAndOperator(
      FormulaGroupDTO group, List<FieldError> validationErrors) {
    Map<String, Long> counts =
        group.getFormulaRules().stream().collect(groupingBy(this::mapToKey, counting()));
    if (counts.values().stream().anyMatch((v -> v > 1))) {
      validationErrors.add(
          new FieldError("group", getAnnotation().messageMoreThanOneArgumentOccurrence()));
    }
  }

  private String mapToKey(FormulaRuleDTO rule) {
    return rule.getAttribute().name()
        + (rule.isInventoryAttribute() ? rule.getAttributePid() : "")
        + rule.getOperator().name();
  }
}
