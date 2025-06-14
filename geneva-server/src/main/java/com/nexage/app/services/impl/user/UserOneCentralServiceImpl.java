package com.nexage.app.services.impl.user;

import static java.util.Objects.isNull;

import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.manager.OneCentralUserManager;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.user.UserOneCentralService;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserOneCentralServiceImpl implements UserOneCentralService {

  private final OneCentralUserManager oneCentralUserManager;
  private final UserRepository userRepository;
  private final UserContext userContext;

  @Autowired
  public UserOneCentralServiceImpl(
      OneCentralUserManager oneCentralUserManager,
      UserRepository userRepository,
      UserContext userContext) {
    this.oneCentralUserManager = oneCentralUserManager;
    this.userRepository = userRepository;
    this.userContext = userContext;
  }

  /** {@inheritDoc} */
  @Override
  public User createUser(User user) {
    if (oneCentralUserManager.createUserEnabled()) {
      // After we call createOneCentralUser, the OneCentralUser returned
      // will either be a new OneCentralUser based on the User object
      // passed into createOneCentralUser or it will be populated by 1C
      // based on an existing 1C user found with the same email address
      OneCentralUserResponseDTO.OneCentralUser oneCentralUser =
          oneCentralUserManager.createOneCentralUser(user);
      user.setOneCentralUserName(oneCentralUser.getUsername());
      user.setName(oneCentralUser.getFirstName().concat(" ").concat(oneCentralUser.getLastName()));
    }
    User genevaUser = userRepository.save(user);
    if (isNull(genevaUser.getSellerSeat()) && user.isPrimaryContact()) {
      genevaUser.getCompany().setContact(user);
    }
    log.debug("A user in geneva is created with userName: {}", genevaUser.getUsername());
    return genevaUser;
  }

  /** {@inheritDoc} */
  @Override
  public User updateUser(User user) {
    if (user.getOneCentralUserName() != null) {
      if (!userRepository.existsByUserNameAndPid(user.getUserName(), user.getPid())) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_USER_NAME_CANNOT_BE_CHANGED);
      }
      if (!userContext.isInternalIdentityIqUser()) {
        oneCentralUserManager.updateOneCentralUser(user);
      }
    }
    return userRepository.save(user);
  }
}
