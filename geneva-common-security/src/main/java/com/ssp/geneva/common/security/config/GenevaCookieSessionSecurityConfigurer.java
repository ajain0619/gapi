package com.ssp.geneva.common.security.config;

import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.cors.CorsConfigurationSource;

@ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "sso")
@Configuration
@Order(1000)
public class GenevaCookieSessionSecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final LogoutSuccessHandler logoutSuccessHandler;
  private final AffirmativeBased urlAccessDesisionManager;
  private final FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;
  private final OAuth2ClientContextFilter oAuth2ClientContextFilter;
  private final OAuth2ClientAuthenticationProcessingFilter
      oAuth2ClientAuthenticationProcessingFilter;
  private final UserDetailsService userDetailsService;
  private final CorsConfigurationSource corsConfigurationSource;

  public GenevaCookieSessionSecurityConfigurer(
      LogoutSuccessHandler logoutSuccessHandler,
      @Qualifier("urlAccessDecisionManager") AffirmativeBased urlAccessDesisionManager,
      FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter,
      OAuth2ClientContextFilter oAuth2ClientContextFilter,
      @Qualifier("oAuth2ClientAuthenticationProcessingFilter")
          OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter,
      UserDetailsService userDetailsService,
      CorsConfigurationSource corsConfigurationSource) {
    this.logoutSuccessHandler = logoutSuccessHandler;
    this.urlAccessDesisionManager = urlAccessDesisionManager;
    this.filterChainExceptionHandlerFilter = filterChainExceptionHandlerFilter;
    this.oAuth2ClientContextFilter = oAuth2ClientContextFilter;
    this.oAuth2ClientAuthenticationProcessingFilter = oAuth2ClientAuthenticationProcessingFilter;
    this.userDetailsService = userDetailsService;
    this.corsConfigurationSource = corsConfigurationSource;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring()
        .antMatchers(
            "/dealCacheRefreshAll",
            "/healthCheck",
            "/awsHealthCheck",
            "/marklog",
            "/unauthorized",
            "/v1/sellers/*/creatives/*");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().configurationSource(corsConfigurationSource);
    http.authorizeRequests()
        .accessDecisionManager(urlAccessDesisionManager)
        .antMatchers("/sso")
        .permitAll()
        .antMatchers("/profile")
        .access("isAuthenticated()")
        .antMatchers("/deals/suppliers", "/actuator/**")
        .hasAnyAuthority("ROLE_ADMIN_NEXAGE", "ROLE_MANAGER_NEXAGE", "ROLE_USER_NEXAGE")
        .antMatchers("/**")
        .hasAnyAuthority(
            "ROLE_USER_NEXAGE",
            "ROLE_MANAGER_NEXAGE",
            "ROLE_MANAGER_YIELD_NEXAGE",
            "ROLE_MANAGER_SMARTEX_NEXAGE",
            "ROLE_ADMIN_NEXAGE",
            "ROLE_USER_SEATHOLDER",
            "ROLE_MANAGER_SEATHOLDER",
            "ROLE_ADMIN_SEATHOLDER",
            "ROLE_USER_SELLER",
            "ROLE_MANAGER_SELLER",
            "ROLE_ADMIN_SELLER",
            "ROLE_USER_BUYER",
            "ROLE_MANAGER_BUYER",
            "ROLE_ADMIN_BUYER",
            "ROLE_AD_REVIEWER_SELLER",
            "ROLE_AD_REVIEWER_NEXAGE")
        .and()
        .csrf()
        .disable()
        .anonymous()
        .disable()
        .sessionManagement()
        .sessionFixation()
        .migrateSession()
        .and()
        .headers()
        .defaultsDisabled()
        .disable()
        .logout()
        .logoutUrl("/logout")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true)
        .logoutSuccessHandler(logoutSuccessHandler)
        .and()
        .addFilterBefore(filterChainExceptionHandlerFilter, ExceptionTranslationFilter.class)
        .addFilterAfter(oAuth2ClientContextFilter, ExceptionTranslationFilter.class)
        .addFilterBefore(
            oAuth2ClientAuthenticationProcessingFilter, FilterSecurityInterceptor.class);
  }
}
