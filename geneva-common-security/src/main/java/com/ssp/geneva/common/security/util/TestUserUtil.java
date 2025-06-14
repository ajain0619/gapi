package com.ssp.geneva.common.security.util;

import com.google.common.base.Strings;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/* A utility class that sets up the state (create/update user as an internal user with admin privileges) of a geneva
 * user in the lower environments (UAT, PERF, QA and DEV) for testing purposes
 * */
@Transactional
public class TestUserUtil {

  // The following are the keys for the authentication data stored in the Map authenticationData
  static final String EMAIL = "email";
  static final String FIRST_NAME = "given_name";
  static final String LAST_NAME = "family_name";
  static final String SSO_LOGIN_USERNAME = "username";

  @SuppressWarnings("squid:S2068")
  private static final String PASSWORD =
      "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";

  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;

  public TestUserUtil(UserRepository userRepository, CompanyRepository companyRepository) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
  }

  public User setupTestUser(final Map<String, Object> authenticationData) {
    if (CollectionUtils.isEmpty(authenticationData)
        || Strings.isNullOrEmpty((String) authenticationData.get(EMAIL))) {
      throw new GenevaValidationException(CommonErrorCodes.COMMON_INSUFFICIENT_AUTHENTICATION_DATA);
    }
    final String email = getAuthenticationData(authenticationData, EMAIL);
    Optional<User> userOptional = userRepository.findByEmail(email);
    if (userOptional.isPresent()) {
      return updateTestUser(authenticationData, userOptional.get());
    } else {
      return createTestUser(authenticationData);
    }
  }

  private User createTestUser(final Map<String, Object> authenticationData) {
    User user = setUp(authenticationData, new User());
    return userRepository.save(user);
  }

  private User updateTestUser(
      final Map<String, Object> authenticationData, final User genevaTestUser) {
    User user = setUpForUpdate(authenticationData, genevaTestUser);
    return userRepository.save(user);
  }

  private User setUpForUpdate(Map<String, Object> authenticationData, User genevaTestUser) {
    setUpNamePassword(authenticationData, genevaTestUser);
    return genevaTestUser;
  }

  private User setUp(final Map<String, Object> authenticationData, final User genevaTestUser) {
    setUpNamePassword(authenticationData, genevaTestUser);
    genevaTestUser.setEnabled(true);
    genevaTestUser.setRole(User.Role.ROLE_ADMIN);
    Company company = companyRepository.findById(1L).orElse(null);
    genevaTestUser.addCompany(company);
    return genevaTestUser;
  }

  private void setUpNamePassword(Map<String, Object> authenticationData, User genevaTestUser) {
    final String email = getAuthenticationData(authenticationData, EMAIL);
    final String firstName = getAuthenticationData(authenticationData, FIRST_NAME);
    final String lastName = getAuthenticationData(authenticationData, LAST_NAME);
    final String loginUsername = getAuthenticationData(authenticationData, SSO_LOGIN_USERNAME);
    genevaTestUser.setEmail(email);
    genevaTestUser.setName(getFullName(firstName, lastName));
    genevaTestUser.setUserName(email); // This is the user's 1M username.
    genevaTestUser.setOneCentralUserName(loginUsername);
    genevaTestUser.setFirstName(firstName);
    genevaTestUser.setLastName(lastName);
  }

  private static String getAuthenticationData(
      final Map<String, Object> authenticationData, final String key) {
    return Strings.isNullOrEmpty((String) authenticationData.get(key))
        ? ""
        : (String) authenticationData.get(key);
  }

  private static String getFullName(final String firstName, final String lastName) {
    return (firstName.isEmpty() && lastName.isEmpty()) ? "" : firstName + " " + lastName;
  }
}
