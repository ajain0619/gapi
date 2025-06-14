package com.nexage.app.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.nexage.admin.core.model.User;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {CompanyViewDTOMapper.class, SellerSeatDTOMapper.class})
public interface UserDTOMapper {

  Logger log = LogManager.getLogger(UserDTOMapper.class);

  UserDTOMapper MAPPER = Mappers.getMapper(UserDTOMapper.class);

  UserDTO map(User user);

  User map(UserDTO userDto);

  default void setUserDetails(UserDTO userDto, User user, UserContext userContext) {
    if (userContext.isCurrentUser(userDto.getPid())) {
      log.info("User managing his/her own account");
    } else {
      if (!userContext.writePrivilegeCheck(user)) {
        log.error(
            "Logged in user should have right affiliation or be Nexage manager to create user or update other user accounts");
        throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
      }
      user.setEnabled(userDto.isEnabled());
      user.setRole(userDto.getRole());
      user.determinePrimaryContact();
      if (isNull(user.getSellerSeat()) && userDto.isPrimaryContact() != user.isPrimaryContact()) {
        user.getCompany().setContact(userDto.isPrimaryContact() ? user : null);
      }
    }
    if (nonNull(userDto.getSellerSeat())) {
      user.setGlobal(userDto.isGlobal());
    } else {
      user.setGlobal(false);
    }
    user.setContactNumber(userDto.getContactNumber());
    user.setEmail(userDto.getEmail());
    user.setTitle(userDto.getTitle());
    user.setName(userDto.getName());
    user.setLastName(userDto.getLastName());
    user.setDealAdmin(userDto.isDealAdmin());
    user.setFirstName(userDto.getFirstName());
  }
}
