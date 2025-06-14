package com.nexage.app.web;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static com.nexage.app.web.support.TestObjectsFactory.getUserDtoFromUser;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.nexage.admin.core.model.User;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.services.user.UserDTOService;
import com.nexage.app.web.support.BaseControllerItTest;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

class UserDTOControllerIT extends BaseControllerItTest {

  @Autowired private ControllerExceptionHandler controllerExceptionHandler;
  @Mock private UserDTOService userDTOService;

  @InjectMocks private UserDTOController usersController;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(usersController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(controllerExceptionHandler)
            .setValidator(mock(Validator.class))
            .build();
  }

  @Test
  void testGetAllUsersForCompanyPaged() throws Exception {
    // given
    User user = createUser(User.Role.ROLE_USER, createCompany(CompanyType.NEXAGE));
    Page<UserDTO> dto =
        new PageImpl(Collections.singletonList(TestObjectsFactory.getUserDtoFromUser(user)));
    when(userDTOService.getAllUsers(any(HashSet.class), any(), any(Pageable.class)))
        .thenReturn(dto);
    // when
    mockMvc
        .perform(get("/v1/users/?qf=companyPid&qt=1&page=0"))
        // then
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].companies[0].type", is("NEXAGE")));
  }

  @Test
  void testGetAllUsersForSellerSeat() throws Exception {
    // given
    User user =
        createUser(
            User.Role.ROLE_USER,
            createCompany(CompanyType.SELLER),
            createCompany(CompanyType.SELLER));
    Page<UserDTO> dto =
        new PageImpl(Collections.singletonList(TestObjectsFactory.getUserDtoFromUser(user)));
    when(userDTOService.getAllUsers(any(HashSet.class), any(), any(Pageable.class)))
        .thenReturn(dto);

    // when
    mockMvc
        .perform(get("/v1/users/?qf=sellerSeatPid&qt=1"))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0]", not(hasProperty("sellerSeat"))))
        .andExpect(jsonPath("$.content[0].sellerSeat.pid", is(user.getSellerSeat().getPid())))
        .andExpect(jsonPath("$.totalElements", is(1)));
  }

  @Test
  void testGetAllUsersPaged() throws Exception {
    // given
    User user =
        createUser(
            User.Role.ROLE_USER,
            createCompany(CompanyType.SELLER),
            createCompany(CompanyType.SELLER));

    Page<UserDTO> dto =
        new PageImpl(Collections.singletonList(TestObjectsFactory.getUserDtoFromUser(user)));
    when(userDTOService.getAllUsers(any(HashSet.class), any(), any(Pageable.class)))
        .thenReturn(dto);

    // when
    ResultActions result =
        this.mockMvc
            .perform(get("/v1/users?page=0&qf=userName&qt=test"))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].companies[0].type", is("SELLER")))
            .andExpect(jsonPath("$.content[0].companies[1].type", is("SELLER")))
            .andExpect(jsonPath("$.totalElements", is(1)));
  }

  @Test
  void shouldNotReturnPasswordFieldWhenGettingUserByUserPid() throws Exception {
    // given
    UserDTO userDTO = getUserDtoFromUser(createUser());
    when(userDTOService.getUser(anyLong())).thenReturn(userDTO);

    // when
    mockMvc
        .perform(get("/v1/users/{userPid}", 2))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", not(hasProperty("password"))));
  }

  @Test
  void shouldThrowExceptionWhenNoUserIsFound() throws Exception {
    // given
    Long userPid = 0L;
    when(userDTOService.getUser(userPid))
        .thenThrow(new GenevaValidationException(CommonErrorCodes.COMMON_USER_NOT_FOUND));

    // when
    mockMvc
        .perform(get("/v1/users/{userPid}", userPid))
        // then
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        CommonErrorCodes.COMMON_USER_NOT_FOUND))));
  }

  @Test
  void testGetUserByUserPid_shouldThrowExceptionWhenNoUserIsFound1() throws Exception {
    // given
    Long userPid = 0L;
    when(userDTOService.getUser(userPid))
        .thenThrow(new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED));

    // when
    mockMvc
        .perform(get("/v1/users/{userPid}", userPid))

        // then
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath(
                "$.errorMessage",
                is(
                    controllerExceptionHandler.getErrorMessage(
                        SecurityErrorCodes.SECURITY_NOT_AUTHORIZED))));
  }

  @Test
  void testCreateUser() throws Exception {
    // given
    UserDTO userDTO = getUserDtoFromUser(createUser());
    when(userDTOService.createUser(any(UserDTO.class))).thenReturn(userDTO);
    Gson gson = new Gson();

    // when
    mockMvc
        .perform(
            post("/v1/users")
                .content(gson.toJson(userDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))

        // then
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andDo(
            result -> {
              UserDTO returnedUserDTO =
                  gson.fromJson(result.getResponse().getContentAsString(), UserDTO.class);
              assertEquals(userDTO, returnedUserDTO);
            });
  }

  @Test
  void testUpdateUser() throws Exception {
    // given
    UserDTO userDTO = getUserDtoFromUser(createUser());
    when(userDTOService.updateUser(any(UserDTO.class), anyLong())).thenReturn(userDTO);
    Gson gson = new Gson();

    // when
    mockMvc
        .perform(
            put("/v1/users/1")
                .content(gson.toJson(userDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))

        // then
        .andExpect(status().is2xxSuccessful())
        .andDo(
            result -> {
              UserDTO returnedUserDTO =
                  gson.fromJson(result.getResponse().getContentAsString(), UserDTO.class);
              assertEquals(userDTO, returnedUserDTO);
            });
  }

  /** Test get current user with qf/qt */
  @Test
  void testGetCurrentUserWithQfQt() throws Exception {
    // given
    User user =
        createUser(
            User.Role.ROLE_USER,
            createCompany(CompanyType.SELLER),
            createCompany(CompanyType.SELLER));
    Set<String> qf = new LinkedHashSet<>();
    qf.add("onlyCurrent");

    Page<UserDTO> dto =
        new PageImpl(Collections.singletonList(TestObjectsFactory.getUserDtoFromUser(user)));
    when(userDTOService.getAllUsers(eq(qf), eq("true"), any())).thenReturn(dto);

    // when
    ResultActions result =
        this.mockMvc
            .perform(get("/v1/users?qf=onlyCurrent&qt=true"))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].companies[0].type", is("SELLER")))
            .andExpect(jsonPath("$.content[0].companies[1].type", is("SELLER")))
            .andExpect(jsonPath("$.totalElements", is(1)));
  }
}
