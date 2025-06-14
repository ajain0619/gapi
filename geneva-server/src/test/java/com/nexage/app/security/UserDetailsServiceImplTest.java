package com.nexage.app.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.service.UserDetailsServiceImpl;
import com.ssp.geneva.common.security.util.TestUserUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

  User user;

  private static String USERNAME = "testUserName";

  @Mock private UserRepository userRepository;
  @Mock private TestUserUtil testUserUtil;
  private UserDetailsServiceImpl userDetailsService;

  @BeforeEach
  void setUp() {
    userDetailsService = new UserDetailsServiceImpl(userRepository, testUserUtil, false);
  }

  @Test
  void shouldTestUsernameNotFoundException() {
    assertThrows(
        UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("anyUser"));
  }

  @Test
  void shouldTestLoginNotSupportedException() {
    buildUser("oneCentralUser", true);
    when(userRepository.findByUserName(USERNAME)).thenReturn(Optional.of(user));

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userDetailsService.loadUserByUsername(USERNAME));
    assertEquals(SecurityErrorCodes.SECURITY_LOGIN_NOT_SUPPORTED, exception.getErrorCode());
  }

  @ParameterizedTest
  @MethodSource("getUserNames")
  void shouldTestSuccessLogin(String userName) {
    buildUser(userName, true);
    when(userRepository.findByUserName(USERNAME)).thenReturn(Optional.of(user));

    UserDetails details = userDetailsService.loadUserByUsername(USERNAME);

    assertNotNull(details);
    assertEquals(USERNAME, details.getUsername());
    assertNull(details.getPassword());
  }

  @Test
  void shouldTestSuccessLoginWithEmpty1cUserAndUserWithMultipleCompanies() {
    Company c1 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    c1.setSelfServeAllowed(true);
    Company c2 = TestObjectsFactory.createCompany(CompanyType.SELLER);
    Company notUserCompany = TestObjectsFactory.createCompany(CompanyType.SELLER);
    user = TestObjectsFactory.createUser(Role.ROLE_USER, c1, c2);
    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    assertNotNull(details);
    assertTrue(
        details.isPublisherSelfServeEnabled(c1.getPid()), "self serve is enabled for Company C1");
    assertFalse(
        details.isPublisherSelfServeEnabled(c2.getPid()), "self serve is disabled for Company C2");
    assertEquals(CompanyType.SELLER, details.getType(), "homogeneous company type is expected");
    assertFalse(
        details.canAccess(notUserCompany.getPid()),
        "user cannot access company that it has not been assigned to");
    assertTrue(
        details.canAccess(c2.getPid()), "user can access company that it has not been assigned to");
  }

  @Test
  void shouldTestCanAccessAnotherUserFromDTO() {
    user = TestObjectsFactory.createUser();
    User userDTO = TestObjectsFactory.createUser(Role.ROLE_USER, user.getCompany());

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    assertTrue(
        details.canAccess(userDTO),
        "for DTO object we may still want check on companyPid instead of companies");
  }

  @Test
  void canAccessSellerSeatShouldReturnFalseWhenSellerSeatPidOfSpringUserIsNull() {
    // given
    Long sellerSeatPid = 123L;

    user = TestObjectsFactory.createUser();

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    // when
    boolean canAccessSellerSeatReturn = details.canAccessSellerSeat(sellerSeatPid);

    // then
    assertFalse(
        canAccessSellerSeatReturn,
        "If the sellerSeatPid of the spring user is null, then it can't access");
  }

  @Test
  void canAccessSellerSeatShouldReturnFalseWhenSellerSeatPidParamIsNull() {
    // given
    Long sellerSeatPid = null;

    user = TestObjectsFactory.createUser();
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    sellerSeat.setPid(123L);
    user.setSellerSeat(sellerSeat);

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    // when
    boolean canAccessSellerSeatReturn = details.canAccessSellerSeat(sellerSeatPid);

    // then
    assertFalse(
        canAccessSellerSeatReturn, "If the sellerSeatPid param is null, then it can't access");
  }

  @Test
  void canAccessSellerSeatShouldReturnTrueWhenSellerSeatPidParamMatchesUserSellerSeatPid() {
    // given
    Long sellerSeatPid = 123L;

    user = TestObjectsFactory.createUser();
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    sellerSeat.setPid(sellerSeatPid);
    user.setSellerSeat(sellerSeat);

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    // when
    boolean canAccessSellerSeatReturn = details.canAccessSellerSeat(sellerSeatPid);

    // then
    assertTrue(
        canAccessSellerSeatReturn,
        "If the sellerSeatPid param and user seller seat match, then it can access");
  }

  @Test
  void canAccessSellerSeatShouldReturnFalseWhenSellerSeatPidParamDoesNotMatchUserSellerSeatPid() {
    // given
    user = TestObjectsFactory.createUser();
    SellerSeat sellerSeat = TestObjectsFactory.createSellerSeat();
    sellerSeat.setPid(456L);
    user.setSellerSeat(sellerSeat);

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    // when
    boolean canAccessSellerSeatReturn = details.canAccessSellerSeat(123L);

    // then
    assertFalse(
        canAccessSellerSeatReturn,
        "If the sellerSeatPid param and user seller seat don't match, then it can't access");
  }

  @Test
  void shouldTestLoadingUser() {
    buildUser("", true);
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    UserDetails details = userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false);
    assertNotNull(details);
    assertEquals(USERNAME, details.getUsername());
    assertNull(details.getPassword());
  }

  @Test
  void shouldFailLoadingNonExistingUser() {
    buildUser("", true);
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.empty());
    assertThrows(
        UsernameNotFoundException.class,
        () -> userDetailsService.loadUserBy1CUsername(authenticationData, true));
  }

  @Test
  void shouldLoadUserEntitlements() {
    buildUser("", true);
    Map<String, Object> authenticationData = new HashMap<>();
    authenticationData.put("username", USERNAME);
    List entitlementsList = new ArrayList<>();
    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
    map.put("name", "entitlement1");
    map.put("id", 1);
    map.put("displayName", "Test entitlement");
    map.put("application", "OneMobile");
    map.put("type", "access");
    map.put("permission", "create");
    map.put("organizationIds", Arrays.asList(1L, 2L, 3L));
    entitlementsList.add(map);
    authenticationData.put("entitlements", entitlementsList);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    SpringUserDetails details =
        (SpringUserDetails)
            userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false);
    assertEquals(USERNAME, details.getUsername());
    assertNull(details.getPassword());
    var entitlement = details.getEntitlements().get(0);
    assertAll(
        "Should get the right entitlements",
        () -> assertNotNull(entitlement),
        () -> assertEquals("entitlement1", entitlement.getName()),
        () -> assertEquals(1, entitlement.getId()),
        () -> assertEquals("Test entitlement", entitlement.getDisplayName()),
        () -> assertEquals("OneMobile", entitlement.getApplication()),
        () -> assertEquals("access", entitlement.getType()),
        () -> assertEquals("create", entitlement.getPermission()));
  }

  @Test
  void shouldNotFailOnNullWhenLoadUserEntitlements() {
    buildUser("", true);
    Map<String, Object> authenticationData = new HashMap<>();
    authenticationData.put("username", USERNAME);
    List entitlementsList = new ArrayList<>();
    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
    map.put("application", "OneMobile");
    map.put("name", null); // Null pointer should not be thrown
    entitlementsList.add(map);
    authenticationData.put("entitlements", entitlementsList);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    SpringUserDetails details =
        (SpringUserDetails)
            userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false);
    assertNotNull(details);
  }

  @Test
  void shouldNotFailOnWrongTypeWhenLoadUserEntitlements() {
    buildUser("", true);
    Map<String, Object> authenticationData = new HashMap<>();
    authenticationData.put("username", USERNAME);
    List entitlementsList = new ArrayList<>();
    LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
    map.put("id", 1L); // id is an Int not Long
    entitlementsList.add(map);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    SpringUserDetails details =
        (SpringUserDetails)
            userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false);
    assertNotNull(details);
  }

  @Test
  void shouldTestLoadingDisabledUser() {
    buildUser("", false);
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    when(userRepository.findByOneCentralUserName(any(String.class))).thenReturn(Optional.of(user));
    assertThrows(
        DisabledException.class,
        () -> userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false));
  }

  @Test
  void shouldTestLoadingApiUser() {
    buildApiUser("oneCentralUserName", true);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    UserDetails details = userDetailsService.loadUserDetailsBy1CUsername(authenticationData, true);
    assertNotNull(details);
    assertEquals(USERNAME, details.getUsername());
    assertNull(details.getPassword());
  }

  @Test
  void shouldTestLoadingDisabledApiUser() {
    buildApiUser("oneCentralUserName", false);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    assertThrows(
        DisabledException.class,
        () -> userDetailsService.loadUserDetailsBy1CUsername(authenticationData, true));
  }

  @Test
  void shouldTestThrowingExceptionWhenApiUserIsFetchedForNonBearerAuthentication() {
    buildApiUser("oneCentralUserName", true);
    when(userRepository.findByOneCentralUserName(USERNAME)).thenReturn(Optional.of(user));
    Map<String, Object> authenticationData = Collections.singletonMap("username", USERNAME);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> userDetailsService.loadUserDetailsBy1CUsername(authenticationData, false));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldTestCurrentUserCanAccessSellerCompanies() {
    SellerSeat ss1 = TestObjectsFactory.createSellerSeat();
    SellerSeat ss2 = TestObjectsFactory.createSellerSeat();

    Company c1 = createSellerCompany(1L, ss1);
    Company c2 = createSellerCompany(2L, ss1);
    Company c3 = createSellerCompany(3L, ss2);
    user = TestObjectsFactory.createUser(Role.ROLE_USER, c1, c2);
    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    assertTrue(details.canAccess(Sets.newHashSet(c1.getPid(), c2.getPid())));
    assertTrue(details.canAccess(c1.getPid()));
    assertTrue(details.canAccess(c2.getPid()));

    assertFalse(details.canAccess(Sets.newHashSet(c1.getPid(), c2.getPid(), c3.getPid())));
    assertFalse(details.canAccess(Sets.newHashSet(c3.getPid())));
    assertFalse(details.canAccess(c3.getPid()));
  }

  @Test
  void shouldThrowUnauthorizedIfOneCentralUsernameNotEmpty() {
    SellerSeat ss1 = TestObjectsFactory.createSellerSeat();

    Company c1 = createSellerCompany(1L, ss1);
    User user = TestObjectsFactory.createUser(Role.ROLE_USER, c1);
    user.setOneCentralUserName("anyUser");
    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userDetailsService.loadUserByUsername("anyUser"));
    assertEquals(SecurityErrorCodes.SECURITY_LOGIN_NOT_SUPPORTED, exception.getErrorCode());
  }

  @Test
  void shouldTestCurrentGlobalSellerSeatUserCanAccessSellerCompanies() {
    SellerSeat ss1 = TestObjectsFactory.createSellerSeat();
    SellerSeat ss2 = TestObjectsFactory.createSellerSeat();

    Company c1 = createSellerCompany(1L, ss1);
    Company c2 = createSellerCompany(2L, ss1);
    Company c3 = createSellerCompany(3L, ss2);
    ss1.setSellers(Sets.newHashSet(c1, c2));
    ss2.setSellers(Sets.newHashSet(c3));

    user = TestObjectsFactory.createUser(Role.ROLE_USER, c1, c2);
    user.setSellerSeat(ss1);

    when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(user));

    SpringUserDetails details =
        (SpringUserDetails) userDetailsService.loadUserByUsername("anyUser");

    assertTrue(details.isAssociatedWithSellerSeat(Sets.newHashSet(c1.getPid(), c2.getPid())));

    assertFalse(
        details.isAssociatedWithSellerSeat(Sets.newHashSet(c1.getPid(), c2.getPid(), c3.getPid())));
    assertFalse(details.isAssociatedWithSellerSeat(Sets.newHashSet(c3.getPid())));
  }

  @Test
  void shouldUpdateUserMigrated() {
    User user = new User();
    User userUpdate = new User();
    userUpdate.setMigratedOneCentral(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(userUpdate);
    userDetailsService.updateUserMigrated(1L);

    assertTrue(user.isMigratedOneCentral());
    assertTrue(userUpdate.isMigratedOneCentral());
  }

  @Test
  void shouldLoadUserSuccesfully() {
    var mockUser = mock(User.class);
    when(mockUser.getPid()).thenReturn(100L);
    when(mockUser.getCompanyType()).thenReturn(CompanyType.NEXAGE);
    var userAuth = new UserAuth(mockUser, List.of());

    var result = userDetailsService.loadUser(userAuth);
    assertNotNull(result);
    assertTrue(result instanceof SpringUserDetails);

    var details = (SpringUserDetails) result;
    assertEquals(100L, details.getPid());
  }

  @Test
  void shouldLoadUserByUsernameSUccessfully() {
    var mockUser = mock(User.class);
    when(mockUser.getCompanyType()).thenReturn(CompanyType.NEXAGE);
    when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(mockUser));

    var result = userDetailsService.loadUserByUsername("username");
    assertNotNull(result);
  }

  @Test
  void shouldLoadUserFailNameNotFound() {
    when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("username"));
  }

  @Test
  void shouldLoadUserFail1CUsername() {
    var mockUser = mock(User.class);
    when(mockUser.getOneCentralUserName()).thenReturn("1cusername");
    when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(mockUser));

    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userDetailsService.loadUserByUsername("username"));
    assertEquals(SecurityErrorCodes.SECURITY_LOGIN_NOT_SUPPORTED, exception.getErrorCode());
  }

  private Company createSellerCompany(Long pid, SellerSeat sellerSeat) {
    Company company = TestObjectsFactory.createCompany(CompanyType.SELLER);
    company.setPid(pid);
    company.setSellerSeat(sellerSeat);
    return company;
  }

  private void buildUser(String oneCentralUserName, boolean enabled) {
    user = new User();
    Company company = new Company();
    company.setPid(1L);
    company.setType(CompanyType.SELLER);
    company.setSelfServeAllowed(true);
    user.setOneCentralUserName(oneCentralUserName);
    user.addCompany(company);
    user.setPid(123L);
    user.setEnabled(enabled);
    user.setUserName(USERNAME);
    user.setRole(Role.ROLE_ADMIN);
  }

  private void buildApiUser(String oneCentralUserName, boolean enabled) {
    user = new User();
    Company company = new Company();
    company.setPid(1L);
    company.setType(CompanyType.SELLER);
    company.setSelfServeAllowed(true);
    user.setOneCentralUserName(oneCentralUserName);
    user.addCompany(company);
    user.setPid(123L);
    user.setEnabled(enabled);
    user.setUserName(USERNAME);
    user.setRole(Role.ROLE_API);
  }

  private static Stream<String> getUserNames() {
    return Stream.of("", null);
  }
}
