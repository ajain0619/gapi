package com.ssp.geneva.common.security.config.oauth2;

import com.ssp.geneva.common.security.config.configurer.Customization;
import com.ssp.geneva.common.security.filter.FilterChainExceptionHandlerFilter;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.oauth2.BearerTokenIntrospector;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@ConditionalOnExpression("'${geneva.server.login}'.matches('oauth2|form-login\')")
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GenevaResourceServerWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final BearerAuthenticationRequestMatcher requestMatcher;

  private final FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter;

  private final AccessDecisionManager accessDecisionManager;

  private final BearerTokenIntrospector bearerTokenIntrospector;

  private final BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint;

  @Value("${sso.oneId.realm}")
  private String ssoOneIdRealm;

  public GenevaResourceServerWebSecurityConfigurer(
      BearerAuthenticationRequestMatcher requestMatcher,
      FilterChainExceptionHandlerFilter filterChainExceptionHandlerFilter,
      @Qualifier("apiAccessDecisionManager") AccessDecisionManager accessDecisionManager,
      BearerTokenIntrospector bearerTokenIntrospector,
      BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint) {
    this.requestMatcher = requestMatcher;
    this.filterChainExceptionHandlerFilter = filterChainExceptionHandlerFilter;
    this.accessDecisionManager = accessDecisionManager;
    this.bearerTokenIntrospector = bearerTokenIntrospector;
    this.bearerTokenAuthenticationEntryPoint = bearerTokenAuthenticationEntryPoint;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.requestMatcher(requestMatcher)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(bearerTokenAuthenticationEntryPoint)
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
        .denyAll()
        .and()
        .addFilterBefore(filterChainExceptionHandlerFilter, ExceptionTranslationFilter.class)
        .oauth2ResourceServer(
            Customization.oAuth2ResourceServerCustomizer(bearerTokenIntrospector, ssoOneIdRealm));
  }
}
