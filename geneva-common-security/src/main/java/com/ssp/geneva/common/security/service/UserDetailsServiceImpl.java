package com.ssp.geneva.common.security.service;

import static com.ssp.geneva.common.model.inventory.CompanyType.BUYER;
import static com.ssp.geneva.common.model.inventory.CompanyType.NEXAGE;
import static com.ssp.geneva.common.model.inventory.CompanyType.SEATHOLDER;
import static com.ssp.geneva.common.model.inventory.CompanyType.SELLER;

import com.mysql.cj.util.StringUtils;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.util.TestUserUtil;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used by spring security and included in the session as extra information. So any
 * class level attributes will be serialized during session replication and should be declared
 * transients.
 */
@Transactional
@Log4j2
public class UserDetailsServiceImpl implements OneCentralUserDetailsService {

  private static final long serialVersionUID = 1L;
  private final transient UserRepository userRepository;
  private final transient TestUserUtil testUserUtil;
  private final boolean isGenevaServerTestingUserEnabled;

  public UserDetailsServiceImpl(
      UserRepository userRepository,
      TestUserUtil testUserUtil,
      boolean isGenevaServerTestingUserEnabled) {
    this.userRepository = userRepository;
    this.testUserUtil = testUserUtil;
    this.isGenevaServerTestingUserEnabled = isGenevaServerTestingUserEnabled;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    User user =
        userRepository
            .findByUserName(username)
            .orElseThrow(
                () -> {
                  log.error("Could not find username " + username);
                  return new UsernameNotFoundException(
                      "could not find geneva username: " + username);
                });
    if (!StringUtils.isNullOrEmpty(user.getOneCentralUserName())) {
      log.error("Login not supported for " + username);
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_LOGIN_NOT_SUPPORTED);
    }
    return loadUser(new UserAuth(user, List.of()));
  }

  /**
   * Gets details from database for users authenticated in One Central
   *
   * @param authenticationData user data fetched from One Central
   * @param bearerAuthentication true for bearer authentication, false otherwise
   * @return user data retrieved from database
   */
  @Override
  public UserDetails loadUserDetailsBy1CUsername(
      final Map<String, Object> authenticationData, boolean bearerAuthentication) {

    var user = loadUserBy1CUsername(authenticationData, bearerAuthentication);

    return loadUser(user);
  }

  @Override
  public UserAuth loadUserBy1CUsername(
      final Map<String, Object> authenticationData, boolean bearerAuthentication) {
    String username = (String) authenticationData.get("username");
    User user =
        isGenevaServerTestingUserEnabled && !bearerAuthentication
            ? testUserUtil.setupTestUser(authenticationData)
            : userRepository
                .findByOneCentralUserName(username)
                .orElseThrow(
                    () -> {
                      log.error("Could not find username " + username);
                      return new UsernameNotFoundException(
                          "could not find one central username: " + username);
                    });
    if (!bearerAuthentication && user.getRole() == Role.ROLE_API) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    if (!user.isEnabled()) {
      log.warn("One Central User is disabled. Login not allowed: " + username);
      throw new DisabledException("One central user {} is disabled: " + username);
    }
    var ocEntitlements =
        (ArrayList<LinkedHashMap<Object, Object>>) authenticationData.get("entitlements");
    List<Entitlement> entitlements = null;
    if (!Objects.isNull(ocEntitlements)) {
      entitlements =
          ocEntitlements.stream()
              .map(
                  e -> {
                    try {
                      var name = (String) e.get("name");
                      var id = (int) e.get("id");
                      var displayName = (String) e.get("displayName");
                      var application = (String) e.get("application");
                      var type = (String) e.get("type");
                      var permission = (String) e.get("permission");
                      return new Entitlement(id, name, displayName, application, type, permission);
                    } catch (Exception exception) {
                      log.warn("Error when reading entitlement: " + exception.getMessage());
                      return null;
                    }
                  })
              .filter(
                  e ->
                      Objects.nonNull(e)
                          && Objects.nonNull(e.getApplication())
                          && "OneMobile".equals(e.getApplication()))
              .collect(Collectors.toList());
    }
    return new UserAuth(user, entitlements);
  }

  public UserDetails loadUser(UserAuth userAuth) {
    if (List.of(SEATHOLDER, NEXAGE, SELLER, BUYER).contains(userAuth.getUser().getCompanyType())) {
      return new SpringUserDetails(userAuth);
    }
    throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
  }
  // should be not final

  @Override
  public void updateUserMigrated(Long userPid) {
    User userMod =
        userRepository
            .findById(userPid)
            .orElseThrow(
                () -> new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));
    userMod.setMigratedOneCentral(true);
    userRepository.save(userMod);
  }
}
