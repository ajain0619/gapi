package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DspRequests {

  @Autowired Request request;

  public Request getPageOfDSPs() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format("/v1/dsps?page=%s&size=%s", RequestParams.PAGE, RequestParams.SIZE));
  }

  public Request getPageOfDSPSummaries() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(
            String.format(
                "/v1/dsps/summary?page=%s&size=%s", RequestParams.PAGE, RequestParams.SIZE));
  }

  public Request getDSPByName() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern(String.format("/v1/dsps?qf=name&qt=%s", RequestParams.QT));
  }
}
