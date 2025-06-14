package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityRequests {

  @Autowired private Request request;

  public Request getRequest(String url) {
    String urlPattern = "/security-tests/" + url + "/{publisherPid}";
    return request.clear().setGetStrategy().setUrlPattern(urlPattern);
  }
}
