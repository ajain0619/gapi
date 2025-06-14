package com.ssp.geneva.sdk.identityb2b.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bErrorCodes;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkErrorResponse;
import com.ssp.geneva.sdk.identityb2b.model.IdentityB2bSdkErrorResponse.IdentityB2bSdkErrorResponseBody;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
public abstract class BaseRestRepository {

  protected final ObjectMapper objectMapper;
  protected final RestTemplate restTemplate;
  protected String baseUrl;

  protected BaseRestRepository(
      final ObjectMapper oneCentralObjectMapper,
      final RestTemplate restTemplate,
      final String baseUrl) {
    this.objectMapper = oneCentralObjectMapper;
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  /**
   * Generic operation to perform REST requests to a service via {@link OAuth2RestTemplate}
   *
   * @param uri URI to connect with.
   * @param method REST operation, {@link HttpMethod}
   * @param requestEntity Request body
   * @param responseType Expected casting for respose
   * @param <T> Class wrapped by {@link HttpEntity}
   * @param <U> Class generics used for response mapping purpose.
   * @return Generic response based on params.
   */
  public <T, U> ResponseEntity<U> makeRequest(
      String uri, HttpMethod method, HttpEntity<T> requestEntity, Class<U> responseType)
      throws IdentityB2bSdkException {
    try {
      return restTemplate.exchange(uri, method, requestEntity, responseType);
    } catch (HttpStatusCodeException e) {
      IdentityB2bSdkErrorResponse identityB2bSdkErrorResponse = parseInternalError(e);
      log.error("Response with errors={}", identityB2bSdkErrorResponse);
      throw new IdentityB2bSdkException(IdentityB2bErrorCodes.IDENTITY_B2B_NOT_AUTHORIZED);
    } catch (Exception e) {
      log.error("The operation is failed :{}", e.getMessage());
      throw new IdentityB2bSdkException(IdentityB2bErrorCodes.IDENTITY_B2B_NOT_AUTHORIZED);
    }
  }

  private IdentityB2bSdkErrorResponse parseInternalError(HttpStatusCodeException errorRes) {
    String response = errorRes.getResponseBodyAsString();
    try {
      List<IdentityB2bSdkErrorResponseBody> errors =
          objectMapper.readValue(response, new TypeReference<>() {});
      return IdentityB2bSdkErrorResponse.builder().identityB2bErrors(errors).build();
    } catch (IOException e) {
      log.error("The IO operation failed :{}", errorRes.getMessage());
    }
    return IdentityB2bSdkErrorResponse.builder().build();
  }

  protected HttpHeaders buildRequestHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    List<MediaType> accept = new ArrayList<>();
    accept.add(MediaType.APPLICATION_JSON);
    headers.setAccept(accept);
    return headers;
  }

  protected HttpHeaders buildRequestHeaders(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(
        HttpHeaders.AUTHORIZATION,
        String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
    return headers;
  }
}
