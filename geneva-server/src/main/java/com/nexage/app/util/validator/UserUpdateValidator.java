package com.nexage.app.util.validator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.User;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.error.model.ErrorCode;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserUpdateValidator extends BaseValidator<UserUpdateConstraint, UserDTO> {

  private final UserContext userContext;
  private final MessageHandler messageHandler;

  public UserUpdateValidator(UserContext userContext, MessageHandler messageHandler) {
    this.messageHandler = messageHandler;
    this.userContext = userContext;
  }

  /**
   * Verify the current users privileges and also the state of the provided user object to ensure it
   * is updatable.
   *
   * @param userDto to be validated
   * @param constraintValidatorContext javax validation contextual data
   * @return boolean indicating whether supplied user is valid or not
   */
  @Override
  public boolean isValid(UserDTO userDto, ConstraintValidatorContext constraintValidatorContext) {
    return validateRole(userDto, constraintValidatorContext)
        && validateUserAsSellerSeat(userDto, constraintValidatorContext);
  }

  /**
   * Validates if the current user has permissions to update the user.
   *
   * @param userDto to be validated
   * @param user user details form database
   */
  public void validateEmail(UserDTO userDto, User user) {
    if (userDto.isDealAdmin() != user.isDealAdmin() && !userContext.isNexageAdmin()) {
      log.info(
          " user {} is not allowed to update deal management permission for user {}.",
          userContext.getPid(),
          userDto.getPid());
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
    if (!(user.getEmail().equals(userDto.getEmail())
        || userContext.isCurrentUser(userDto.getPid())
        || userContext.isNexageAdmin()
        || userContext.isOcApiIIQ())) {
      // MX-1793 - user can only change own email address
      log.info(
          "user {} not allowed to change email address for user {}",
          userContext.getPid(),
          userDto.getPid());
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  /**
   * Validates if the user pid from path variable is same as the pid in userdto.
   *
   * @param userDto to be validated
   * @param userPid pid of the user
   */
  public void validatePid(UserDTO userDto, Long userPid) {
    if (!userPid.equals(userDto.getPid())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_PIDS_MISMATCH);
    }
  }

  boolean validateRole(UserDTO userDto, ConstraintValidatorContext context) {
    if ((userDto.getRole() == User.Role.ROLE_MANAGER_YIELD
            || userDto.getRole() == User.Role.ROLE_MANAGER_SMARTEX)
        && userDto.getCompanies().stream()
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

  boolean validateUserAsSellerSeat(UserDTO userDto, ConstraintValidatorContext context) {
    boolean isSellerSeatIncluded = nonNull(userDto.getSellerSeat());

    if (!isSellerSeatIncluded && userDto.isGlobal()) {
      return addConstraintMessage(
          context, "sellerSeat", ServerErrorCodes.SERVER_ONLY_SELLER_SEAT_USER_CAN_BE_GLOBAL);
    }
    if (isSellerSeatIncluded) {
      Long sellerSeatPid = userDto.getSellerSeat().getPid();
      if (isNull(sellerSeatPid)
          || !userContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeatPid)) {
        log.error(
            "Logged in user should be assigned to the same seller seat as updated user or be Nexage user");
        return addConstraintMessage(
            context, "sellerSeat", SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
    }
    return true;
  }

  boolean addConstraintMessage(
      ConstraintValidatorContext context, String field, ErrorCode errorCode) {
    super.addConstraintMessage(context, field, messageHandler.getMessage(errorCode.toString()));
    return false;
  }
}
