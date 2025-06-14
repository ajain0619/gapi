package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntitlementsRequest {
  @Autowired private Request request;

  public Request getUserEntitlementsFiltered() {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/entitlements?qf=" + RequestParams.QF + "&qt=" + RequestParams.QT);
  }
}
