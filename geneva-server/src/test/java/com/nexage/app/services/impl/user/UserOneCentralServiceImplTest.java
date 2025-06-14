package com.nexage.app.services.impl.user;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.manager.OneCentralUserManager;
import com.nexage.app.security.UserContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class UserOneCentralServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private OneCentralUserManager oneCentralUserManager;
  @Mock private UserContext userContext;
  @Autowired private UserOneCentralServiceImpl userOneCentralService;

  @BeforeEach
  public void before() {
    userOneCentralService =
        new UserOneCentralServiceImpl(oneCentralUserManager, userRepository, userContext);
  }

  @Test
  void updateUserWithDifferentUserNameThrowException() {
    // given
    Company company = createCompany(CompanyType.NEXAGE);

    User user = createTestUser(company);
    user.setDealAdmin(false);
    user.setOneCentralUserName("oneCentralUserName");

    User oldUser = (User) SerializationUtils.clone(user);
    oldUser.setDealAdmin(true);
    oldUser.setPid(44L);
    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> userOneCentralService.updateUser(oldUser));

    // then
    assertEquals(ServerErrorCodes.SERVER_USER_NAME_CANNOT_BE_CHANGED, exception.getErrorCode());
  }

  @Test
  void updateUserTest() {
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

    OneCentralUserResponseDTO.OneCentralUser oneCentralUser =
        mock(OneCentralUserResponseDTO.OneCentralUser.class);
    when(oneCentralUserManager.updateOneCentralUser(user)).thenReturn(oneCentralUser);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.existsByUserNameAndPid(user.getUserName(), user.getPid())).thenReturn(true);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    // when
    userOneCentralService.updateUser(user);

    // then
    verify(userRepository).save(argument.capture());
    User value = argument.getValue();
    assertEquals("username", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals(user.getEmail(), value.getEmail());
    assertTrue(value.isEnabled());
  }

  @Test
  void shouldNotUpdateUserOnOneCentralWhenIdentityIqPetitioner() {
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

    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.existsByUserNameAndPid(user.getUserName(), user.getPid())).thenReturn(true);
    when(userContext.isInternalIdentityIqUser()).thenReturn(true);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    // when
    userOneCentralService.updateUser(user);

    // then
    verify(userRepository).save(argument.capture());
    User value = argument.getValue();
    assertEquals("username", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals(user.getEmail(), value.getEmail());
    assertTrue(value.isEnabled());
    verify(oneCentralUserManager, never()).updateOneCentralUser(user);
  }

  @Test
  void createUserTest() {
    // given
    Company company = createCompany(CompanyType.SELLER);

    User user = TestObjectsFactory.createUser(User.Role.ROLE_API, company);
    user.setFirstName("firstname");
    user.setLastName("lastname");
    user.setEmail("test@mail.com");
    user.setContactName("contactName");
    user.setContactEmail("contactName@test.com");
    user.setRole(User.Role.ROLE_API);
    user.setEnabled(true);

    OneCentralUserResponseDTO.OneCentralUser oneCentralUser =
        mock(OneCentralUserResponseDTO.OneCentralUser.class);
    when(oneCentralUserManager.createUserEnabled()).thenReturn(true);
    when(oneCentralUserManager.createOneCentralUser(user)).thenReturn(oneCentralUser);
    when(oneCentralUser.getUsername()).thenReturn("username");
    when(oneCentralUser.getFirstName()).thenReturn("firstname2");
    when(oneCentralUser.getLastName()).thenReturn("lastname2");
    when(oneCentralUserManager.createOneCentralUser(user)).thenReturn(oneCentralUser);
    when(oneCentralUserManager.createOneCentralUser(user)).thenReturn(oneCentralUser);
    when(userRepository.save(user)).thenReturn(user);
    ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

    // when
    userOneCentralService.createUser(user);

    // then
    verify(userRepository).save(argument.capture());
    User value = argument.getValue();
    assertEquals("username", value.getOneCentralUserName());
    assertEquals(user.getFirstName(), value.getFirstName());
    assertEquals(user.getLastName(), value.getLastName());
    assertEquals("firstname2 lastname2", value.getName());
    assertEquals(user.getEmail(), value.getEmail());
    assertTrue(value.isEnabled());
  }

  private User createTestUser(Company company) {
    User user = createUser(User.Role.ROLE_ADMIN, company);
    user.setEmail("valid@mail.com");
    return user;
  }
}
