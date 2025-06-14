package com.ssp.geneva.sdk.dv360.seller.repository;

import static java.util.Objects.nonNull;

import com.google.api.services.doubleclickbidmanager.DoubleClickBidManager;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.error.Dv360SellerSdkErrorCodes;
import com.ssp.geneva.sdk.dv360.seller.exception.Dv360SellerSdkException;
import com.ssp.geneva.sdk.dv360.seller.model.Dv360Request;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Log4j2
public abstract class BaseRestRepository {
  private static final String REQUEST_URI = "%s/%s";

  protected final String dv360Endpoint;
  protected final String dv360ExchangeId;
  protected final RestTemplate dv360SellerRestTemplate;
  protected final DoubleClickBidManager doubleClickBidManager;
  protected final GoogleCredentials googleCredentials;

  protected BaseRestRepository(
      final String dv360Endpoint,
      final String dv360ExchangeId,
      final RestTemplate dv360SellerRestTemplate,
      final DoubleClickBidManager doubleClickBidManager,
      final GoogleCredentials googleCredentials) {
    this.dv360Endpoint = dv360Endpoint;
    this.dv360ExchangeId = dv360ExchangeId;
    this.dv360SellerRestTemplate = dv360SellerRestTemplate;
    this.doubleClickBidManager = doubleClickBidManager;
    this.googleCredentials = googleCredentials;
  }

  /**
   * Generate a {@link RequestEntity} object to send to the DV360 service
   *
   * @param name The name of the model object being requested
   * @param httpMethod The {@link HttpMethod} to be used in the request
   * @param body the {@link T} to be sent as the body of the request
   * @return {@link ResponseEntity<T>} containing the requested model object
   */
  <T> RequestEntity<T> generateRequest(String name, HttpMethod httpMethod, T body) {
    log.debug("BaseRepository.generateRequest(): name: {}", name);
    String url = String.format(REQUEST_URI, dv360Endpoint, name);
    log.debug("formatted url: {}", url);
    HttpHeaders httpHeaders = getHeaders();
    return new RequestEntity<>(body, httpHeaders, httpMethod, URI.create(url));
  }

  /**
   * Generate a set of {@link HttpHeaders} including an access token for the DV360 service
   *
   * @return {@link HttpHeaders>} A newly generated set of {@link HttpHeaders}
   */
  HttpHeaders getHeaders() {
    try {
      googleCredentials.refreshIfExpired();
    } catch (IOException e) {
      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_GOOGLE_CREDENTIALS_ERROR);
    }
    AccessToken accessToken = googleCredentials.getAccessToken();
    String accessTokenValue = accessToken.getTokenValue();
    log.debug("access token length: {}", accessTokenValue.length());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue);
    List<MediaType> mediaTypes = new ArrayList<>();
    mediaTypes.add(MediaType.APPLICATION_JSON);
    httpHeaders.setAccept(mediaTypes);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return httpHeaders;
  }

  <T extends Dv360Request> ResponseEntity<T> sendRequest(RequestEntity<T> req, Class<T> type) {
    return sendRequest(req, type, type);
  }

  public <T> ResponseEntity<T> sendRequestWithTypeReference(
      RequestEntity<T> req, ParameterizedTypeReference<T> typeRef) {
    if (req.getMethod() != null) {
      log.debug(
          "BaseRepository.sendRequest():  method: {}: url: {}",
          req.getMethod().toString(),
          req.getUrl().toString());
    }
    ResponseEntity<T> resp;
    try {
      resp = dv360SellerRestTemplate.exchange(req, typeRef);
    } catch (Exception ex) {
      log.warn("Exception while sending request to dv360: {}", ex);

      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR,
          new Object[] {ex.getMessage()});
    }
    return resp;
  }

  /**
   * Send a REST request to the DV360 service
   *
   * @param req The {@link RequestEntity<T>} to send
   * @param inType The {@link Class<T>} type used in the request
   * @param outType the {@link Class<Y>} type to be returned as the response
   * @return {@link ResponseEntity<Y>} containing the requested model object
   */
  <T extends Dv360Request, Y extends Dv360Request> ResponseEntity<Y> sendRequest(
      RequestEntity<T> req, Class<T> inType, Class<Y> outType) {
    log.debug(
        "BaseRepository.sendRequest():  method: {}: url: {}",
        req.getMethod().toString(),
        req.getUrl().toString());
    ResponseEntity<Y> resp = null;
    try {
      resp = dv360SellerRestTemplate.exchange(req, outType);
    } catch (HttpStatusCodeException ex) {
      if (log.isInfoEnabled()) {
        log.info(buildRequestFailureLogMessage(req, resp, ex));
      }
      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR,
          new Object[] {ex.getMessage()},
          ex.getStatusCode());
    } catch (Exception ex) {
      log.debug("Exception while sending request to dv360: {}", ex);
      if (log.isInfoEnabled()) {
        log.info(buildRequestFailureLogMessage(req, resp, ex));
      }
      throw new Dv360SellerSdkException(
          Dv360SellerSdkErrorCodes.DV360_SELLER_SDK_HTTP_CLIENT_ERROR,
          new Object[] {ex.getMessage()});
    }
    return resp;
  }

  <T extends Dv360Request, Y extends Dv360Request> String buildRequestFailureLogMessage(
      RequestEntity<T> req, ResponseEntity<Y> resp, Exception ex) {
    if (nonNull(req) || nonNull(resp) || nonNull(ex)) {
      var buffer = new StringBuilder("Request exception: ");
      if (nonNull(req)) {
        buffer.append(
            String.format(
                "request: { url: '%s', method: '%s', body: '%s' } ",
                req.getUrl(), req.getMethod(), req.getBody()));
      }
      if (nonNull(resp)) {
        buffer.append(
            String.format(
                "response: { status: %d, body: '%s' } ",
                resp.getStatusCodeValue(), resp.getBody()));
      }
      if (nonNull(ex)) {
        buffer.append(
            String.format(
                "exception: { name: '%s', message: '%s' } ",
                ex.getClass().getName(), ex.getMessage()));
      }
      return buffer.toString();
    }
    return "";
  }
}
