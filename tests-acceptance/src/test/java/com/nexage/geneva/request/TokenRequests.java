package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenRequests {

  @Autowired private Request request;

  public Request getTokensFiltered(boolean redirect) {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/tokens?qf=" + RequestParams.QF + "&qt=" + RequestParams.QT)
        .setFollowRedirects(redirect);
  }
}
