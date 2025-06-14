package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StaticRequests {

  @Autowired private Request request;

  public Request getStaticResourceRequest(final String fileName) {
    request.clear().setGetStrategy().setUrlPattern("/static/" + fileName);
    return request.setGetStrategy();
  }
}
