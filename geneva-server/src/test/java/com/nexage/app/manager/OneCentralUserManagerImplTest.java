package com.nexage.app.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigProperties;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkJacksonBeanFactory;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralExtendedUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUsersResponseDTO;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.PasswordUpdateRequest;
import com.ssp.geneva.sdk.onecentral.model.UserMigrationRequest;
import com.ssp.geneva.sdk.onecentral.repository.AuthorizationManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserAuthorizationRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementPasswordRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserMigrationRepository;
import com.ssp.geneva.sdk.onecentral.service.OneCentralUserService;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Log4j2
@ExtendWith(MockitoExtension.class)
class OneCentralUserManagerImplTest {

  private static OneCentralSdkConfigProperties oneCentralSdkConfigProperties;
  private static UserManagementRepository userManagementRepository;
  private static UserMigrationRepository userMigrationRepository;
  private static UserAuthorizationRepository userAuthorizationRepository;
  private static UserManagementPasswordRepository userManagementPasswordRepository;
  private static OneCentralSdkClient oneCentralSdkClient;
  private static OneCentralUserService userService;
  private static AuthorizationManagementRepository authorizationManagementRepository;
  private static OAuth2RestTemplate s2sTemplate;
  private final ObjectMapper objectMapper = OneCentralSdkJacksonBeanFactory.initObjectMapper();
  private OneCentralUserManager oneCentralUserManager;
  private final String ONE_CENTRAL_USER_RESPONSE = "/data/oneCentral_usersByEmailResponse.json";

  @BeforeAll
  public static void beforeAll() {
    final Boolean ssoCreateOneCentralUser = true;
    final String ssoOneApiBaseUrl = "https://oneapi.com";
    final String ssoUiBaseEndpoint = "http://localhost";
    final String ssoSystemName = "SSP";
    final String ssoRoleId = "1234";
    final String ssoApiUserRoleId = "5678";
    oneCentralSdkConfigProperties =
        OneCentralSdkConfigProperties.builder()
            .ssoCreateOneCentralUser(ssoCreateOneCentralUser)
            .ssoOneApiBaseUrl(ssoOneApiBaseUrl)
            .ssoUiBaseEndpoint(ssoUiBaseEndpoint)
            .ssoSystemName(ssoSystemName)
            .ssoRoleId(ssoRoleId)
            .ssoApiUserRoleId(ssoApiUserRoleId)
            .build();
    userManagementRepository = mock(UserManagementRepository.class);
    userMigrationRepository = mock(UserMigrationRepository.class);
    userManagementPasswordRepository = mock(UserManagementPasswordRepository.class);
    userAuthorizationRepository = mock(UserAuthorizationRepository.class);
    authorizationManagementRepository = mock(AuthorizationManagementRepository.class);
    s2sTemplate = mock(OAuth2RestTemplate.class);
    OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
    when(accessToken.getValue()).thenReturn(UUID.randomUUID().toString());
    when(s2sTemplate.getAccessToken()).thenReturn(accessToken);

    userService = mock(OneCentralUserService.class);
    oneCentralSdkClient =
        OneCentralSdkClient.builder()
            .userManagementRepository(userManagementRepository)
            .userMigrationRepository(userMigrationRepository)
            .userAuthorizationRepository(userAuthorizationRepository)
            .userManagementPasswordRepository(userManagementPasswordRepository)
            .authorizationManagementRepository(authorizationManagementRepository)
            .userService(userService)
            .oneCentralSdkConfigProperties(oneCentralSdkConfigProperties)
            .build();
  }

  @BeforeEach
  void setUp() {
    reset(
        userManagementRepository,
        userMigrationRepository,
        userManagementPasswordRepository,
        userAuthorizationRepository,
        authorizationManagementRepository);
    reset(userService);
    oneCentralUserManager = new OneCentralUserManagerImpl(oneCentralSdkClient, s2sTemplate);
  }

  @Test
  void shouldCreateUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildUser();
    String serviceResponse =
        "{ \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@test.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    String usersByEmailResponse = "{ \"list\": [], \"totalCount\": 0}";
    OneCentralUsersResponseDTO oneCentralUsersByEmail =
        objectMapper.readValue(usersByEmailResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUsersResponseDTO> oneCentralUsersResponseEntity =
        ResponseEntity.of(Optional.of(oneCentralUsersByEmail));
    when(userManagementRepository.getUsersByEmail(any(), any()))
        .thenReturn(oneCentralUsersResponseEntity);
    when(userManagementRepository.createUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.createOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    ArgumentCaptor<OneCentralUserRequestDTO> argumentCaptor =
        ArgumentCaptor.forClass(OneCentralUserRequestDTO.class);
    verify(userManagementRepository, times(1))
        .createUser(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getEmail(), user.getEmail());
    verifyNoInteractions(userMigrationRepository, userManagementPasswordRepository);
  }

  @Test
  void shouldCreateInternalUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildInternalUser();
    String serviceResponse =
        "{ \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@yahooinc.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    when(userMigrationRepository.create(eq(accessToken), any())).thenReturn(responseEntity);
    var usersByEmailResponse = getData(ONE_CENTRAL_USER_RESPONSE);
    OneCentralUsersResponseDTO oneCentralUsersByEmail =
        objectMapper.readValue(usersByEmailResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUsersResponseDTO> oneCentralUsersResponseEntity =
        ResponseEntity.of(Optional.of(oneCentralUsersByEmail));
    when(userManagementRepository.getUsersByEmail(any(), any()))
        .thenReturn(oneCentralUsersResponseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.createOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    ArgumentCaptor<UserMigrationRequest> argumentCaptor =
        ArgumentCaptor.forClass(UserMigrationRequest.class);
    verify(userMigrationRepository, times(1)).create(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getEmail(), user.getEmail());
    verifyNoInteractions(userManagementPasswordRepository);
  }

  @Test
  void shouldCreateApiUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildApiUser();
    String serviceResponse =
        "{  \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@test.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    when(userManagementRepository.createUser(eq(accessToken), any())).thenReturn(responseEntity);
    var usersByEmailResponse = getData(ONE_CENTRAL_USER_RESPONSE);
    OneCentralUsersResponseDTO oneCentralUserListByEmail =
        objectMapper.readValue(usersByEmailResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUsersResponseDTO> oneCentralUsersResponseEntity =
        ResponseEntity.of(Optional.of(oneCentralUserListByEmail));
    when(userManagementRepository.getUsersByEmail(any(), any()))
        .thenReturn(oneCentralUsersResponseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.createOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    ArgumentCaptor<OneCentralUserRequestDTO> argumentCaptor =
        ArgumentCaptor.forClass(OneCentralUserRequestDTO.class);
    verify(userManagementRepository, times(1))
        .createUser(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getEmail(), user.getEmail());
    verifyNoInteractions(userMigrationRepository, userManagementPasswordRepository);
  }

  @Test
  void shouldUpdateUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildUser();
    String serviceResponse =
        "{ \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@test.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    when(userService.updateUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.updateOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    var argumentCaptor = ArgumentCaptor.forClass(OneCentralExtendedUserRequestDTO.class);
    verify(userService, times(1)).updateUser(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getUserRequestDTO().getEmail(), user.getEmail());
    verifyNoInteractions(userManagementPasswordRepository);
  }

  @Test
  void shouldUpdateInternalUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildInternalUser();
    String serviceResponse =
        "{ \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@yahooinc.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    when(userService.updateUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.updateOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    var argumentCaptor = ArgumentCaptor.forClass(OneCentralExtendedUserRequestDTO.class);
    verify(userService, times(1)).updateUser(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getUserRequestDTO().getEmail(), user.getEmail());
    verifyNoInteractions(userMigrationRepository, userManagementPasswordRepository);
  }

  @Test
  void shouldUpdateApiUserInOneCentral() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildApiUser();
    String serviceResponse =
        "{ \"id\": \"666\", \"username\": \"1cusername\", \"email\": \"tester@test.com\", \"firstName\":\"userFirstName\", \"lastName\": \"userLastName\"}";
    OneCentralUser oneCentralUser =
        objectMapper.readValue(serviceResponse, new TypeReference<>() {});
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.of(Optional.of(oneCentralUser));
    when(userService.updateUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    OneCentralUser response = oneCentralUserManager.updateOneCentralUser(user);

    // then
    assertEquals(response.getEmail(), user.getEmail());
    assertEquals(response.getFirstName(), user.getFirstName());
    assertEquals(response.getLastName(), user.getLastName());
    assertEquals(666, response.getId());
    assertEquals("1cusername", response.getUsername());
    var argumentCaptor = ArgumentCaptor.forClass(OneCentralExtendedUserRequestDTO.class);
    verify(userService, times(1)).updateUser(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    assertEquals(argumentCaptor.getValue().getUserRequestDTO().getEmail(), user.getEmail());
    verifyNoInteractions(userMigrationRepository, userManagementPasswordRepository);
  }

  @Test
  void shouldThrowExceptionWhenOneCentralResponseStatusIsNotOk() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildApiUser();
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.badRequest().body(null);
    when(userService.updateUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    var exception =
        assertThrows(
            OneCentralException.class, () -> oneCentralUserManager.updateOneCentralUser(user));
    assertEquals(OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenOneCentralResponseBodyIsNNull() throws Exception {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildApiUser();
    ResponseEntity<OneCentralUser> responseEntity = ResponseEntity.ok(null);
    when(userService.updateUser(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    var exception =
        assertThrows(
            OneCentralException.class, () -> oneCentralUserManager.updateOneCentralUser(user));
    assertEquals(OneCentralErrorCodes.ONECENTRAL_NULL_RESPONSE, exception.getErrorCode());
  }

  @Test
  void shouldChangePasswordInOneCentral() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    User user = buildUser();
    String serviceResponse = "{ \"oldpassword\": \"oldpass\", \"newpassword\": \"newpass\"}";
    ResponseEntity<String> responseEntity = ResponseEntity.of(Optional.of(serviceResponse));
    when(userManagementPasswordRepository.reset(eq(accessToken), any())).thenReturn(responseEntity);

    // when
    String response = oneCentralUserManager.resetPassword(user, "oldpass", "newpass");

    // then
    assertNotNull(response);
    JsonParser jsonParser = new JsonParser();
    JsonObject element = jsonParser.parse(response).getAsJsonObject();
    assertEquals("oldpass", element.get("oldpassword").getAsString());
    assertEquals("newpass", element.get("newpassword").getAsString());
    ArgumentCaptor<PasswordUpdateRequest> argumentCaptor =
        ArgumentCaptor.forClass(PasswordUpdateRequest.class);
    verify(userManagementPasswordRepository, times(1))
        .reset(eq(accessToken), argumentCaptor.capture());
    assertNotNull(argumentCaptor);
    assertNotNull(argumentCaptor.getValue());
    verifyNoInteractions(userMigrationRepository, userManagementRepository);
  }

  @Test
  void shouldCheckIfCreateInOneCentralIsEnabled() {
    assertTrue(oneCentralUserManager.createUserEnabled());
  }

  private User buildUser() {
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, new Company());
    user.setEmail("tester@test.com");
    user.setFirstName("userFirstName");
    user.setLastName("userLastName");
    user.setOneCentralUserName("testUser");
    return user;
  }

  private User buildInternalUser() {
    Company company = new Company();
    company.setType(CompanyType.NEXAGE);
    company.setName("testCompany");
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    user.setEmail("tester@yahooinc.com");
    user.setFirstName("userFirstName");
    user.setLastName("userLastName");
    user.setOneCentralUserName("testUser");
    return user;
  }

  private User buildApiUser() {
    User user = TestObjectsFactory.createUser(Role.ROLE_API, new Company());
    user.setEmail("tester@test.com");
    user.setFirstName("userFirstName");
    user.setLastName("userLastName");
    user.setContactName("contactName");
    user.setContactEmail("contactName@test.com");
    return user;
  }

  private String getData(String name) throws IOException {
    return Resources.toString(
        Resources.getResource(this.getClass(), name), Charset.forName("UTF-8"));
  }
}
