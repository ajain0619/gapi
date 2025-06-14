package com.ssp.geneva.common.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import java.util.Collection;
import java.util.Map;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/** {@link ResourceServerTokenServices} that uses a user info REST service. */
@Log4j2
public class UserInfoImplicitTokenServices implements ResourceServerTokenServices {

  private static final String USER_STATUS_ACTIVE = "ACTIVE";

  private OneCentralUserDetailsService userDetailsService;
  private boolean bearerAuthentication;
  @Setter UserAuthorizationService userAuthorizationService;
  private final OAuth2RestTemplate s2sTemplate;

  public UserInfoImplicitTokenServices(
      OneCentralUserDetailsService userDetailsService,
      UserAuthorizationService userAuthorizationService,
      boolean bearerAuthentication,
      OAuth2RestTemplate s2sTemplate) {
    this.userDetailsService = userDetailsService;
    this.userAuthorizationService = userAuthorizationService;
    this.bearerAuthentication = bearerAuthentication;
    this.s2sTemplate = s2sTemplate;
  }

  /**
   * This method is meant for authentication purposes. Besides, temporally it is performing a user
   * migration to add expected roles. Due to the nature of the call to perform such upgrade, a
   * server 2 server token is required because the 1C operation is not accepting users access token.
   *
   * @param accessToken a valid oidc access token.
   * @return {@link OAuth2Authentication} an OAuth 2 authentication token
   * @throws AuthenticationException
   * @throws InvalidTokenException
   */
  public OAuth2Authentication loadAuthentication(String accessToken)
      throws AuthenticationException, InvalidTokenException {

    Map<String, Object> auth;
    auth = getAuthByToken(accessToken);

    try {
      var userAuth = userDetailsService.loadUserBy1CUsername(auth, bearerAuthentication);
      if (!userAuth.getUser().isMigratedOneCentral()) {
        migrateUser(auth, userAuth.getUser());
        auth = getAuthByToken(accessToken);
      }
    } catch (Exception e) {
      log.error("Unable to assign role in OneCentral. Error: {}", e.getMessage());
    }

    return extractAuthentication(auth);
  }

  private void migrateUser(Map<String, Object> auth, User user) {
    var s2sAccessToken = s2sTemplate.getAccessToken().getValue();
    var userDetails = userDetailsService.loadUserDetailsBy1CUsername(auth, bearerAuthentication);
    log.debug("userDetails={}", userDetails);
    for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
      userAuthorizationService.assignRole(
          s2sAccessToken, grantedAuthority.getAuthority(), user.getOneCentralUserName());
      userDetailsService.updateUserMigrated(user.getPid());
    }
  }

  private Map<String, Object> getAuthByToken(String accessToken) {
    OneCentralUserAuthResponse authResponse =
        userAuthorizationService.getUserAuthorization(accessToken).getBody();
    var oMapper = new ObjectMapper();
    Map<String, Object> auth = oMapper.convertValue(authResponse, Map.class);
    if (!USER_STATUS_ACTIVE.equals(auth.get("status"))) {
      log.debug("user-authorization returned status: {}", auth.get("status"));
      throw new InvalidTokenException(accessToken);
    }
    return auth;
  }

  private OAuth2Authentication extractAuthentication(Map<String, Object> map) {

    var userDetails = userDetailsService.loadUserDetailsBy1CUsername(map, bearerAuthentication);

    Collection<GrantedAuthority> authorities =
        (Collection<GrantedAuthority>) userDetails.getAuthorities();

    OAuth2Request request = new OAuth2Request(null, null, null, true, null, null, null, null, null);

    UsernamePasswordAuthenticationToken token =
        new UsernamePasswordAuthenticationToken(userDetails, "N/A", authorities);

    return new OAuth2Authentication(request, token);
  }

  @Override
  public OAuth2AccessToken readAccessToken(String accessToken) {
    throw new UnsupportedOperationException("Not supported: read access token");
  }

  public void setUserDetailsService(OneCentralUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
}
