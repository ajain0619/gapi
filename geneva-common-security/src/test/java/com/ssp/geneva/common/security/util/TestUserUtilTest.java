package com.ssp.geneva.common.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class TestUserUtilTest {
  private static final String EMAIL = "GenevaTestUser@yahooinc.com";
  private static final String FIRST_NAME = "John";
  private static final String LAST_NAME = "Doe";
  private static final String SSO_LOGIN_USERNAME = "johndoe37";
  private static final Long COMPANY_PID = 1L;
  private static final String EMPTY_STRING = "";
  @Mock UserRepository userRepository;
  @Mock CompanyRepository companyRepository;
  @Mock Map<String, Object> authenticationData;
  @Mock Company company;
  private User createdUser;
  @InjectMocks TestUserUtil testAccountUtil;
  @Mock private SellerSeat sellerSeat;

  @BeforeEach
  void setup() {
    setupManagerUser();
  }

  private static User createBasicUser(Company company) {
    User user = new User();
    user.setUserName(EMAIL);
    user.setName(FIRST_NAME + " " + LAST_NAME);
    user.setEmail(EMAIL);
    user.addCompany(company);
    user.setEnabled(true);
    user.setCreationDate(new Date());
    return user;
  }

  private void setupSellerSeatUser() {
    createdUser = createBasicUser(company);
    createdUser.setRole(Role.ROLE_USER);
    createdUser.setSellerSeat(sellerSeat);
  }

  private void setupManagerUser() {
    createdUser = createBasicUser(company);
    createdUser.setRole(Role.ROLE_MANAGER);
  }

  @Test
  void shouldTestSetupTestUserOnNullAuthenticationData() {
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> testAccountUtil.setupTestUser(authenticationData));

    assertEquals(
        CommonErrorCodes.COMMON_INSUFFICIENT_AUTHENTICATION_DATA, exception.getErrorCode());
  }

  @Test
  void shouldTestSetupTestUserOnNullEmailInAuthenticationData() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(null);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> testAccountUtil.setupTestUser(authenticationData));

    assertEquals(
        CommonErrorCodes.COMMON_INSUFFICIENT_AUTHENTICATION_DATA, exception.getErrorCode());
  }

  @Test
  void shouldTestSetupTestUserOnEmptyEmailInAuthenticationData() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMPTY_STRING);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> testAccountUtil.setupTestUser(authenticationData));

    assertEquals(
        CommonErrorCodes.COMMON_INSUFFICIENT_AUTHENTICATION_DATA, exception.getErrorCode());
  }

  @Test
  void shouldTestSetupTestUserCreateAndUpdate() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMAIL);
    when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
    when(authenticationData.get(TestUserUtil.FIRST_NAME)).thenReturn(FIRST_NAME);
    when(authenticationData.get(TestUserUtil.LAST_NAME)).thenReturn(LAST_NAME);
    when(userRepository.save(any(User.class)))
        .thenAnswer((Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);
    when(authenticationData.get(TestUserUtil.SSO_LOGIN_USERNAME)).thenReturn(SSO_LOGIN_USERNAME);
    when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
    when(company.getPid()).thenReturn(COMPANY_PID);
    User createdAndUpdatedUser = testAccountUtil.setupTestUser(authenticationData);
    assertNotNull(createdAndUpdatedUser);
    assertEquals(EMAIL, createdAndUpdatedUser.getEmail());
    assertEquals(FIRST_NAME + " " + LAST_NAME, createdAndUpdatedUser.getName());
    assertEquals(User.Role.ROLE_ADMIN, createdAndUpdatedUser.getRole());
    assertEquals(EMAIL, createdAndUpdatedUser.getUserName());
    assertEquals(COMPANY_PID, createdAndUpdatedUser.getCompanyPid());
    assertEquals(SSO_LOGIN_USERNAME, createdAndUpdatedUser.getOneCentralUserName());
    assertEquals(FIRST_NAME, createdAndUpdatedUser.getFirstName());
    assertEquals(LAST_NAME, createdAndUpdatedUser.getLastName());
  }

  @Test
  void shouldTestSetupTestUserUpdateOnly() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMAIL);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(createdUser));
    when(authenticationData.get(TestUserUtil.FIRST_NAME)).thenReturn(FIRST_NAME);
    when(authenticationData.get(TestUserUtil.LAST_NAME)).thenReturn(LAST_NAME);
    when(authenticationData.get(TestUserUtil.SSO_LOGIN_USERNAME)).thenReturn(SSO_LOGIN_USERNAME);
    when(company.getPid()).thenReturn(COMPANY_PID);
    when(userRepository.save(any(User.class)))
        .thenAnswer((Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);
    User updatedUser = testAccountUtil.setupTestUser(authenticationData);
    assertNotNull(updatedUser);
    assertEquals(EMAIL, updatedUser.getEmail());
    assertEquals(FIRST_NAME + " " + LAST_NAME, updatedUser.getName());
    assertEquals(User.Role.ROLE_MANAGER, updatedUser.getRole());
    assertEquals(EMAIL, updatedUser.getUserName());
    assertEquals(COMPANY_PID, updatedUser.getCompanyPid());
    assertEquals(SSO_LOGIN_USERNAME, updatedUser.getOneCentralUserName());
    assertEquals(FIRST_NAME, updatedUser.getFirstName());
    assertEquals(LAST_NAME, updatedUser.getLastName());
  }

  @Test
  void shouldTestSetupSellerSeatTestUserUpdateOnly() {
    setupSellerSeatUser();

    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMAIL);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.ofNullable(createdUser));
    when(authenticationData.get(TestUserUtil.FIRST_NAME)).thenReturn(FIRST_NAME);
    when(authenticationData.get(TestUserUtil.LAST_NAME)).thenReturn(LAST_NAME);
    when(authenticationData.get(TestUserUtil.SSO_LOGIN_USERNAME)).thenReturn(SSO_LOGIN_USERNAME);
    when(userRepository.save(any(User.class)))
        .thenAnswer((Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);
    User updatedUser = testAccountUtil.setupTestUser(authenticationData);
    assertNotNull(updatedUser);
    assertEquals(EMAIL, updatedUser.getEmail());
    assertEquals(FIRST_NAME + " " + LAST_NAME, updatedUser.getName());
    assertEquals(Role.ROLE_USER, updatedUser.getRole());
    assertEquals(EMAIL, updatedUser.getUserName());
    assertNull(updatedUser.getCompanyPid());
    assertEquals(SSO_LOGIN_USERNAME, updatedUser.getOneCentralUserName());
    assertEquals(FIRST_NAME, updatedUser.getFirstName());
    assertEquals(LAST_NAME, updatedUser.getLastName());
    assertEquals(sellerSeat, updatedUser.getSellerSeat());
    assertEquals(CompanyType.SELLER, updatedUser.getCompanyType());
    assertEquals(1, updatedUser.getCompanies().size());
  }

  @Test
  void shouldTestSetupTestUserUpdateWithNullAuthenticationDataFields() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMAIL);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(createdUser));
    when(authenticationData.get(TestUserUtil.FIRST_NAME)).thenReturn(null);
    when(authenticationData.get(TestUserUtil.LAST_NAME)).thenReturn(null);
    when(authenticationData.get(TestUserUtil.SSO_LOGIN_USERNAME)).thenReturn(null);
    when(company.getPid()).thenReturn(COMPANY_PID);
    when(userRepository.save(any(User.class)))
        .thenAnswer((Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);
    User updatedUser = testAccountUtil.setupTestUser(authenticationData);
    assertNotNull(updatedUser);
    assertEquals(EMAIL, updatedUser.getEmail());
    assertEquals(EMPTY_STRING, updatedUser.getName());
    assertEquals(Role.ROLE_MANAGER, updatedUser.getRole());
    assertEquals(EMAIL, updatedUser.getUserName());
    assertEquals(COMPANY_PID, updatedUser.getCompanyPid());
    assertEquals(EMPTY_STRING, updatedUser.getOneCentralUserName());
    assertEquals(EMPTY_STRING, updatedUser.getFirstName());
    assertEquals(EMPTY_STRING, updatedUser.getLastName());
  }

  @Test
  void shouldTestSuccessfulSetupUpdateWithEmptyAuthenticationData() {
    when(authenticationData.get(TestUserUtil.EMAIL)).thenReturn(EMAIL);
    when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(createdUser));
    when(authenticationData.get(TestUserUtil.FIRST_NAME)).thenReturn(EMPTY_STRING);
    when(authenticationData.get(TestUserUtil.LAST_NAME)).thenReturn(EMPTY_STRING);
    when(authenticationData.get(TestUserUtil.SSO_LOGIN_USERNAME)).thenReturn(EMPTY_STRING);
    when(company.getPid()).thenReturn(COMPANY_PID);
    when(userRepository.save(any(User.class)))
        .thenAnswer((Answer<User>) invocationOnMock -> (User) invocationOnMock.getArguments()[0]);
    User updatedUser = testAccountUtil.setupTestUser(authenticationData);
    assertNotNull(updatedUser);
    assertEquals(EMAIL, updatedUser.getEmail());
    assertEquals(EMPTY_STRING, updatedUser.getName());
    assertEquals(Role.ROLE_MANAGER, updatedUser.getRole());
    assertEquals(EMAIL, updatedUser.getUserName());
    assertEquals(COMPANY_PID, updatedUser.getCompanyPid());
    assertEquals(EMPTY_STRING, updatedUser.getOneCentralUserName());
    assertEquals(EMPTY_STRING, updatedUser.getFirstName());
    assertEquals(EMPTY_STRING, updatedUser.getLastName());
  }
}
