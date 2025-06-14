package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity.IgnoredRequestConfigurer;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@ExtendWith(MockitoExtension.class)
class GenevaCookieSessionSecurityConfigurerTest {

  private GenevaCookieSessionSecurityConfigurer configurer;

  @Mock private LogoutSuccessHandler logoutSuccessHandler;
  @Mock private AffirmativeBased affirmativeBased;
  @Mock private FilterChainExceptionHandlerFilter exceptionHandlerFilter;
  @Mock private OAuth2ClientContextFilter oAuth2ClientContextFilter;
  @Mock private OAuth2ClientAuthenticationProcessingFilter clientAuthenticationProcessingFilter;
  @Mock private UserDetailsService userDetailsService;
  @Mock private CorsConfigurationSource corsConfigurationSource;
  @Mock private WebSecurity webSecurity;

  @Mock private GenevaSecurityProperties genevaSecurityProperties;

  @BeforeEach
  void setup() {
    configurer =
        new GenevaCookieSessionSecurityConfigurer(
            logoutSuccessHandler,
            affirmativeBased,
            exceptionHandlerFilter,
            oAuth2ClientContextFilter,
            clientAuthenticationProcessingFilter,
            userDetailsService,
            corsConfigurationSource);
  }

  @Test
  void shouldConfigureWebSecurity() throws Exception {
    var ignoreRegistry = mock(IgnoredRequestConfigurer.class);
    when(webSecurity.ignoring()).thenReturn(ignoreRegistry);
    assertDoesNotThrow(() -> configurer.configure(webSecurity), "Failed to configure web security");
  }

  @Test
  void shouldConfigureHttpSecurity() {
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

    assertDoesNotThrow(
        () -> {
          when(http.cors()).thenReturn(corsConfig);
          when(corsConfig.configurationSource(any())).thenReturn(corsConfig);
          when(http.authorizeRequests()).thenReturn(expressionRegistry);
          when(http.anonymous()).thenReturn(anonymousConfig);
          when(http.headers()).thenReturn(headersConfig);
          when(http.logout()).thenReturn(logoutConfig);
          when(http.addFilterBefore(any(), any())).thenReturn(http);
          when(http.addFilterAfter(any(), any())).thenReturn(http);
          when(anonymousConfig.disable()).thenReturn(http);
          when(expressionRegistry.accessDecisionManager(any())).thenReturn(expressionRegistry);
          when(expressionRegistry.antMatchers(any(String.class))).thenReturn(authorizedUrl);
          when(authorizedUrl.permitAll()).thenReturn(expressionRegistry);
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
        },
        "Failed to configure HTTP security");
  }
}
