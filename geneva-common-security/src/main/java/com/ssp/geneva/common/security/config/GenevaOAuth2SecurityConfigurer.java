package com.ssp.geneva.common.security.config;

import static org.springframework.util.StringUtils.hasText;

import com.ssp.geneva.common.security.config.configurer.Customization;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.cors.CorsConfigurationSource;

/*
 * This is class may be intermediate configuration transition for Spring Security OAuth 2.x to Spring Security 5.2.x.
 * */
@ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "oauth2")
@Configuration
public class GenevaOAuth2SecurityConfigurer extends WebSecurityConfigurerAdapter {

  private final LogoutSuccessHandler logoutSuccessHandler;
  public final ClientRegistrationRepository clientRegistrationRepository;

  private final OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

  private final GenevaSecurityProperties genevaSecurityProperties;

  private final AffirmativeBased urlAccessDecisionManager;

  private final CorsConfigurationSource corsConfigurationSource;

  @Value("${sso.oneId.realm}")
  private String ssoOneIdRealm;

  public GenevaOAuth2SecurityConfigurer(
      LogoutSuccessHandler logoutSuccessHandler,
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService,
      GenevaSecurityProperties genevaSecurityProperties,
      AffirmativeBased urlAccessDecisionManager,
      CorsConfigurationSource corsConfigurationSource) {
    this.logoutSuccessHandler = logoutSuccessHandler;
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.oidcUserService = oidcUserService;
    this.genevaSecurityProperties = genevaSecurityProperties;
    this.urlAccessDecisionManager = urlAccessDecisionManager;
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
    http.authorizeRequests()
        .accessDecisionManager(urlAccessDecisionManager)
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
        .cors()
        .configurationSource(corsConfigurationSource)
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
        .oauth2Login(
            loginConfigurer -> {
              var uiBaseEndpoint = genevaSecurityProperties.getSsoUiBaseEndpoint();
              var successEndpoint =
                  hasText(uiBaseEndpoint)
                      ? uiBaseEndpoint + "/#/?login=success"
                      : genevaSecurityProperties.getSsoOneIdBaseApplicationUri()
                          + "/geneva/actuator/info";

              var failureEndpoint =
                  hasText(uiBaseEndpoint)
                      ? uiBaseEndpoint + "/#/?login=failed"
                      : genevaSecurityProperties.getSsoOneIdBaseApplicationUri()
                          + "/geneva/unauthorized";

              loginConfigurer
                  .loginPage(
                      genevaSecurityProperties.getSsoOneIdBaseApplicationUri()
                          + "/geneva"
                          + OAuth2AuthorizationRequestRedirectFilter
                              .DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
                          + "/b2b")
                  .failureUrl(failureEndpoint)
                  .defaultSuccessUrl(successEndpoint)
                  .loginProcessingUrl("/sso")
                  .authorizedClientRepository(new HttpSessionOAuth2AuthorizedClientRepository())
                  .authorizationEndpoint(
                      Customization.authorizationEndpointConfigCustomizer(
                          clientRegistrationRepository, ssoOneIdRealm))
                  .tokenEndpoint(Customization.tokenEndpointConfigCustomizer(ssoOneIdRealm))
                  .userInfoEndpoint()
                  .oidcUserService(oidcUserService);
            });
  }
}
