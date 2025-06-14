package com.ssp.geneva.common.security.config.oauth2;

import com.ssp.geneva.common.security.config.GenevaSecurityProperties;
import com.ssp.geneva.common.security.handler.sso.SsoAuthenticationFailureHandler;
import com.ssp.geneva.common.security.handler.sso.SsoAuthenticationSuccessHandler;
import com.ssp.geneva.common.security.oauth2.AuthorizationCodeAccessTokenProvider;
import com.ssp.geneva.common.security.oauth2.JwtClientAuthenticationHandler;
import com.ssp.geneva.common.security.oauth2.oidc.OidcClientCredentialsResourceFactory;
import com.ssp.geneva.common.security.oauth2.oidc.OpenIdFileSystemCredentialsConfig;
import com.ssp.geneva.common.security.oauth2.s2s.Oauth2ClientCredentialsResourceFactory;
import com.ssp.geneva.common.security.service.OneCentralUserDetailsService;
import com.ssp.geneva.common.security.service.UserAuthorizationService;
import com.ssp.geneva.common.security.service.UserInfoImplicitTokenServices;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
public class GenevaOAuth2ClientConfig {

  private final GenevaSecurityProperties genevaSecurityProperties;
  private final DataSource dataSource;
  private final OAuth2ClientContext oAuth2ClientContext;
  private final UserAuthorizationService userAuthorizationService;
  private final OneCentralUserDetailsService userDetailsService;

  public GenevaOAuth2ClientConfig(
      @Qualifier("genevaSecurityProperties") GenevaSecurityProperties genevaSecurityProperties,
      @Qualifier("coreDS") DataSource dataSource,
      OAuth2ClientContext oAuth2ClientContext,
      UserAuthorizationService userAuthorizationService,
      OneCentralUserDetailsService userDetailsService) {
    this.genevaSecurityProperties = genevaSecurityProperties;
    this.dataSource = dataSource;
    this.oAuth2ClientContext = oAuth2ClientContext;
    this.userAuthorizationService = userAuthorizationService;
    this.userDetailsService = userDetailsService;
  }

  @Bean("oauth2ClientContextFilter")
  @ConditionalOnProperty(prefix = "geneva.server", name = "login", havingValue = "sso")
  public OAuth2ClientContextFilter oauth2ClientContextFilter() {
    return new OAuth2ClientContextFilter();
  }

  @Bean("restTemplate")
  public OAuth2RestTemplate restTemplate() {
    var template = new OAuth2RestTemplate(oidcResource(), oAuth2ClientContext);
    template.setAccessTokenProvider(accessTokenProvider());
    return template;
  }

  @Bean("s2sTemplate")
  public OAuth2RestTemplate s2sTemplate() {
    var template = new OAuth2RestTemplate(s2sResource());
    template.setAccessTokenProvider(s2sTokenProvider());
    return template;
  }

  @Bean("oAuth2ClientAuthenticationProcessingFilter")
  @ConditionalOnExpression("'${geneva.server.login}'.matches('sso|form-login')")
  public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter(
      @Qualifier("restTemplate") OAuth2RestTemplate restTemplate) {
    var filter = new OAuth2ClientAuthenticationProcessingFilter("/sso");
    filter.setRestTemplate(restTemplate());
    filter.setTokenServices(tokenServices());
    var uiBaseEndpoint = genevaSecurityProperties.getSsoUiBaseEndpoint();
    var successEndpoint =
        uiBaseEndpoint != null && uiBaseEndpoint.trim().length() > 0
            ? uiBaseEndpoint + "/#/?login=success"
            : genevaSecurityProperties.getSsoOneIdBaseApplicationUri() + "/geneva/actuator/info";
    var successHandler = new SsoAuthenticationSuccessHandler(successEndpoint);
    filter.setAuthenticationSuccessHandler(successHandler);

    var failureEndpoint =
        uiBaseEndpoint != null && uiBaseEndpoint.trim().length() > 0
            ? uiBaseEndpoint + "/#/?login=failed"
            : genevaSecurityProperties.getSsoOneIdBaseApplicationUri() + "/geneva/unauthorized";
    filter.setAuthenticationFailureHandler(new SsoAuthenticationFailureHandler(failureEndpoint));
    return filter;
  }

  @Bean("oauth2ClientAuthenticationHandler")
  public JwtClientAuthenticationHandler oauth2ClientAuthenticationHandler() {
    return new JwtClientAuthenticationHandler();
  }

  @Bean("accessTokenProvider")
  public AuthorizationCodeAccessTokenProvider accessTokenProvider() {
    var provider =
        new AuthorizationCodeAccessTokenProvider(genevaSecurityProperties.getSsoOneIdRealm());
    provider.setAuthenticationHandler(oauth2ClientAuthenticationHandler());
    return provider;
  }

  @Bean("openIdConnectConfigHandler")
  public OpenIdFileSystemCredentialsConfig openIdConnectConfigHandler() {
    return new OpenIdFileSystemCredentialsConfig();
  }

  @Bean("oidcClientCredentialsResourceFactory")
  public OidcClientCredentialsResourceFactory oidcClientCredentialsResourceFactory() {
    return new OidcClientCredentialsResourceFactory(
        openIdConnectConfigHandler(),
        genevaSecurityProperties.getSsoOneIdBaseUrl(),
        genevaSecurityProperties.getSsoOneIdRealm(),
        genevaSecurityProperties.getSsoOneIdBaseApplicationUri());
  }

  @Bean("oidcResource")
  public AuthorizationCodeResourceDetails oidcResource() {
    return oidcClientCredentialsResourceFactory().createAuthorizationCodeResourceDetails();
  }

  // S2S resource related

  @Bean("coreNamedJdbcTemplate")
  public NamedParameterJdbcTemplate coreNamedJdbcTemplate() {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Bean("oauth2ClientCredentialsResource")
  public Oauth2ClientCredentialsResourceFactory oauth2ClientCredentialsResource() {
    var factory =
        new Oauth2ClientCredentialsResourceFactory(
            genevaSecurityProperties.getSsoOneIdBaseUrl(),
            genevaSecurityProperties.getSsoOneIdRealm(),
            genevaSecurityProperties.getGenevaSsoClientId(),
            genevaSecurityProperties.getGenevaSsoClientSecret());
    factory.setScope(List.of("one"));
    return factory;
  }

  @Bean("s2sResource")
  public ClientCredentialsResourceDetails s2sResource() {
    return oauth2ClientCredentialsResource().createClientCredentialsResourceDetails();
  }

  @Bean("s2sTokenProvider")
  public ClientCredentialsAccessTokenProvider s2sTokenProvider() {
    var provider = new ClientCredentialsAccessTokenProvider();
    provider.setAuthenticationHandler(oauth2ClientAuthenticationHandler());
    return provider;
  }

  @Primary
  @Bean("tokenServices")
  public UserInfoImplicitTokenServices tokenServices() {
    return new UserInfoImplicitTokenServices(
        userDetailsService, userAuthorizationService, false, s2sTemplate());
  }
}
