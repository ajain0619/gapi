package com.ssp.geneva.common.security.service;

import com.ssp.geneva.common.security.oauth2.s2s.Oauth2AuthorizedClientProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// This class is need refactoring until we move completely spring security Oauth
@Component("oidcUserService")
@Log4j2
public class OidcUserService extends Oauth2UserProvider
    implements OAuth2UserService<OidcUserRequest, OidcUser> {

  public OidcUserService(
      UserAuthorizationService userAuthorizationService,
      OneCentralUserDetailsService userDetailsService,
      Oauth2AuthorizedClientProvider clientProvider) {
    super(userAuthorizationService, userDetailsService, clientProvider);
  }

  @Override
  @Transactional
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    var accessToken = userRequest.getAccessToken().getTokenValue();
    var springUserDetails = getUserDetails(accessToken);
    var nameAttributeKey =
        userRequest
            .getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
    var oidcUserInfo =
        OidcUserInfo.builder()
            .claims(
                claim -> {
                  claim.put("springUserDetails", springUserDetails);
                  claim.put("userName", springUserDetails.getUsername());
                })
            .build();

    return new DefaultOidcUser(
        springUserDetails.getAuthorities(),
        userRequest.getIdToken(),
        oidcUserInfo,
        nameAttributeKey);
  }
}
