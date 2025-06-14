package com.ssp.geneva.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.error.handler.MessageHandler;
import com.ssp.geneva.common.security.auth.NoPasswordDaoAuthenticationProvider;
import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import com.ssp.geneva.common.security.handler.login.LoginAuthenticationEntryPoint;
import com.ssp.geneva.common.security.handler.login.LoginAuthenticationFailureHandler;
import com.ssp.geneva.common.security.handler.login.LoginAuthenticationSuccessHandler;
import com.ssp.geneva.common.security.util.login.LoginEntitlementCorrectionHandler;
import com.ssp.geneva.common.settings.util.SysConfigUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.cors.CorsConfigurationSource;

@ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "form-login")
@Configuration
@Order(1000)
public class GenevaFormLoginSecurityConfigurer extends GenevaOAuth2SecurityConfigurer {

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final Environment environment;
  private final SysConfigUtil sysConfigUtil;
  private final MessageHandler messageHandler;
  private final UserDetailsService userDetailsService;

  public GenevaFormLoginSecurityConfigurer(
      LogoutSuccessHandler logoutSuccessHandler,
      @Qualifier("urlAccessDecisionManager") AffirmativeBased urlAccessDesisionManager,
      FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter,
      OAuth2ClientContextFilter oAuth2ClientContextFilter,
      @Qualifier("oAuth2ClientAuthenticationProcessingFilter")
          OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter,
      UserDetailsService userDetailsService,
      UserRepository userRepository,
      ObjectMapper objectMapper,
      Environment environment,
      SysConfigUtil sysConfigUtil,
      MessageHandler messageHandler,
      CorsConfigurationSource corsConfigurationSource,
      AffirmativeBased urlAccessDecisionManager,
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService,
      GenevaSecurityProperties genevaSecurityProperties) {

    super(
        logoutSuccessHandler,
        clientRegistrationRepository,
        oidcUserService,
        genevaSecurityProperties,
        urlAccessDecisionManager,
        corsConfigurationSource);

    this.userRepository = userRepository;
    this.objectMapper = objectMapper;
    this.environment = environment;
    this.sysConfigUtil = sysConfigUtil;
    this.messageHandler = messageHandler;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    var provider = new NoPasswordDaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    auth.authenticationProvider(provider);
  }

  @Bean("loginEntitlementCorrectionHandler")
  public LoginEntitlementCorrectionHandler loginEntitlementCorrectionHandler() {
    return new LoginEntitlementCorrectionHandler(userRepository, objectMapper);
  }

  @Bean("loginAuthenticationSuccessHandler")
  public LoginAuthenticationSuccessHandler loginAuthenticationSuccessHandler() {
    return new LoginAuthenticationSuccessHandler(loginEntitlementCorrectionHandler());
  }

  @Bean("loginAuthenticationFailureHandler")
  public LoginAuthenticationFailureHandler loginAuthenticationFailureHandler() {
    return new LoginAuthenticationFailureHandler(
        objectMapper, messageHandler, sysConfigUtil, environment);
  }

  @Bean("loginAuthenticationEntryPoint")
  public LoginAuthenticationEntryPoint loginAuthenticationEntryPoint() {
    return new LoginAuthenticationEntryPoint(objectMapper, messageHandler, sysConfigUtil);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/login").permitAll();
    http.formLogin()
        .loginProcessingUrl("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .successHandler(loginAuthenticationSuccessHandler())
        .failureHandler(loginAuthenticationFailureHandler());
    super.configure(http);
  }
}
