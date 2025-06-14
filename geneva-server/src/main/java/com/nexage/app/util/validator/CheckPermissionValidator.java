package com.nexage.app.util.validator;

import com.nexage.admin.core.model.User.Role;
import com.nexage.app.security.UserContext;
import java.util.Arrays;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckPermissionValidator extends BaseValidator<CheckPermissionConstraint, Object> {

  @Autowired private UserContext userContext;

  /**
   * Validates specified User's permissions to check if {@link Object} can be set.
   *
   * <p>When checkIfNexageUser = true, user will be validated if Nexage User
   *
   * <p>When roles() not empty, each role will be checked if user has it.
   *
   * <p>All values will be OR'd so a user only has to have one of the above (isNexageUser or
   * hasRole(roles()) specified to set the value.
   *
   * @param value {@link Object} value to be checked if has permission to be set
   * @param context {@link ConstraintValidatorContext}
   * @return boolean
   */
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    boolean result = false;
    if (getAnnotation().checkIfNexageUser()) {
      result = userContext.isNexageUser();
    }

    result =
        Arrays.stream(getAnnotation().roles())
            .map(this::validateRole)
            .reduce(result, (bool1, bool2) -> bool1 || bool2);

    if (result) {
      return true;
    }

    if (Objects.nonNull(value)) {
      context
          .buildConstraintViolationWithTemplate(getAnnotation().message())
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean validateRole(Role role) {
    return userContext.hasRole(role);
  }
}
