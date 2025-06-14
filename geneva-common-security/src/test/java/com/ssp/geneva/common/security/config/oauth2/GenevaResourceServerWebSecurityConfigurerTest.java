package com.ssp.geneva.common.security.config.oauth2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.oauth2.BearerTokenIntrospector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;

@ExtendWith(MockitoExtension.class)
class GenevaResourceServerWebSecurityConfigurerTest {

  private GenevaResourceServerWebSecurityConfigurer configurer;

  @Mock private BearerAuthenticationRequestMatcher requestMatcher;

  @Mock private FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;

  @Mock private AccessDecisionManager accessDecisionManager;

  @Mock private BearerTokenIntrospector bearerTokenIntrospector;

  @Mock private BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint;

  @BeforeEach
  void setup() {
    configurer =
        new GenevaResourceServerWebSecurityConfigurer(
            requestMatcher,
            filterChainExceptionHandlerFilter,
            accessDecisionManager,
            bearerTokenIntrospector,
            bearerTokenAuthenticationEntryPoint);
  }

  @Test
  void shouldConfigureHttpSecurity() throws Exception {
    var http = mock(HttpSecurity.class);
    var expressionRegistry = mock(ExpressionInterceptUrlRegistry.class);
    var authorizedUrl = mock(AuthorizedUrl.class);
    var csrfConfig = mock(CsrfConfigurer.class);
    var sessionManagementConfig = mock(SessionManagementConfigurer.class);
    var anonymousConfig = mock(AnonymousConfigurer.class);
    var exceptionHandlingConfigurer = mock(ExceptionHandlingConfigurer.class);

    when(http.requestMatcher(any())).thenReturn(http);
    when(http.exceptionHandling()).thenReturn(exceptionHandlingConfigurer);
    when(exceptionHandlingConfigurer.authenticationEntryPoint(any()))
        .thenReturn(exceptionHandlingConfigurer);
    when(exceptionHandlingConfigurer.and()).thenReturn(http);
    when(http.authorizeRequests()).thenReturn(expressionRegistry);
    when(http.anonymous()).thenReturn(anonymousConfig);
    when(http.addFilterBefore(any(), any())).thenReturn(http);
    when(anonymousConfig.disable()).thenReturn(http);
    when(expressionRegistry.accessDecisionManager(any())).thenReturn(expressionRegistry);
    when(expressionRegistry.antMatchers(any(String.class))).thenReturn(authorizedUrl);
    when(authorizedUrl.access(anyString())).thenReturn(expressionRegistry);
    when(authorizedUrl.denyAll()).thenReturn(expressionRegistry);
    when(expressionRegistry.and()).thenReturn(http);
    when(http.csrf()).thenReturn(csrfConfig);
    when(csrfConfig.disable()).thenReturn(http);
    when(http.sessionManagement()).thenReturn(sessionManagementConfig);
    when(sessionManagementConfig.sessionCreationPolicy(any())).thenReturn(sessionManagementConfig);
    when(sessionManagementConfig.and()).thenReturn(http);
    assertDoesNotThrow(() -> configurer.configure(http));
  }
}
