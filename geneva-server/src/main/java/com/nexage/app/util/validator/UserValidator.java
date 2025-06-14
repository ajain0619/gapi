package com.nexage.app.util.validator;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.nexage.admin.core.model.User.Role.ROLE_API;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_SMARTEX;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_YIELD;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.SellerLimitService;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.model.inventory.CompanyType;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserValidator extends BaseValidator<UserCreateConstraint, UserDTO> {

  private final SellerLimitService sellerLimitService;
  private final MessageHandler messageHandler;

  public UserValidator(SellerLimitService sellerLimitService, MessageHandler messageHandler) {
    this.sellerLimitService = sellerLimitService;
    this.messageHandler = messageHandler;
  }

  /**
   * Verify the current users privileges and also the state of the provided user object to ensure it
   * is creatable.
   *
   * @param user to be validated
   * @param context javax validation contextual data
   * @return boolean indicating whether supplied user is valid or not
   */
  @Override
  public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
    return validateUserCompaniesState(user, context)
        && validateSellerSeatLimit(user, context)
        && validatePrimaryContactEnabled(user, context)
        && validateRole(user, context);
  }

  boolean validateUserCompaniesState(UserDTO user, ConstraintValidatorContext context) {
    if (isNull(user.getSellerSeat())
        && (isNull(user.getCompanies()) || user.getCompanies().isEmpty())) {
      log.error("Cannot create a user with NULL for both company and sellerSeat");
      return addConstraintMessage(
          context, "companies", ServerErrorCodes.SERVER_CREATE_USER_MISSING_COMPANY_OR_SELLER_SEAT);
    }
    return true;
  }

  boolean validateSellerSeatLimit(UserDTO user, ConstraintValidatorContext context) {
    if (isNull(user.getSellerSeat())
        && user.getCompanies().stream()
            .anyMatch(
                company ->
                    (isNull(company.getPid())
                            || !sellerLimitService.canCreateUsers(company.getPid()))
                        && company.getType() != CompanyType.NEXAGE)) {
      return addConstraintMessage(context, "sellerSeat", ServerErrorCodes.SERVER_LIMIT_REACHED);
    }
    return true;
  }

  boolean validatePrimaryContactEnabled(UserDTO user, ConstraintValidatorContext context) {
    if (user.isPrimaryContact() && !user.isEnabled()) {
      return addConstraintMessage(
          context, "primaryContact", ServerErrorCodes.SERVER_PRIMARY_CONTACT_NOT_ENABLED);
    }
    return true;
  }

  boolean validateRole(UserDTO user, ConstraintValidatorContext context) {
    if (user.getRole() == ROLE_API && isNullOrEmpty(user.getContactEmail())) {
      return addConstraintMessage(context, "role", ServerErrorCodes.SERVER_INVALID_CONTACT_EMAIL);
    } else if ((user.getRole() == ROLE_MANAGER_YIELD || user.getRole() == ROLE_MANAGER_SMARTEX)
        && user.getCompanies().stream()
            .anyMatch(
                company ->
                    (nonNull(company.getPid())
                            && InternalCompany.NEXAGE.getPid() != company.getPid())
                        || (nonNull(company.getType())
                            && InternalCompany.NEXAGE.getType() != company.getType()))) {
      return addConstraintMessage(
          context, "role", ServerErrorCodes.SERVER_ROLE_COMPANY_TYPE_MISMATCH);
    }
    return true;
  }

  boolean addConstraintMessage(
      ConstraintValidatorContext context, String field, ErrorCode errorCode) {
    super.addConstraintMessage(context, field, messageHandler.getMessage(errorCode.toString()));
    return false;
  }
}
