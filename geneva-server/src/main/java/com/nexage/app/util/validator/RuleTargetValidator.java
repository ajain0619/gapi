package com.nexage.app.util.validator;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RuleTargetValidator
    implements ConstraintValidator<RuleTargetConstraint, RuleTargetDTO> {

  private RuleTargetType[] ruleTargetTypes;
  private Set<RuleTargetValidation> ruleTargetValidation;

  public RuleTargetValidator(Set<RuleTargetValidation> ruleTargetValidation) {
    this.ruleTargetValidation = ruleTargetValidation;
  }

  @Override
  public boolean isValid(
      RuleTargetDTO ruleTargetDTO, ConstraintValidatorContext constraintValidatorContext) {

    if (Arrays.stream(ruleTargetTypes)
        .noneMatch(ruleTargetType -> ruleTargetType == ruleTargetDTO.getTargetType())) {
      return true;
    }

    if (ruleTargetValidation == null) {
      return false;
    }

    // Device_Type and Os(Device_os) dto validation by default, player size, Content Channel,
    // Content Livestream and Ad Format Type validation
    boolean result =
        ruleTargetValidation.stream()
            .filter(target -> target.getRuleTarget() == ruleTargetDTO.getTargetType())
            .findFirst()
            .map(target -> target.isValid(ruleTargetDTO.getData()))
            .orElse(false);

    if (!result) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(
              String.format("Rule Target %s has invalid value", ruleTargetDTO.getTargetType()))
          .addConstraintViolation();
    }
    return result;
  }

  @Override
  public void initialize(RuleTargetConstraint constraintAnnotation) {
    ruleTargetTypes = constraintAnnotation.allowedTargets();
  }
}
