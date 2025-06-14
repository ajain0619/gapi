package com.ssp.geneva.sdk.xandr.repository;

import static java.util.Objects.nonNull;

import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import com.ssp.geneva.sdk.xandr.model.XandrRequest;
import java.net.URI;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Log4j2
public abstract class BaseRestRepository {
  private static final String REQUEST_URI = "%s/%s";

  protected final String xandrEndpoint;
  protected final RestTemplate xandrRestTemplate;

  protected BaseRestRepository(final String xandrEndpoint, final RestTemplate xandrRestTemplate) {
    this.xandrEndpoint = xandrEndpoint;
    this.xandrRestTemplate = xandrRestTemplate;
  }

  /**
   * Generate a {@link RequestEntity} object to send to the Xandr service
   *
   * @param name The name of the model object being requested
   * @param httpMethod The {@link HttpMethod} to be used in the request
   * @param body the {@link T} to be sent as the body of the request
   * @return {@link ResponseEntity<T>} containing the requested model object
   */
  <T> RequestEntity<T> generateRequest(
      String name, HttpMethod httpMethod, T body, HttpHeaders httpHeaders) {
    log.debug("BaseRepository.generateRequest(): name: {}", name);
    String url = String.format(REQUEST_URI, xandrEndpoint, name);
    log.debug("formatted url: {}", url);
    return new RequestEntity<>(body, httpHeaders, httpMethod, URI.create(url));
  }

  <T> RequestEntity<T> generateRequest(String name, HttpMethod httpMethod, T body) {
    return generateRequest(name, httpMethod, body, new HttpHeaders());
  }

  /**
   * Send a REST request to the Xandr service
   *
   * @param req The {@link RequestEntity<T>} to send
   * @param inType The {@link Class<T>} type used in the request
   * @param outType the {@link Class<Y>} type to be returned as the response
   * @return {@link ResponseEntity<Y>} containing the requested model object
   */
  <T extends XandrRequest, Y extends XandrRequest> ResponseEntity<Y> sendRequest(
      RequestEntity<T> req, Class<T> inType, Class<Y> outType) {

    log.debug(
        "BaseRepository.sendRequest():  method: {}: url: {} body: {}",
        Objects.toString(req.getMethod(), null),
        req.getUrl().toString(),
        Objects.toString(req.getBody(), null));

    ResponseEntity<Y> resp = null;
    try {
      resp = xandrRestTemplate.exchange(req, outType);
    } catch (HttpStatusCodeException ex) {
      if (log.isInfoEnabled()) {
        log.info(buildRequestFailureLogMessage(req, resp, ex));
      }
      throw new XandrSdkException(
          XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR,
          new String[] {ex.getMessage()},
          ex.getStatusCode());
    } catch (Exception ex) {
      log.debug("Exception while sending request to Xandr: {}", ex);
      if (log.isInfoEnabled()) {
        log.info(buildRequestFailureLogMessage(req, resp, ex));
      }
      throw new XandrSdkException(
          XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, new String[] {ex.getMessage()});
    }
    return resp;
  }

  <T extends XandrRequest, Y extends XandrRequest> String buildRequestFailureLogMessage(
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
