package com.ssp.geneva.common.security.config.oauth2;

import com.ssp.geneva.common.security.config.GenevaSecurityProperties;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.oauth2.BearerTokenIntrospector;
import com.ssp.geneva.common.security.oauth2.s2s.Oauth2AuthorizedClientProvider;
import com.ssp.geneva.common.security.service.Oauth2UserProvider;
import com.ssp.geneva.common.security.service.OneCentralUserDetailsService;
import com.ssp.geneva.common.security.service.UserAuthorizationService;
import com.ssp.geneva.common.security.service.UserInfoImplicitTokenServices;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

@Configuration
public class GenevaOauth2SecurityConfiguration {

  private final GenevaSecurityProperties genevaSecurityProperties;
  private final OAuth2RestTemplate s2sTemplate;

  @Value("${sso.oneId.realm}")
  private String ssoOneIdRealm;

  public GenevaOauth2SecurityConfiguration(
      GenevaSecurityProperties genevaSecurityProperties,
      @Qualifier("s2sTemplate") OAuth2RestTemplate s2sTemplate) {
    this.genevaSecurityProperties = genevaSecurityProperties;
    this.s2sTemplate = s2sTemplate;
  }

  @Bean
  public BearerAuthenticationRequestMatcher apiRequestMatcher() {
    return new BearerAuthenticationRequestMatcher();
  }

  @Bean("roleExprHandler")
  public DefaultWebSecurityExpressionHandler roleExpressionHandler(
      @Qualifier("roleHierarchy") RoleHierarchy roleHierarchy) {
    var handler = new OAuth2WebSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
  }

  @Bean
  public AffirmativeBased apiAccessDecisionManager(
      @Qualifier("roleExprHandler") SecurityExpressionHandler handler) {
    var webExpressionVoter = new WebExpressionVoter();
    webExpressionVoter.setExpressionHandler(handler);
    var voters = List.of(new AuthenticatedVoter(), webExpressionVoter, new Jsr250Voter());
    return new AffirmativeBased(voters);
  }

  @Bean("apiOauthAccessDeniedHandler")
  public OAuth2AccessDeniedHandler accessDeniedHandler() {
    return new OAuth2AccessDeniedHandler();
  }

  @Bean("apiOauthAuthenticationEntryPoint")
  public OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint() {
    var entryPoint = new OAuth2AuthenticationEntryPoint();
    entryPoint.setRealmName(ssoOneIdRealm);
    return entryPoint;
  }

  @Bean
  public UserInfoImplicitTokenServices apiTokenServices(
      OneCentralUserDetailsService userDetailsService,
      UserAuthorizationService userAuthorizationService) {
    return new UserInfoImplicitTokenServices(
        userDetailsService, userAuthorizationService, true, s2sTemplate);
  }

  @Bean("apiAuthenticationManager")
  public OAuth2AuthenticationManager oAuth2AuthenticationManager(
      @Qualifier("apiTokenServices") UserInfoImplicitTokenServices tokenServices) {
    var authManager = new OAuth2AuthenticationManager();
    authManager.setTokenServices(tokenServices);
    return authManager;
  }

  @Bean("apiOAuth2ImplicitAuthenticationProcessingFilter")
  public OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter(
      @Qualifier("apiAuthenticationManager") OAuth2AuthenticationManager authManager,
      @Qualifier("apiOauthAuthenticationEntryPoint") OAuth2AuthenticationEntryPoint entryPoint) {
    var filter = new OAuth2AuthenticationProcessingFilter();
    filter.setAuthenticationManager(authManager);
    filter.setAuthenticationEntryPoint(entryPoint);
    filter.setStateless(false);

    return filter;
  }

  @Bean
  public Oauth2UserProvider oauth2UserProvider(
      UserAuthorizationService userAuthorizationService,
      OneCentralUserDetailsService userDetailsService,
      Oauth2AuthorizedClientProvider clientProvider) {
    return new Oauth2UserProvider(userAuthorizationService, userDetailsService, clientProvider);
  }

  @Bean
  @DependsOn("genevaSecurityProperties")
  public BearerTokenIntrospector getOpaqueTokenIntrospector(
      GenevaSecurityProperties genevaSecurityProperties, Oauth2UserProvider oauth2UserProvider) {
    return new BearerTokenIntrospector(
        genevaSecurityProperties.getSsoOneIdBaseUrl().concat("/identity/oauth2/introspect"),
        genevaSecurityProperties.getGenevaSsoClientId(),
        genevaSecurityProperties.getGenevaSsoClientSecret(),
        oauth2UserProvider);
  }

  @Bean
  @DependsOn("genevaSecurityProperties")
  public BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint(
      GenevaSecurityProperties genevaSecurityProperties) {
    var bearerTokenAuthenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
    bearerTokenAuthenticationEntryPoint.setRealmName(genevaSecurityProperties.getSsoOneIdRealm());
    return bearerTokenAuthenticationEntryPoint;
  }
}
