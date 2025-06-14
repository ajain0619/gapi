package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import com.ssp.geneva.common.security.service.OidcUserService;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.cors.CorsConfigurationSource;

@ExtendWith(MockitoExtension.class)
class GenevaFormLoginSecurityConfigurerTest {

  private GenevaFormLoginSecurityConfigurer configurer;

  @Mock private LogoutSuccessHandler logoutSuccessHandler;

  @Mock private AffirmativeBased affirmativeBased;
  @Mock private FilterChainExceptionHandlerFilter exceptionHandlerFilter;
  @Mock private OAuth2ClientContextFilter oAuth2ClientContextFilter;
  @Mock private OAuth2ClientAuthenticationProcessingFilter clientAuthenticationProcessingFilter;

  @Mock private UserDetailsService userDetailsService;
  @Mock private CorsConfigurationSource corsConfigurationSource;
  @Mock private HttpSecurity http;
  @Mock private UserRepository userRepository;
  @Mock private ObjectMapper objectMapper;
  @Mock private Environment environment;
  @Mock private SysConfigUtil sysConfigUtil;
  @Mock private MessageHandler messageHandler;

  @Mock private AffirmativeBased urlAccessDecisionManager;

  @Mock ClientRegistrationRepository clientRegistrationRepository;

  @Mock OidcUserService oidcUserService;

  private GenevaSecurityProperties genevaSecurityProperties;

  @Captor ArgumentCaptor<Customizer<OAuth2LoginConfigurer<HttpSecurity>>> oauth2LoginCustomizer;

  @BeforeEach
  void setup() {
    var genevaSecurityProperties = new GenevaSecurityProperties();
    genevaSecurityProperties.setSsoOneIdBaseApplicationUri("https://test.api.yahoo.com");

    configurer =
        new GenevaFormLoginSecurityConfigurer(
            logoutSuccessHandler,
            affirmativeBased,
            exceptionHandlerFilter,
            oAuth2ClientContextFilter,
            clientAuthenticationProcessingFilter,
            userDetailsService,
            userRepository,
            objectMapper,
            environment,
            sysConfigUtil,
            messageHandler,
            corsConfigurationSource,
            urlAccessDecisionManager,
            clientRegistrationRepository,
            oidcUserService,
            genevaSecurityProperties);
  }

  @Test
  void shouldConfigureAuthenticationManager() {
    var authManager = mock(AuthenticationManagerBuilder.class);
    assertDoesNotThrow(
        () -> configurer.configure(authManager), "Failed to configure authentication manager");
  }

  @Test
  void shouldConfigureHttpSecurity() throws Exception {
    var http = mock(HttpSecurity.class);
    var expressionRegistry = mock(ExpressionInterceptUrlRegistry.class);
    var authorizedUrl = mock(AuthorizedUrl.class);
    var csrfConfig = mock(CsrfConfigurer.class);
    var sessionManagementConfig = mock(SessionManagementConfigurer.class);
    var sessionFixConfig = mock(SessionFixationConfigurer.class);
    var anonymousConfig = mock(AnonymousConfigurer.class);
    var headersConfig = mock(HeadersConfigurer.class);
    var logoutConfig = mock(LogoutConfigurer.class);
    var corsConfig = mock(CorsConfigurer.class);
    var formLoginConfig = mock(FormLoginConfigurer.class);

    when(http.formLogin()).thenReturn(formLoginConfig);
    when(formLoginConfig.loginProcessingUrl(anyString())).thenReturn(formLoginConfig);
    when(formLoginConfig.usernameParameter(anyString())).thenReturn(formLoginConfig);
    when(formLoginConfig.passwordParameter(anyString())).thenReturn(formLoginConfig);
    when(formLoginConfig.successHandler(any())).thenReturn(formLoginConfig);
    when(formLoginConfig.failureHandler(any())).thenReturn(formLoginConfig);
    when(http.cors()).thenReturn(corsConfig);
    when(corsConfig.configurationSource(any())).thenReturn(corsConfig);
    when(corsConfig.and()).thenReturn(http);
    when(http.authorizeRequests()).thenReturn(expressionRegistry);
    when(http.anonymous()).thenReturn(anonymousConfig);
    when(http.headers()).thenReturn(headersConfig);
    when(http.logout()).thenReturn(logoutConfig);
    when(anonymousConfig.disable()).thenReturn(http);
    when(expressionRegistry.accessDecisionManager(any())).thenReturn(expressionRegistry);
    when(expressionRegistry.antMatchers(any(String.class))).thenReturn(authorizedUrl);
    when(authorizedUrl.access(anyString())).thenReturn(expressionRegistry);
    when(authorizedUrl.hasAnyAuthority(anyVararg())).thenReturn(expressionRegistry);
    when(expressionRegistry.and()).thenReturn(http);
    when(http.csrf()).thenReturn(csrfConfig);
    when(csrfConfig.disable()).thenReturn(http);
    when(http.sessionManagement()).thenReturn(sessionManagementConfig);
    when(sessionManagementConfig.sessionFixation()).thenReturn(sessionFixConfig);
    when(sessionFixConfig.migrateSession()).thenReturn(sessionManagementConfig);
    when(sessionManagementConfig.and()).thenReturn(http);
    when(headersConfig.defaultsDisabled()).thenReturn(headersConfig);
    when(headersConfig.disable()).thenReturn(http);
    when(logoutConfig.logoutUrl(anyString())).thenReturn(logoutConfig);
    when(logoutConfig.deleteCookies(anyVararg())).thenReturn(logoutConfig);
    when(logoutConfig.invalidateHttpSession(true)).thenReturn(logoutConfig);
    when(logoutConfig.logoutSuccessHandler(logoutSuccessHandler)).thenReturn(logoutConfig);
    when(logoutConfig.and()).thenReturn(http);

    configurer.configure(http);
    verify(http).oauth2Login(oauth2LoginCustomizer.capture());
    var oauthLoginConfigurer = new OAuth2LoginConfigurer<HttpSecurity>();
    oauthLoginConfigurer.setBuilder(http);

    oauth2LoginCustomizer.getValue().customize(oauthLoginConfigurer);
    Assertions.assertEquals(http, oauthLoginConfigurer.and());
    Assertions.assertEquals(oauthLoginConfigurer, oauthLoginConfigurer.redirectionEndpoint().and());
  }
}
