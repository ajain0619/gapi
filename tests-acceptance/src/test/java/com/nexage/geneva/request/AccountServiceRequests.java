package com.nexage.geneva.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceRequests {

  @Autowired private Request request;

  public Request getLoginRequestSSO() {
    return request.clear().setGetStrategy().setUrlPattern("/oauth2/authorization/b2b");
  }

  public Request getGetCurrentUserRequest(boolean redirectPolicy) {
    return request
        .clear()
        .setGetStrategy()
        .setUrlPattern("/v1/users?qf=onlyCurrent&qt=true")
        .setFollowRedirects(redirectPolicy);
  }

  public Request getLogoutRequest() {
    return request.clear().setGetStrategy().setUrlPattern("/logout");
  }
}
