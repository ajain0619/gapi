package com.ssp.geneva.common.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.model.User;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.matcher.BearerAuthenticationRequestMatcher;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.common.security.model.UserAuth;
import com.ssp.geneva.common.security.oauth2.s2s.Oauth2AuthorizedClientProvider;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.Entitlement;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserRolesResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Log4j2
public class Oauth2UserProvider {
  private static final String USER_STATUS_ACTIVE = "ACTIVE";

  @Setter protected final UserAuthorizationService userAuthorizationService;

  private final OneCentralUserDetailsService userDetailsService;

  private final Oauth2AuthorizedClientProvider clientProvider;

  private BearerAuthenticationRequestMatcher bearerAuthenticationRequestMatcher =
      new BearerAuthenticationRequestMatcher();

  public Oauth2UserProvider(
      UserAuthorizationService userAuthorizationService,
      OneCentralUserDetailsService userDetailsService,
      Oauth2AuthorizedClientProvider clientProvider) {
    this.userAuthorizationService = userAuthorizationService;
    this.userDetailsService = userDetailsService;
    this.clientProvider = clientProvider;
  }

  @Transactional
  public SpringUserDetails getUserDetails(String accessToken) {
    var bearerAuthentication = bearerAuthenticationRequestMatcher.matches(getRequest());
    var auth = getAuthByToken(accessToken);
    var userAuth = getUserAuth(auth, bearerAuthentication);
    userAuth = attemptMigration(userAuth, auth, bearerAuthentication);
    return new SpringUserDetails(userAuth);
  }

  private UserAuth getUserAuth(Map<String, Object> auth, boolean bearerAuthentication) {
    UserAuth userAuth;
    try {
      userAuth = userDetailsService.loadUserBy1CUsername(auth, bearerAuthentication);
    } catch (Exception e) {
      throw new AuthenticationServiceException(e.getMessage());
    }
    return userAuth;
  }

  private Map<String, Object> getAuthByToken(String accessToken) {
    Map<String, Object> auth;
    try {
      OneCentralUserAuthResponse authResponse =
          userAuthorizationService.getUserAuthorization(accessToken).getBody();
      var oMapper = new ObjectMapper();
      auth = oMapper.convertValue(authResponse, Map.class);
      if (!USER_STATUS_ACTIVE.equals(auth.get("status"))) {
        log.debug("user-authorization returned status: {}", auth.get("status"));
        throw new OneCentralException(
            OneCentralErrorCodes.ONECENTRAL_USER_INACTIVE,
            OneCentralSdkErrorResponse.builder()
                .oneCentralErrors(
                    List.of(
                        new OneCentralSdkErrorResponse.OneCentralErrorResponseBody(
                            401,
                            "User is inactive in One central system",
                            "User is inactive in One central system")))
                .build());
      }
    } catch (Exception e) {
      throw new AuthenticationServiceException(e.getMessage());
    }

    return auth;
  }

  private UserAuth attemptMigration(
      UserAuth userAuth, Map<String, Object> auth, boolean bearerAuthentication) {
    try {
      var user = userAuth.getUser();
      if (!user.isMigratedOneCentral()) {
        var entitlements = migrateUser(auth, user, bearerAuthentication);
        return new UserAuth(user, entitlements);
      }
    } catch (OneCentralException e) {
      parseUserMigrationErrors(e, userAuth.getUser());
    }
    return userAuth;
  }

  private List<Entitlement> migrateUser(
      Map<String, Object> auth, User user, boolean bearerAuthentication) {
    String s2sAccessToken;
    try {
      s2sAccessToken = getS2SAccessToken();
    } catch (Exception e) {
      throw new AuthenticationServiceException("Token Credentials expired");
    }

    var userDetails =
        (SpringUserDetails)
            userDetailsService.loadUserDetailsBy1CUsername(auth, bearerAuthentication);
    log.debug("userDetails={}", userDetails);
    List<Entitlement> entitlements = userDetails.getEntitlements();
    for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
      ResponseEntity<OneCentralUserRolesResponse> response =
          userAuthorizationService.assignRole(
              s2sAccessToken, grantedAuthority.getAuthority(), user.getOneCentralUserName());
      entitlements =
          Optional.ofNullable(response)
              .map(ResponseEntity::getBody)
              .map(OneCentralUserRolesResponse::getEntitlements)
              .stream()
              .reduce(
                  entitlements,
                  (acc, ele) -> {
                    acc.addAll(ele);
                    return acc;
                  });
      userDetailsService.updateUserMigrated(user.getPid());
    }
    return entitlements;
  }

  private void parseUserMigrationErrors(OneCentralException e, User user) {
    Optional.ofNullable(e)
        .map(OneCentralException::getOneCentralErrorResponse)
        .map(OneCentralSdkErrorResponse::getOneCentralErrors)
        .filter(
            errors ->
                errors.stream()
                    .noneMatch(
                        error ->
                            error
                                .getMessage()
                                .contains("Entity Role Assignment already exists in the system")))
        .ifPresentOrElse(
            response -> {
              throw new OneCentralException(
                  OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR,
                  new OneCentralSdkErrorResponse(response, e.getErrorCode().getHttpStatus()));
            },
            () -> userDetailsService.updateUserMigrated(user.getPid()));
  }

  private String getS2SAccessToken() {
    return Optional.ofNullable(clientProvider)
        .map(Oauth2AuthorizedClientProvider::getAuthorizedClient)
        .map(OAuth2AuthorizedClient::getAccessToken)
        .map(OAuth2AccessToken::getTokenValue)
        .orElseThrow(
            () -> new OneCentralException(OneCentralErrorCodes.ONECENTRAL_UNABLE_TO_GET_TOKEN));
  }

  private HttpServletRequest getRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .map(ServletRequestAttributes.class::cast)
        .map(ServletRequestAttributes::getRequest)
        .orElseThrow(
            () -> new GenevaSecurityException(SecurityErrorCodes.SECURITY_UNKNOWN_FAILURE));
  }
}
