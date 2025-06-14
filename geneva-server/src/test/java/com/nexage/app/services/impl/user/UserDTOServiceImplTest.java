package com.nexage.app.services.impl.user;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeat;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.dto.SellerSeatDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.CompanyViewDTOMapper;
import com.nexage.app.mapper.UserDTOMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.SellerSeatService;
import com.nexage.app.services.UserCompanyService;
import com.nexage.app.services.UserSellerSeatService;
import com.nexage.app.services.user.UserOneCentralService;
import com.nexage.app.util.validator.UserUpdateValidator;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToLongFunction;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class UserDTOServiceImplTest {

  @Mock private SellerSeatService sellerSeatService;
  @Mock private UserSellerSeatService userSellerSeatService;
  @Mock private CompanyService companyService;
  @Mock private UserCompanyService userCompanyService;
  @Mock private UserOneCentralService userOneCentralService;
  @Mock private UserContext userContext;
  @Mock private UserRepository userRepository;
  @Mock private UserUpdateValidator userUpdateValidator;
  @Mock private SpringUserDetails springUser;
  @InjectMocks private UserDTOServiceImpl userDTOService;

  private final long companyPid = 111L;
  private final long sellerSeatPid = 11L;

  @BeforeEach
  public void before() {
    lenient().when(userContext.writePrivilegeCheck(any(User.class))).thenReturn(true);
  }

  @Test
  void shouldGetUsersByCompany() {
    // given
    var pageable = mock(Pageable.class);
    Company company = createCompany(CompanyType.SELLER);
    User user = createTestUser(company);
    var found = new PageImpl<>(Collections.singletonList(user));
    Set<String> qf = new HashSet<>();
    qf.add("companyPid");
    when(companyService.getCompany(anyLong())).thenReturn(company);
    getAllUsersMockBehaviour(found);

    // when
    var users = userDTOService.getAllUsers(qf, "111", pageable);

    // then
    assertEquals(1, users.getTotalElements());
  }

  @Test
  void shouldThrowWhenGettingUsersByNonexistentCompany() {
    // given
    var pageable = mock(Pageable.class);
    Set<String> qf = new HashSet<>();
    qf.add("companyPid");
    when(userContext.canAccessSellerSeat(eq(qf), anyString())).thenReturn(true);
    when(companyService.getCompany(anyLong())).thenReturn(null);

    // when
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> userDTOService.getAllUsers(qf, "1", pageable));

    // then
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenGettingUsersByNonexistentSellerSeat() {
    // given
    Set<String> qf = new HashSet<>();
    qf.add("sellerSeatPid");
    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));
    when(userContext.canAccessSellerSeat(eq(qf), anyString())).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    when(userContext.isOcUserNexage()).thenReturn(true);

    // when
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> userDTOService.getAllUsers(qf, "1", pageable));

    // then
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldGetUsersBySellerSeat() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    User user = createTestUser(company);

    List<User> found = Collections.singletonList(user);
    Page<User> page = new PageImpl<>(found);

    Set<String> qf = new HashSet<>();
    qf.add("sellerSeatPid");
    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));

    when(sellerSeatService.getSellerSeat(sellerSeatPid)).thenReturn(mock(SellerSeatDTO.class));
    getAllUsersMockBehaviour(page);

    // when
    Page<UserDTO> users = userDTOService.getAllUsers(qf, "11", pageable);

    // then
    assertTrue(users.hasContent());
    assertEquals(1, users.getNumberOfElements());
    assertEquals(user.getRole(), users.iterator().next().getRole());
    assertEquals(user.getEmail(), users.iterator().next().getEmail());
    assertEquals(
        user.getCompanies().iterator().next().getPid(),
        users.iterator().next().getCompanies().iterator().next().getPid());
  }

  @Test
  void shouldGetAllUsersWithoutQuery() {
    // given
    Company company = new Company();
    company.setType(CompanyType.NEXAGE);
    company.setPid(companyPid);
    List<User> users = new ArrayList<>();
    users.add(createTestUser(company));
    users.add(createTestUser(company));
    var found = new PageImpl<>(users);
    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));
    getAllUsersMockBehaviour(found);

    // when
    Page<UserDTO> result = userDTOService.getAllUsers(new HashSet<>(), null, pageable);

    // then
    assertEquals(2, result.getTotalElements());
  }

  @Test
  void shouldGetUsersByUserName() {
    // given
    List<User> users = new ArrayList<>();
    Company company = new Company();
    company.setType(CompanyType.SELLER);
    company.setPid(companyPid);
    users.add(createTestUser(company));
    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));
    var found = new PageImpl<>(users);
    Set<String> qf = new HashSet<>();
    qf.add("userName");
    getAllUsersMockBehaviour(found);

    // when
    Page<UserDTO> result = userDTOService.getAllUsers(qf, "user", pageable);

    // then
    assertEquals(1, result.getTotalElements());
    assertEquals(
        users.get(0).getPid(), result.get().findFirst().map(UserDTO::getPid).orElseThrow());
  }

  @Test
  void shouldGetCurrentUserOnlyWhenUnauthorizedToGetAllUsers() {
    // given
    List<User> users = new ArrayList<>();
    Company company = new Company();
    company.setType(CompanyType.SELLER);
    company.setPid(companyPid);
    users.add(createTestUser(company));
    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));
    var found = new PageImpl<>(users);
    getAllUsersMockBehaviourNotAuthorized(found);

    // when
    Page<UserDTO> result = userDTOService.getAllUsers(null, null, pageable);

    // then
    assertEquals(1, result.getTotalElements());
    assertEquals(
        users.get(0).getPid(), result.get().findFirst().map(UserDTO::getPid).orElseThrow());
  }

  @Test
  void shouldGetCurrentUserByOnlyCurrent() {
    // given
    Company company = new Company();
    company.setType(CompanyType.SELLER);
    company.setPid(companyPid);
    final User testUser = createTestUser(company);
    var users = List.of(testUser);

    Pageable pageable = PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")));
    var found = new PageImpl<>(users);

    Set<String> qf = Set.of("onlyCurrent");
    getAllUsersMockBehaviourWithCurrent(found, "SELLER");

    // when
    Page<UserDTO> result = userDTOService.getAllUsers(qf, "true", pageable);

    // then
    assertEquals(1, result.getTotalElements());
    assertEquals(testUser.getPid(), result.get().findFirst().map(UserDTO::getPid).orElseThrow());
  }

  @Test
  void shouldThrowWhenGettingUsersByInvalidField() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 1);
    when(userContext.canAccessSellerSeat(anySet(), any())).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    when(userContext.isOcUserNexage()).thenReturn(false);
    when(userContext.isOcAdminSeller()).thenReturn(true);
    // when
    Set<String> qf = Set.of("invalidField");
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userDTOService.getAllUsers(qf, null, pageRequest));
    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenGettingUsersByCompanyAndSellerSeat() {
    // given
    PageRequest pageRequest = PageRequest.of(0, 1);
    when(userContext.canAccessSellerSeat(anySet(), anyString())).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    when(userContext.isOcUserNexage()).thenReturn(false);
    when(userContext.isOcAdminSeller()).thenReturn(false);
    when(userContext.isOcAdminBuyer()).thenReturn(true);
    // when
    Set<String> qf = Set.of("companyPid", "sellerSeatPid");
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userDTOService.getAllUsers(qf, "1", pageRequest));
    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenGettingNonexistentUser() {
    // given
    long userId = 1L;

    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    // when
    var exception =
        assertThrows(GenevaValidationException.class, () -> userDTOService.getUser(userId));

    // then
    assertEquals(CommonErrorCodes.COMMON_USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenGettingUserWithoutAbilityToAccessThem() {
    // given
    long userId = 1L;

    User user = createUser();
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userContext.doSameOrNexageAffiliation(any(User.class))).thenReturn(false);

    // when
    var exception =
        assertThrows(GenevaSecurityException.class, () -> userDTOService.getUser(userId));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldGetUser() {
    // given
    long userId = 1L;
    User user = createUser();
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userContext.doSameOrNexageAffiliation(any(User.class))).thenReturn(true);

    // when
    UserDTO dto = userDTOService.getUser(userId);

    // then
    assertNotNull(dto, "Dto shouldn't be null");
    assertNull(dto.getSellerSeat(), "Seller seat should be null");
  }

  @Test
  void shouldCreateSellerUser() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    when(userOneCentralService.createUser(user)).thenReturn(user);

    // when
    UserDTO createdUser = userDTOService.createUser(UserDTOMapper.MAPPER.map(user));

    // then
    assertEquals(1, createdUser.getCompanies().size());
    assertEquals(
        CompanyViewDTOMapper.MAPPER.map(company),
        createdUser.getCompanies().iterator().next(),
        "the same company should be User#companies as requested for user");
  }

  @Test
  void shouldCreateApiUser() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    User user = TestObjectsFactory.createUser(User.Role.ROLE_API, company);
    user.setFirstName("firstname");
    user.setLastName("lastname");
    user.setEmail("test@mail.com");
    user.setContactName("contactName");
    user.setContactEmail("contactName@test.com");
    user.setRole(User.Role.ROLE_API);
    user.setOneCentralUserName("username");
    user.setEnabled(true);

    when(userOneCentralService.createUser(user)).thenReturn(user);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    // when
    userDTOService.createUser(UserDTOMapper.MAPPER.map(user));

    // then
    verify(userOneCentralService).createUser(argument.capture());
    User value = argument.getValue();
    assertEquals("username", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals(user.getEmail(), value.getEmail());
    assertNull(value.getPassword());
    assertTrue(value.isEnabled());
  }

  @Test
  void shouldCreateNexageYieldManager() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = TestObjectsFactory.createUser(User.Role.ROLE_MANAGER_YIELD, company);
    user.setFirstName("firstname");
    user.setLastName("lastname");
    user.setEmail("test@mail.com");
    user.setContactName("contactName");
    user.setContactEmail("contactName@test.com");
    user.setOneCentralUserName("username");
    user.setEnabled(true);

    when(userOneCentralService.createUser(user)).thenReturn(user);

    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    // when
    userDTOService.createUser(UserDTOMapper.MAPPER.map(user));

    // then
    verify(userOneCentralService).createUser(argument.capture());
    User value = argument.getValue();
    assertEquals("username", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals(user.getEmail(), value.getEmail());
    assertTrue(value.isEnabled());
    assertEquals(User.Role.ROLE_MANAGER_YIELD, value.getRole());
    assertEquals(CompanyType.NEXAGE, value.getCompanyType());
  }

  @Test
  void shouldPassAndSetGlobalFlagWhenNexageUserCreatesUserOrSellerSeatDoesMatch() {
    // given
    User user = createSellerSeatUser();
    user.setGlobal(true);

    when(userOneCentralService.createUser(user)).thenReturn(user);

    // when
    UserDTO createdUser = userDTOService.createUser(UserDTOMapper.MAPPER.map(user));

    // then
    assertTrue(createdUser.isGlobal());
  }

  @Test
  void shouldThrowWhenPrivilegeCheckFails() {
    // given
    User user = createUser();
    when(userContext.writePrivilegeCheck(user)).thenReturn(false);

    // when
    var exception =
        assertThrows(GenevaSecurityException.class, () -> userDTOService.checkPrivilege(user));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateApiUser() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    User user = createUserWithCompany(company);

    when(userOneCentralService.updateUser(user)).thenReturn(user);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));

    // when
    userDTOService.updateUser(UserDTOMapper.MAPPER.map(user), user.getPid());

    // then
    verify(userOneCentralService).updateUser(argument.capture());

    User value = argument.getValue();
    assertEquals("test123", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals(user.getEmail(), value.getEmail());
    assertNull(value.getPassword());
    assertTrue(value.isEnabled());
  }

  @Test
  void shouldThrowWhenChangingUserAffiliationByUnauthorizedUser() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    SellerSeat sellerSeat =
        createSellerSeat(createCompany(CompanyType.SELLER), createCompany(CompanyType.SELLER));

    User user = createUserWithCompany(company);

    UserDTO mappedUser = UserDTOMapper.MAPPER.map(user);
    SellerSeatDTO sellerSeatDTO = new SellerSeatDTO();
    sellerSeatDTO.setPid(sellerSeat.getPid());
    mappedUser.setSellerSeat(sellerSeatDTO);

    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));
    when(userContext.isNexageAdminOrManager()).thenReturn(false);

    // when
    Long pid = user.getPid();
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userDTOService.updateUser(mappedUser, pid));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldChangeUserAffiliationToSellerSeat() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    SellerSeat sellerSeat =
        createSellerSeat(createCompany(CompanyType.SELLER), createCompany(CompanyType.SELLER));

    User user = createUserWithCompany(company);

    UserDTO mappedUser = UserDTOMapper.MAPPER.map(user);
    SellerSeatDTO sellerSeatDTO = new SellerSeatDTO();
    sellerSeatDTO.setPid(sellerSeat.getPid());
    mappedUser.setSellerSeat(sellerSeatDTO);

    when(userOneCentralService.updateUser(user)).thenReturn(user);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));

    when(userContext.isNexageAdminOrManager()).thenReturn(true);
    doAnswer(
            (args) -> {
              user.setSellerSeat(sellerSeat);
              return null;
            })
        .when(userSellerSeatService)
        .updateUserWithVerifiedSellerSeat(user, sellerSeat.getPid());

    // when
    userDTOService.updateUser(mappedUser, user.getPid());

    // then
    verify(userOneCentralService).updateUser(argument.capture());

    User value = argument.getValue();
    assertEquals("test123", value.getOneCentralUserName());
    assertEquals(mappedUser.getFirstName(), value.getFirstName());
    assertEquals(mappedUser.getLastName(), value.getLastName());
    assertEquals(mappedUser.getEmail(), value.getEmail());
    assertEquals(sellerSeat.getPid(), value.getSellerSeat().getPid());
    assertNull(value.getPassword());
    assertTrue(value.isEnabled());
  }

  @Test
  void shouldChangeUserAffiliationBetweenCompanies() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    Company company2 = createCompany(CompanyType.SELLER);

    User user = createUserWithCompany(company);

    UserDTO mappedUser = UserDTOMapper.MAPPER.map(user);
    mappedUser.setCompanies(Set.of(CompanyViewDTOMapper.MAPPER.map(company2)));

    when(userOneCentralService.updateUser(user)).thenReturn(user);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));

    when(userContext.isNexageAdminOrManager()).thenReturn(true);
    doAnswer(
            (args) -> {
              user.getCompanies().clear();
              user.addCompany(company2);
              return null;
            })
        .when(userCompanyService)
        .updateUserWithVerifiedCompany(eq(user), anySet(), any(ToLongFunction.class));

    // when
    userDTOService.updateUser(mappedUser, user.getPid());

    // then
    verify(userOneCentralService).updateUser(argument.capture());

    User value = argument.getValue();
    assertEquals("test123", value.getOneCentralUserName());
    assertEquals(mappedUser.getFirstName(), value.getFirstName());
    assertEquals(mappedUser.getLastName(), value.getLastName());
    assertEquals(mappedUser.getEmail(), value.getEmail());
    assertEquals(company2, value.getCompany());
    assertNull(value.getPassword());
    assertTrue(value.isEnabled());
  }

  @Test
  void shouldUpdateSellerSeatUser() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);

    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));
    when(userContext.writePrivilegeCheck(user)).thenReturn(true);
    when(userOneCentralService.updateUser(user)).thenReturn(user);

    UserDTO mappedUser = UserDTOMapper.MAPPER.map(user);
    mappedUser.setContactNumber("987");
    // when
    UserDTO updatedUser = userDTOService.updateUser(mappedUser, user.getPid());

    // then
    assertNotNull(updatedUser);
  }

  @Test
  void shouldUpdateUserWithEnableDealAdminByNexageAdmin() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    user.setDealAdmin(true);

    User oldUser = (User) SerializationUtils.clone(user);
    oldUser.setDealAdmin(false);

    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(oldUser));
    when(userOneCentralService.updateUser(user)).thenReturn(user);

    // when
    UserDTO updatedUser = userDTOService.updateUser(UserDTOMapper.MAPPER.map(user), user.getPid());

    // then
    assertTrue(updatedUser.isDealAdmin());
  }

  @Test
  void shouldUpdateUserDisableDealAdminByNexageAdmin() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    user.setDealAdmin(false);

    User oldUser = (User) SerializationUtils.clone(user);
    oldUser.setDealAdmin(true);

    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(oldUser));
    when(userOneCentralService.updateUser(user)).thenReturn(user);
    // when
    UserDTO updatedUser = userDTOService.updateUser(UserDTOMapper.MAPPER.map(user), user.getPid());

    // then
    assertFalse(updatedUser.isDealAdmin());
  }

  @Test
  void shouldThrowWhenUpdatingUserByUnauthorizedUser() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    User user = createTestUser(company);
    SellerSeat ss = new SellerSeat();
    ss.setPid(1L);
    user.setSellerSeat(ss);
    when(userRepository.findById(user.getPid())).thenReturn(Optional.of(user));
    when(userContext.writePrivilegeCheck(any())).thenReturn(false);
    // when
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    Long userPid = user.getPid();
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> userDTOService.updateUser(userDto, userPid));

    // then
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenUpdatingNonexistentUser() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    User user = createTestUser(company);
    SellerSeat ss = new SellerSeat();
    ss.setPid(1L);
    user.setSellerSeat(ss);
    when(userRepository.findById(user.getPid())).thenReturn(Optional.empty());

    // when
    UserDTO userDto = UserDTOMapper.MAPPER.map(user);
    Long userPid = user.getPid();
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> userDTOService.updateUser(userDto, userPid));

    // then
    assertEquals(CommonErrorCodes.COMMON_USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowDuplicateDataExceptionWhenUpdateWithDuplicatedEmail() {
    // given
    String duplicatedEmail = "duplicatedEmail@mail.com";
    Company company = createCompany(CompanyType.SELLER);
    User user = createTestUser(company);
    user.setEmail(duplicatedEmail);
    when(userRepository.existsByEmailAndPidNot(duplicatedEmail, user.getPid())).thenReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userDTOService.updateUser(UserDTOMapper.MAPPER.map(user), user.getPid()));

    // then
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_EMAIL, exception.getErrorCode());
  }

  @Test
  void shouldThrowDuplicateDataExceptionWhenUpdateWithDuplicatedUserName() {
    // given
    String duplicatedUserName = "duplicatedUserName";
    Company company = createCompany(CompanyType.SELLER);
    User user = createTestUser(company);
    user.setUserName(duplicatedUserName);
    when(userRepository.existsByUserNameAndPidNot(duplicatedUserName, user.getPid()))
        .thenReturn(true);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> userDTOService.updateUser(UserDTOMapper.MAPPER.map(user), user.getPid()));

    // then
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_USER_NAME, exception.getErrorCode());
  }

  private User createUserWithCompany(Company company) {
    User user = TestObjectsFactory.createUser(User.Role.ROLE_API, company);
    user.setFirstName("firstname1");
    user.setLastName("lastname1");
    user.setEmail("test1@mail.com");
    user.setContactName("contactName1");
    user.setContactEmail("contactName1@test.com");
    user.setOneCentralUserName("test123");
    user.setRole(User.Role.ROLE_API);
    user.setEnabled(true);
    return user;
  }

  private User createSellerSeatUser() {
    Company company1 = createCompany(CompanyType.SELLER);
    Company company2 = createCompany(CompanyType.SELLER);
    User user = TestObjectsFactory.createUser(User.Role.ROLE_ADMIN, company1, company2);
    user.setEmail("valid@mail.com");
    SellerSeat sellerSeat = createSellerSeat();
    sellerSeat.setPid(sellerSeat.getPid());
    user.setSellerSeat(sellerSeat);
    return user;
  }

  private User createTestUser(Company company) {
    User user = createUser(User.Role.ROLE_ADMIN, company);
    user.setEmail("valid@mail.com");
    return user;
  }

  private void getAllUsersMockBehaviour(Page page) {
    when(springUser.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.canAccessSellerSeat(anySet(), any())).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    when(userContext.isOcUserNexage()).thenReturn(false);
    when(userContext.isOcAdminSeller()).thenReturn(false);
    when(userContext.isOcAdminBuyer()).thenReturn(false);
    when(userContext.isOcAdminSeatHolder()).thenReturn(true);

    when(userContext.getCurrentUser()).thenReturn(springUser);
    when(userContext.getCompanyPids()).thenReturn(Set.of(12345L, 67890L));
    when(userContext.getPid()).thenReturn(12345L);
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
  }

  private void getAllUsersMockBehaviourNotAuthorized(Page page) {
    when(userContext.canAccessSellerSeat(any(), any())).thenReturn(false);
    when(userContext.isOcApiIIQ()).thenReturn(false);
    when(userContext.isOcUserNexage()).thenReturn(false);
    when(userContext.isOcAdminSeller()).thenReturn(false);
    when(userContext.isOcAdminBuyer()).thenReturn(false);
    when(userContext.isOcAdminSeatHolder()).thenReturn(false);

    when(userContext.getPid()).thenReturn(12345L);
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
  }

  private void getAllUsersMockBehaviourWithCurrent(Page page, String companyType) {
    Set<Long> companyPids = Set.of(12345L, 67890L);
    when(springUser.getType()).thenReturn(CompanyType.valueOf(companyType));
    when(userContext.canAccessSellerSeat(anySet(), anyString())).thenReturn(true);
    when(userContext.getCurrentUser()).thenReturn(springUser);
    when(userContext.getCompanyPids()).thenReturn(companyPids);
    when(userContext.getPid()).thenReturn(12345L);
    when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
  }
}
