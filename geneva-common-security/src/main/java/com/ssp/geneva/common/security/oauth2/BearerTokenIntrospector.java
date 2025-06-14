package com.ssp.geneva.common.security.oauth2;

import com.ssp.geneva.common.security.service.Oauth2UserProvider;
import java.util.Collection;
import java.util.HashMap;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.web.client.RestOperations;

public class BearerTokenIntrospector extends SpringOpaqueTokenIntrospector {
  @Getter private String introspectionUri;
  private final Oauth2UserProvider oauth2UserProvider;

  public BearerTokenIntrospector(
      String introspectionUri,
      String clientId,
      String clientSecret,
      Oauth2UserProvider oauth2UserProvider) {
    super(introspectionUri, clientId, clientSecret);
    this.introspectionUri = introspectionUri;
    this.oauth2UserProvider = oauth2UserProvider;
  }

  public BearerTokenIntrospector(
      String introspectionUri,
      RestOperations restOperations,
      Oauth2UserProvider oauth2UserProvider) {
    super(introspectionUri, restOperations);

    this.oauth2UserProvider = oauth2UserProvider;
  }

  @Override
  public OAuth2AuthenticatedPrincipal introspect(String token) {
    var oAuth2AuthenticatedPrincipal = super.introspect(token);
    var attributes = new HashMap<>(oAuth2AuthenticatedPrincipal.getAttributes());
    var springUserDetails = oauth2UserProvider.getUserDetails(token);
    attributes.put("springUserDetails", springUserDetails);

    return new DefaultOAuth2AuthenticatedPrincipal(
        springUserDetails.getUsername(),
        attributes,
        (Collection<GrantedAuthority>) oAuth2AuthenticatedPrincipal.getAuthorities());
  }
}
