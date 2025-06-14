package com.nexage.app.web;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.nexage.app.dto.IdentityProviderDTO;
import com.nexage.app.services.IdentityProviderService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:application-context-test.xml"})
@WebAppConfiguration
public class IdentityProviderControllerIT {

  private MockMvc mockMvc;

  @Mock private IdentityProviderService identityProviderService;

  @InjectMocks private IdentityProviderController identityProviderController;

  @BeforeEach
  void setUp() {
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(identityProviderController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
            .build();
  }

  @Test
  void getIdentityProviderData() throws Exception {
    Pageable pageRequest = PageRequest.of(0, 1);
    IdentityProviderDTO identityProviderDTO =
        IdentityProviderDTO.builder()
            .pid(2L)
            .name("LIVERAMP")
            .displayName("RampId")
            .providerId(0)
            .domain("liveramp.com")
            .version(1)
            .enabled(true)
            .uiVisible(true)
            .build();

    Page<IdentityProviderDTO> dtoPage =
        new PageImpl<>(List.of(identityProviderDTO), pageRequest, 1);
    when(identityProviderService.getAllIdentityProviders(nullable(Pageable.class)))
        .thenReturn(dtoPage);
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(URI.create("/v1/dsps/identity-providers")))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("content[0].pid").value(2))
        .andExpect(jsonPath("content[0].name").value("LIVERAMP"))
        .andExpect(jsonPath("content[0].displayName").value("RampId"))
        .andExpect(jsonPath("content[0].providerId").value(0))
        .andExpect(jsonPath("content[0].domain").value("liveramp.com"))
        .andExpect(jsonPath("content[0].version").value(1))
        .andExpect(jsonPath("content[0].enabled").value(true))
        .andExpect(jsonPath("content[0].uiVisible").value(true));
  }
}
