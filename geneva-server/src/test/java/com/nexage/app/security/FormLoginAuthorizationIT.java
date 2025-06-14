package com.nexage.app.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.app.web.support.BaseControllerItTest;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.config.GenevaFormLoginSecurityConfigurer;
import com.ssp.geneva.common.security.config.GenevaWebSecurityConfiguration;
import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@TestPropertySource(properties = {"geneva.server.login=form-login"})
class FormLoginAuthorizationIT extends BaseControllerItTest {

  @Autowired private FilterChainProxy springSecurityFilterChain;
  @Autowired private UserRepository userRepository;

  private static User createTestUser(String userName) {
    User user = new User();
    user.setPid(1L);
    user.setUserName(userName);
    user.setRole(User.Role.ROLE_USER);
    user.addCompany(createTestCompany());
    user.setEnabled(true);
    return user;
  }

  private static Company createTestCompany() {
    Company company = new Company();
    company.setPid(1L);
    company.setSelfServeAllowed(false);
    company.setType(CompanyType.SELLER);
    return company;
  }

  @BeforeEach
  public void setUp() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(wac)
            .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
            .build();
  }

  @Test
  void shouldLetUserInIgnoringPasswordValue() throws Exception {
    // given
    String userName = "user";
    when(userRepository.findByUserName(userName)).thenReturn(Optional.of(createTestUser(userName)));

    RequestBuilder requestBuilder = formLogin().user(userName).password("whatever");

    // when
    ResultActions result = mockMvc.perform(requestBuilder);

    // then
    result.andExpect(status().isOk());
  }

  @Configuration
  @Import({
    GenevaFormLoginSecurityConfigurer.class,
    GenevaWebSecurityConfiguration.class,
    RoleHierarchyVoter.class
  })
  static class TestApplicationProperties {

    @Bean
    OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter() {
      return mock(OAuth2ClientAuthenticationProcessingFilter.class);
    }

    @Bean
    OAuth2ClientContextFilter oAuth2ClientContextFilter() {
      return mock(OAuth2ClientContextFilter.class);
    }

    @Bean
    LogoutSuccessHandler logoutSuccessHandler() {
      return mock(LogoutSuccessHandler.class);
    }

    @Bean
    FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter() {
      return new FilterChainExceptionHandlerFilter(new DefaultHandlerExceptionResolver());
    }

    @Bean
    OAuth2UserService oAuth2UserService() {
      return mock(OAuth2UserService.class);
    }
  }
}
