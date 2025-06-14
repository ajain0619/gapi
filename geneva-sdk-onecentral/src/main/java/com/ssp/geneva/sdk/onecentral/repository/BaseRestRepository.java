package com.ssp.geneva.sdk.onecentral.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkErrorResponse.OneCentralErrorResponseBody;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
   * Generic operation to perform REST requests to a service via {@link RestTemplate}
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
      throws OneCentralException {

    return makeRequest(null, uri, method, requestEntity, responseType);
  }

  /**
   * Generic operation to perform REST requests to a service via {@link RestTemplate}. This helps
   * control the encoding state of the query parameters in the URI: they will be encoded when built
   * into the URI with no further intervention from the framework.
   *
   * @param uri URI to connect with - already built with query params
   * @param method REST operation, {@link HttpMethod}
   * @param requestEntity request body
   * @param responseType expected casting for response
   * @param <T> class wrapped by {@link HttpEntity}
   * @param <U> class used for response mapping
   * @return the response of the integration call if successful
   * @throws OneCentralException for error responses or failures calling the integration
   */
  public <T, U> ResponseEntity<U> makeRequest(
      URI uri, HttpMethod method, HttpEntity<T> requestEntity, Class<U> responseType)
      throws OneCentralException {

    return makeRequest(uri, null, method, requestEntity, responseType);
  }

  private <T, U> ResponseEntity<U> makeRequest(
      URI uri,
      String uriAsString,
      HttpMethod method,
      HttpEntity<T> requestEntity,
      Class<U> responseType)
      throws OneCentralException {

    var requestUrl =
        MessageFormat.format("{0} - {1}", method.name(), uri != null ? uri : uriAsString);
    try {
      log.debug("Execute request: {}", requestUrl);

      if (uri != null) {
        return restTemplate.exchange(uri, method, requestEntity, responseType);
      }
      return restTemplate.exchange(uriAsString, method, requestEntity, responseType);
    } catch (HttpStatusCodeException e) {
      log.error("Failed to execute request: {}. Reason: {}", requestUrl, e.getMessage(), e);
      OneCentralSdkErrorResponse oneCentralErrorResponse = parseInternalError(e);
      log.error("Response with errors={}", oneCentralErrorResponse);
      throw new OneCentralException(
          OneCentralErrorCodes.ONECENTRAL_CONSTRAINT_VIOLATION, oneCentralErrorResponse);
    } catch (Exception e) {
      log.error("Failed to execute request: {}. Reason: {}", requestUrl, e.getMessage(), e);
      throw new OneCentralException(OneCentralErrorCodes.ONECENTRAL_INTERNAL_ERROR);
    }
  }

  protected HttpHeaders buildRequestHeaders(String accessToken) {
    if (accessToken == null || accessToken.isEmpty()) {
      log.error("Invalid Access Token parameter. It cannot be null or empty.");
      throw new OneCentralException(OneCentralErrorCodes.ONECENTRAL_UNABLE_TO_GET_TOKEN);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(
        HttpHeaders.AUTHORIZATION,
        String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken));
    return headers;
  }

  private OneCentralSdkErrorResponse parseInternalError(HttpStatusCodeException errorRes) {
    OneCentralSdkErrorResponse.OneCentralSdkErrorResponseBuilder builder =
        OneCentralSdkErrorResponse.builder();
    builder.httpStatus(errorRes.getStatusCode());
    var response = errorRes.getResponseBodyAsString();
    try {
      List<OneCentralErrorResponseBody> errors =
          objectMapper.readValue(response, new TypeReference<>() {});
      return builder.oneCentralErrors(errors).build();
    } catch (IOException e) {
      log.error("The operation failed: {}", errorRes.getMessage());
    }
    return builder.build();
  }
}
