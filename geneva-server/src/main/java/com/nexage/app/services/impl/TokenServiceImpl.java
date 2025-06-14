package com.nexage.app.services.impl;

import com.google.common.base.Strings;
import com.nexage.app.dto.AccessTokenDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.TokenService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.sdk.identityb2b.IdentityB2bSdkClient;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.identityb2b.model.B2bAccessToken;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Setter
@Service
@Log4j2
public class TokenServiceImpl implements TokenService {

  protected final OAuth2RestTemplate restTemplate;

  private final IdentityB2bSdkClient identityB2bSdkClient;

  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  @Autowired
  public TokenServiceImpl(
      @Qualifier("restTemplate") OAuth2RestTemplate restTemplate,
      IdentityB2bSdkClient identityB2bSdkClient,
      OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {
    this.restTemplate = restTemplate;
    this.identityB2bSdkClient = identityB2bSdkClient;
    this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
  }

  @Override
  public AccessTokenDTO getToken(String qt, Set<String> qf) {
    HttpServletRequest request = null;
    var servletRequestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (servletRequestAttributes != null) {
      request = servletRequestAttributes.getRequest();
    }
    validateSearchParamRequest(qf, qt);
    validateAuthContext(request);
    var client = oAuth2AuthorizedClientRepository.loadAuthorizedClient("b2b", null, request);
    String refreshToken;
    OAuth2RefreshToken refreshTokenObj;
    if (client != null && (refreshTokenObj = client.getRefreshToken()) != null) {
      refreshToken = refreshTokenObj.getTokenValue();
    } else {
      refreshToken =
          restTemplate.getOAuth2ClientContext().getAccessToken().getRefreshToken().getValue();
    }

    return getToken(() -> getAccessTokenByRefreshToken(refreshToken));
  }

  private ResponseEntity<B2bAccessToken> getAccessTokenByRefreshToken(String refreshToken) {
    try {

      return identityB2bSdkClient
          .getAccessTokenRepository()
          .getAccessTokenByRefreshToken(refreshToken);
    } catch (IdentityB2bSdkException e) {
      log.error("A B2B exception occurred {} {} {}", e.getClass(), e.getMessage(), e.toString());
      throw new GenevaException(ServerErrorCodes.SERVER_B2B_ERROR);
    }
  }

  private AccessTokenDTO getToken(Supplier<ResponseEntity<B2bAccessToken>> accessTokenSupplier) {
    var accessTokenDTO = new AccessTokenDTO();

    try {
      ResponseEntity<B2bAccessToken> response = accessTokenSupplier.get();

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        accessTokenDTO.setAccessToken(response.getBody().getAccess_token());
        accessTokenDTO.setExpiresIn(Long.valueOf(response.getBody().getExpires_in()));
        accessTokenDTO.setTokenType(response.getBody().getToken_type());
      }
    } catch (RestClientException e) {
      log.error(
          "Could not talk to B2B to get Access Token from Refresh Token {} {}",
          e.getClass(),
          e.getMessage());
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_UNABLE_TO_GET_TOKEN);
    } catch (IdentityB2bSdkException e) {
      log.error("A B2B exception occurred {} {} {}", e.getClass(), e.getMessage(), e.toString());
      throw new GenevaException(ServerErrorCodes.SERVER_B2B_ERROR);
    } catch (GenevaException e) {
      log.error(
          "A B2B exception occurred when getting access token from refresh token {} {} {}",
          e.getClass(),
          e.getMessage(),
          e.toString());
      throw new GenevaException(ServerErrorCodes.SERVER_B2B_ERROR);
    } catch (Exception e) {
      log.error(
          "Something unexpected occurred when getting Access Token from Refresh Token {} {} {}",
          e.getClass(),
          e.getMessage(),
          e.toString());
      throw new GenevaException(ServerErrorCodes.SERVER_TOKEN_INTERNAL_ERROR);
    }

    return accessTokenDTO;
  }

  private void validateAuthContext(HttpServletRequest request) {
    if (oAuth2AuthorizedClientRepository.loadAuthorizedClient("b2b", null, request) != null) {
      return;
    }
    if (Objects.isNull(restTemplate.getOAuth2ClientContext())
        || Objects.isNull(restTemplate.getOAuth2ClientContext().getAccessToken())
        || Objects.isNull(
            restTemplate.getOAuth2ClientContext().getAccessToken().getRefreshToken())) {
      log.error("Unable to get Refresh token from context");
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }

  private void validateSearchParamRequest(Set<String> qf, String qt) {
    if (CollectionUtils.isEmpty(qf) || Strings.isNullOrEmpty(qt))
      throw new GenevaValidationException(CommonErrorCodes.COMMON_BAD_REQUEST);
  }
}
