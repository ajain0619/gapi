package com.ssp.geneva.common.security.util.login;

import static com.nexage.admin.core.model.User.Role.ROLE_ADMIN;
import static com.nexage.admin.core.model.User.Role.ROLE_API;
import static com.nexage.admin.core.model.User.Role.ROLE_API_IIQ;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_SMARTEX;
import static com.nexage.admin.core.model.User.Role.ROLE_MANAGER_YIELD;
import static com.nexage.admin.core.model.User.Role.ROLE_USER;
import static com.ssp.geneva.common.model.inventory.CompanyType.BUYER;
import static com.ssp.geneva.common.model.inventory.CompanyType.NEXAGE;
import static com.ssp.geneva.common.model.inventory.CompanyType.SEATHOLDER;
import static com.ssp.geneva.common.model.inventory.CompanyType.SELLER;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginEntitlementCorrectionHandlerTest {

  private static final List<Entitlement> entitlements = List.of(mock(Entitlement.class));
  @Mock private UserRepository userRepository;
  @Mock private ObjectMapper objectMapper;

  private LoginEntitlementCorrectionHandler loginEntitlementCorrectionHandler;

  private static Stream<Arguments> provideCompanyTypesAndUserRoles() {
    return Stream.of(Arguments.of(NEXAGE, ROLE_ADMIN), Arguments.of(SELLER, ROLE_MANAGER));
  }

  @BeforeEach
  void setUp() {
    loginEntitlementCorrectionHandler =
        new LoginEntitlementCorrectionHandler(userRepository, objectMapper);
  }

  @ParameterizedTest
  @MethodSource("provideCompanyTypesAndUserRoles")
  void shouldSuccessfullyExecuteOnAuthenticationSuccess(
      CompanyType mockedCompanyType, User.Role userRole) throws IOException {
    // given
    Company company = mock(Company.class);
    when(company.getType()).thenReturn(mockedCompanyType);
    User user = new User();
    user.setRole(userRole);
    user.addCompany(company);

    when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));
    when(objectMapper.configure(any(DeserializationFeature.class), anyBoolean()))
        .thenReturn(objectMapper);
    when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(entitlements);

    // when
    loginEntitlementCorrectionHandler.correctEntitlements("john.doe");

    // then
    verify(userRepository).findByUserName("john.doe");
  }

  @Test
  void shouldFailExecutionOnAuthenticationSuccessForBadPrincipal() {
    // given
    String username = null;
    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> loginEntitlementCorrectionHandler.correctEntitlements(username));
    assertEquals(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL, exception.getErrorCode());
  }

  @Test
  void shouldFailExecutionOnAuthenticationSuccessForBadSpringUser() {
    // given
    SpringUserDetails springUserDetails = null;
    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> loginEntitlementCorrectionHandler.correctEntitlements(springUserDetails));
    assertEquals(SecurityErrorCodes.SECURITY_BAD_PRINCIPAL, exception.getErrorCode());
  }

  @Test
  void shouldFailExecutionOnAuthenticationSuccessForUnknownUser() {
    // given
    String username = "username";
    when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
    // then
    assertThrows(
        RuntimeException.class,
        () -> loginEntitlementCorrectionHandler.correctEntitlements(username));
  }

  @Test
  void shouldReturnCorrectEntitlements() {
    String result =
        invokeMethod(loginEntitlementCorrectionHandler, "createEntitlements", ROLE_USER, BUYER);
    assertThat(result, containsString("Buyer"));
    assertThat(result, containsString("User"));

    result =
        invokeMethod(loginEntitlementCorrectionHandler, "createEntitlements", ROLE_API, SELLER);
    assertThat(result, containsString("Seller"));
    assertThat(result, containsString("Api"));

    result =
        invokeMethod(loginEntitlementCorrectionHandler, "createEntitlements", ROLE_API_IIQ, NEXAGE);
    assertThat(result, containsString("ApiIIQ"));

    result =
        invokeMethod(
            loginEntitlementCorrectionHandler, "createEntitlements", ROLE_MANAGER_SMARTEX, NEXAGE);
    assertThat(result, containsString("Nexage"));
    assertThat(result, containsString("Smartex"));
    assertThat(result, containsString("Manager"));
    assertThat(result, containsString("User"));

    result =
        invokeMethod(
            loginEntitlementCorrectionHandler, "createEntitlements", ROLE_MANAGER_YIELD, NEXAGE);
    assertThat(result, containsString("Nexage"));
    assertThat(result, containsString("Smartex"));
    assertThat(result, containsString("Manager"));
    assertThat(result, containsString("User"));
    assertThat(result, containsString("Yield"));

    result =
        invokeMethod(
            loginEntitlementCorrectionHandler, "createEntitlements", ROLE_ADMIN, SEATHOLDER);
    assertThat(result, containsString("Seatholder"));
    assertThat(result, containsString("Admin"));
    assertThat(result, containsString("Manager"));
    assertThat(result, containsString("User"));
  }

  @Test
  void shouldReturnEmptyEntitlementsListForNullCompanyType() {
    // given
    User user = mock(User.class);
    when(user.getRole()).thenReturn(ROLE_ADMIN);
    when(user.getCompanyType()).thenReturn(null);
    // when
    List<Entitlement> result =
        invokeMethod(loginEntitlementCorrectionHandler, "buildOneCentralEntitlements", user);
    // then
    assertTrue(result != null && result.isEmpty());
  }

  @Test
  void shouldReturnEmptyEntitlementsListForNullUserRole() {
    // given
    User user = mock(User.class);
    when(user.getRole()).thenReturn(null);
    when(user.getCompanyType()).thenReturn(NEXAGE);
    // when
    List<Entitlement> result =
        invokeMethod(loginEntitlementCorrectionHandler, "buildOneCentralEntitlements", user);
    // then
    assertTrue(result != null && result.isEmpty());
  }
}
