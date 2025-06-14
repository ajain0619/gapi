package com.nexage.app.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.app.dto.AccessTokenDTO;
import com.nexage.app.services.TokenService;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
class TokenDTOControllerIT {
  private MockMvc mockMvc;

  @Mock private TokenService tokenService;

  @InjectMocks private TokenDTOController tokenDTOController;

  @BeforeEach
  public void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(tokenDTOController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void testGetToken() throws Exception {

    List<AccessTokenDTO> accessTokenDTOS = TestObjectsFactory.gimme(1, AccessTokenDTO.class);

    when(tokenService.getToken(anyString(), any(Set.class))).thenReturn(accessTokenDTOS.get(0));
    mockMvc
        .perform(get("/v1/tokens?qt=status&qf=current"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath("$.content[0].accessToken", is(accessTokenDTOS.get(0).getAccessToken())))
        .andExpect(jsonPath("$.content[0].tokenType", is(accessTokenDTOS.get(0).getTokenType())))
        .andExpect(jsonPath("$.content[0].expiresIn", is(accessTokenDTOS.get(0).getExpiresIn())));
  }

  @Test
  void testGetTokenNoParams() throws Exception {

    mockMvc
        .perform(get("/v1/tokens"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();
  }
}
