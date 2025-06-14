package com.ssp.geneva.common.security.config.oauth2;

import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.service.UserInfoImplicitTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration("api-resourceServerFilter")
@EnableResourceServer
@Order(1001)
@ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "sso")
public class GenevaApiWebSecurityConfigurer extends ResourceServerConfigurerAdapter {

  private static final String RESOURCE_ID = "api-resource";

  @Autowired
  @Qualifier("apiTokenServices")
  private UserInfoImplicitTokenServices tokenServices;

  @Autowired
  @Qualifier("apiOauthAuthenticationEntryPoint")
  private OAuth2AuthenticationEntryPoint authenticationEntryPoint;

  @Autowired
  @Qualifier("apiRequestMatcher")
  private BearerAuthenticationRequestMatcher requestMatcher;

  @Autowired
  @Qualifier("apiAccessDecisionManager")
  private AccessDecisionManager accessDecisionManager;

  @Autowired
  @Qualifier("apiOauthAccessDeniedHandler")
  private OAuth2AccessDeniedHandler accessDeniedHandler;

  @Autowired private OAuth2ClientContextFilter oAuth2ClientContextFilter;
  @Autowired private FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;

  @Autowired
  @Qualifier("apiOAuth2ImplicitAuthenticationProcessingFilter")
  private OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter;

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.requestMatcher(requestMatcher)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntryPoint)
        .accessDeniedHandler(accessDeniedHandler)
        .and()
        .anonymous()
        .disable()
        .csrf()
        .disable()
        .authorizeRequests()
        .accessDecisionManager(accessDecisionManager)
        .antMatchers(
            "/pss/*/site",
            "/pss/*/site/*",
            "/pss/*/site/*/position",
            "/pss/*/site/*/position/*",
            "/v1/sellers/*/rules",
            "/v1/sellers/*/rules/*",
            "/v1/placements/*/rules",
            "/v1/placements/*/rules/*",
            "/v1/rules",
            "/v1/rules/*",
            "/v1/sellers/*/screens")
        .access("@loginUserContext.isOcApiSeller()")
        .antMatchers("/v1/sellers/*/sites")
        .access("@loginUserContext.isOcManagerNexage()")
        .antMatchers("/v1/sellers/*/sites/*/placements", "/v1/sellers/*/sites/*/placements/*")
        .access("@loginUserContext.isOcApiSeller() or @loginUserContext.isOcManagerNexage()")
        .antMatchers("/v1/users/**")
        .access(
            "@loginUserContext.isOcApiIIQ() or "
                + "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() "
                + "or @loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
        .antMatchers("/v1/sessions")
        .access("@loginUserContext.isNexageAdmin()")
        .antMatchers("/**")
        .denyAll();

    http.addFilterBefore(filterChainExceptionHandlerFilter, ExceptionTranslationFilter.class);
    http.addFilterAfter(oAuth2ClientContextFilter, ExceptionTranslationFilter.class);
    http.addFilterBefore(oAuth2AuthenticationProcessingFilter, FilterSecurityInterceptor.class);
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(RESOURCE_ID).stateless(false).tokenServices(tokenServices);
  }
}
