package com.ssp.geneva.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.security.handler.LogoutSuccessHandler;
import com.ssp.geneva.common.security.service.OneCentralUserDetailsService;
import com.ssp.geneva.common.security.service.UserAuthorizationService;
import com.ssp.geneva.common.security.service.UserAuthorizationServiceImpl;
import com.ssp.geneva.common.security.service.UserDetailsServiceImpl;
import com.ssp.geneva.common.security.util.TestUserUtil;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class GenevaWebSecurityConfiguration {

  @Value("${sso.oneId.baseURL}")
  private String ssoOneIdBaseUrl;

  @Value("${sso.oneId.realm}")
  private String ssoOneIdRealm;

  @Value("${sso.oneId.baseApplicationUri}")
  private String ssoOneIdBaseApplicationUri;

  @Value("${sso.ui.base.endpoint}")
  private String ssoUiBaseEndpoint;

  @Value("${geneva.server.create.dbusers:false}")
  private boolean genevaServerTestingUserEnabled;

  @Value("${spring.session.max.inactive.timeout:1440}")
  private Integer springSessionMaxInactiveTimeout;

  @Value("${geneva.sso.oidc.client.id}")
  private String ssoClientId;

  @Value("${geneva.sso.oidc.client.secret}")
  private String ssoClientSecret;

  @Value("${geneva.sso.client.id}")
  private String genevaSsoClientId;

  @Value("${geneva.sso.client.secret}")
  private String genevaSsoClientSecret;

  @Value("${sso.oneId.scope}")
  private List<String> scope;

  @Bean("genevaSecurityProperties")
  public GenevaSecurityProperties genevaSecurityProperties() {
    return GenevaSecurityProperties.builder()
        .ssoOneIdBaseUrl(ssoOneIdBaseUrl)
        .ssoOneIdRealm(ssoOneIdRealm)
        .ssoOneIdBaseApplicationUri(ssoOneIdBaseApplicationUri)
        .ssoUiBaseEndpoint(ssoUiBaseEndpoint)
        .genevaServerTestingUserEnabled(genevaServerTestingUserEnabled)
        .springSessionMaxInactiveTimeout(springSessionMaxInactiveTimeout)
        .genevaSsoClientId(genevaSsoClientId)
        .genevaSsoClientSecret(genevaSsoClientSecret)
        .build();
  }

  @Bean("objectMapper")
  public ObjectMapper objectMapper() {
    return GenevaSecurityJacksonBeanFactory.initObjectMapper();
  }

  @Bean("simpleRestTemplate")
  public RestTemplate simpleRestTemplate() {
    return GenevaSecurityRestTemplateFactory.initRestTemplate();
  }

  @Bean("logoutSuccessHandler")
  public LogoutSuccessHandler logoutSuccessHandler() {
    String defaultUri = ssoOneIdBaseApplicationUri;
    if (ssoUiBaseEndpoint != null && !ssoUiBaseEndpoint.isEmpty()) {
      defaultUri = ssoUiBaseEndpoint;
    }
    return new LogoutSuccessHandler(ssoOneIdBaseUrl, defaultUri);
  }

  @Bean("urlAccessDecisionManager")
  public AffirmativeBased urlAccessDecisionManager(@Autowired RoleHierarchyVoter roleVoter) {
    return new AffirmativeBased(
        List.of(roleVoter, new Jsr250Voter(), new WebExpressionVoter(), new AuthenticatedVoter()));
  }

  @Bean("userAuthorizationService")
  public UserAuthorizationService userAuthorizationService(
      @Autowired OneCentralSdkClient oneCentralSdkClient) {
    return new UserAuthorizationServiceImpl(oneCentralSdkClient);
  }

  @Bean("userDetailsService")
  public OneCentralUserDetailsService userDetailsService(
      @Autowired UserRepository userRepository, @Autowired TestUserUtil testUserUtil) {
    return new UserDetailsServiceImpl(
        userRepository,
        testUserUtil,
        genevaSecurityProperties().isGenevaServerTestingUserEnabled());
  }

  @Bean("testUserUtil")
  public TestUserUtil testUserUtil(
      @Autowired UserRepository userRepository, @Autowired CompanyRepository companyRepository) {
    return new TestUserUtil(userRepository, companyRepository);
  }

  @Bean
  public ClientRegistrationRepository clientRegistrationRepository() {

    return new InMemoryClientRegistrationRepository(
        b2bClientRegistration(), s2sClientRegistration());
  }

  private ClientRegistration b2bClientRegistration() {
    return ClientRegistration.withRegistrationId("b2b")
        .clientId(ssoClientId)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri(ssoOneIdBaseApplicationUri + "/geneva/sso")
        .authorizationUri(ssoOneIdBaseUrl.concat("/identity/oauth2/authorize"))
        .tokenUri(ssoOneIdBaseUrl.concat("/identity/oauth2/access_token"))
        .scope(scope)
        .clientSecret(ssoClientSecret)
        .clientName("Yahoo-B2B")
        .userNameAttributeName("userName")
        .userInfoUri(
            ssoOneIdBaseUrl.concat("/identity/oauth2/userinfo?realm=").concat(ssoOneIdRealm))
        .jwkSetUri(
            ssoOneIdBaseUrl.concat("/identity/oauth2/connect/jwk_uri?realm=").concat(ssoOneIdRealm))
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .build();
  }

  private ClientRegistration s2sClientRegistration() {
    return ClientRegistration.withRegistrationId("s2s")
        .clientId(genevaSsoClientId)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .tokenUri(ssoOneIdBaseUrl.concat("/identity/oauth2/access_token"))
        .scope("one")
        .clientSecret(genevaSsoClientSecret)
        .clientName("Yahoo-S2S")
        .userNameAttributeName("userName")
        .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
        .build();
  }

  @Bean
  @DependsOn({"clientRegistrationRepository"})
  public OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {

    OAuth2AuthorizedClientProvider authorizedClientProvider =
        OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken().build();

    DefaultOAuth2AuthorizedClientManager authorizedClientManager =
        new DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  @Bean
  public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
    return new HttpSessionOAuth2AuthorizedClientRepository();
  }

  @Bean
  @DependsOn({"clientRegistrationRepository"})
  public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(
      ClientRegistrationRepository clientRegistrationRepository) {

    return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
  }

  @Bean
  public AuthorizedClientServiceOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
        clientRegistrationRepository, oAuth2AuthorizedClientService);
  }
}
