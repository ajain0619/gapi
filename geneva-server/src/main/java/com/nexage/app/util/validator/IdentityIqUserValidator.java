package com.nexage.app.util.validator;

import static java.util.Objects.isNull;

import com.nexage.admin.core.model.User.Role;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Log4j2
@Component
public class IdentityIqUserValidator extends BaseValidator<IdentityIqUserConstraint, UserDTO> {

  private final UserContext userContext;
  private final MessageHandler messageHandler;

  public IdentityIqUserValidator(UserContext userContext, MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
    this.userContext = userContext;
  }

  /**
   * Verify the current user input for IdentityIQ system user role.
   *
   * @param userDto to be validated
   * @param constraintValidatorContext javax validation contextual data
   * @return boolean indicating whether the scenario is valid or not
   */
  @Override
  public boolean isValid(UserDTO userDto, ConstraintValidatorContext constraintValidatorContext) {
    if (userContext.isOcApiIIQ()) {
      boolean isValid = validateCompanies(userDto, constraintValidatorContext);
      if (isValid) {
        isValid = validateUserRole(userDto, constraintValidatorContext);
      }
      return isValid;
    }
    return true;
  }

  boolean validateCompanies(UserDTO userDto, ConstraintValidatorContext context) {
    if (!CollectionUtils.isEmpty(userDto.getCompanies())
        && userDto.getCompanies().stream()
            .anyMatch(
                company ->
                    isNull(company.getPid())
                        || company.getPid() != InternalCompany.NEXAGE.getPid())) {
      addConstraintMessage(
          context,
          "companies",
          ServerErrorCodes.SERVER_IDENTITYIQ_USER_VALIDATION_NOT_INTERNAL_COMPANY);
      return false;
    }
    return true;
  }

  boolean validateUserRole(UserDTO userDto, ConstraintValidatorContext context) {
    if (isNull(userDto.getRole()) || userDto.getRole().equals(Role.ROLE_API)) {
      addConstraintMessage(
          context, "role", ServerErrorCodes.SERVER_IDENTITYIQ_USER_VALIDATION_INVALID_USER_ROLE);
      return false;
    }
    return true;
  }

  void addConstraintMessage(ConstraintValidatorContext context, String field, ErrorCode errorCode) {
    super.addConstraintMessage(context, field, messageHandler.getMessage(errorCode.toString()));
  }
}
