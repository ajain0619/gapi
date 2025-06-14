package com.nexage.app.web;

import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.User;
import com.nexage.app.services.UserService;
import com.nexage.app.web.support.BaseControllerItTest;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserControllerIT extends BaseControllerItTest {

  @Mock private UserService userService;

  @InjectMocks private UserController userController;

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Test
  void testGetAllUsersForCompany() throws Exception {
    List<User> users = Collections.singletonList(createUser());

    when(userService.getAllUsersByCompanyPid(anyLong())).thenReturn(users);

    mockMvc
        .perform(get("/users?companyPID=1"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].companyType", is("NEXAGE")));
  }

  @Test
  void testDeleteUser() throws Exception {
    mockMvc
        .perform(delete("/users/1"))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }
}
