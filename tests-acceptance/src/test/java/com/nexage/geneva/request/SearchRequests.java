package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchRequests {

  @Autowired private Request request;

  public Request getRequestWithQf() {
    String urlPattern = "/search-tests?qf={qf}";
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(urlPattern);
  }

  public Request getRequestWithQfAndQo() {
    String urlPattern = "/search-tests?qf={qf}&qo={qo}";
    return request
        .clear()
        .disableOptionalQueryParamsRemoval()
        .setGetStrategy()
        .setUrlPattern(urlPattern);
  }
}
